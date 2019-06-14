package cli;

import com.markmurfin.chat.*;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryRepo implements CreateChannelStrategy.Repository, ListWatchedChannelsStrategy.Repository, WatchChannelStrategy.Repository, SendMessageStrategy.Repository, LeaveChannelStrategy.Repository
{
	@Override
	public boolean channelNameExists(final String name)
	{
		return map.containsKey(keyize(name));
	}

	@Override
	public Channel createChannelWithName(final String name, final String ownerId)
	{
		final String id = keyize(name);
		map.put(id, new InMemChannel(name));
		watched.computeIfAbsent(ownerId, k -> new HashSet<>()).add(name);
		return new Channel(id, name);
	}

	@Override
	public MessageStream watchChannelAndRetrieveMessageStream(final String channelName, final String userId)
	{
		final String id = keyize(channelName);
		final InMemChannel imc = map.get(id);
		if(imc == null)
			throw new RuntimeException(String.format("Channel does not exist: %s", channelName));
		watched.computeIfAbsent(userId, k -> new HashSet<>()).add(id);
		final Channel channel = new Channel(id, channelName);
		return new MessageStream(channel, imc.stream(), 0);
	}

	@Override
	public List<Channel> retrieveWatchedChannelsForUser(final String userId)
	{
		return watched.getOrDefault(userId, Set.of()).stream().map(s -> new Channel(keyize(s), s)).collect(Collectors.toList());
	}

	@Override
	public Optional<Channel> leaveChannel(final String userId, final String channelName)
	{
		final String id = keyize(channelName);
		if (watched.containsKey(userId))
			watched.get(userId).remove(id);

		final Optional<Channel> o = Optional.ofNullable(map.get(id)).map(c -> new Channel(id, c.name));

		return o;
	}

	@Override
	public boolean isUserWatchingChannel(final String channelId, final String userId)
	{
		return watched.getOrDefault(userId, Set.of()).contains(channelId);
	}

	@Override
	public void persistMessage(final String channelId, final Message message)
	{
		map.get(channelId).q.add(message);
	}

	private String keyize(final String name)
	{
		return map.entrySet().stream()
			.filter(e -> e.getValue().name.equalsIgnoreCase(name))
			.map(Map.Entry::getKey)
			.findAny()
			.orElseGet(() -> String.valueOf(counter++));
	}

	private void clean()
	{
		map.keySet().removeIf(Predicate.not(watched.values().stream().flatMap(Set::stream).distinct().collect(Collectors.toSet())::contains));
	}

	private static class InMemChannel
	{
		final String name;
		private final LinkedBlockingQueue<Message> q;

		InMemChannel(final String name)
		{
			this.name = Objects.requireNonNull(name,"name");
			this.q = new LinkedBlockingQueue<>();
		}

		Stream<Message> stream()
		{
			return Stream.generate(this::take);
		}

		Message take()
		{
			try
			{
				return q.take();
			}
			catch(final InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	private int counter = 0;
	public final Map<String, Set<String>> watched = new HashMap<>();
	public final Map<String, InMemChannel> map = new HashMap<>();
}
