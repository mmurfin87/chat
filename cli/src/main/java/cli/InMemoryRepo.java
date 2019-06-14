package cli;

import com.markmurfin.chat.*;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryRepo implements CreateChannelStrategy.Repository, ExploreChannelsStrategy.Repository, ListWatchedChannelsStrategy.Repository, WatchChannelStrategy.Repository, SendMessageStrategy.Repository, LeaveChannelStrategy.Repository, ConnectChannelStrategy.Repository
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
		final Channel channel = new Channel(id, name);
		map.put(id, new InMemChannel(channel));
		return channel;
	}

	@Override
	public List<Channel> seekChannels(final String afterName, final long count)
	{
		return map.values().stream()
			.map(imc -> imc.channel)
			.sorted(Comparator.comparing(c -> c.name))
			.dropWhile(c -> afterName != null && c.name.compareTo(afterName) < 1)
			.limit(count)
			.collect(Collectors.toList());
	}

	@Override
	public MessageStream connectChannel(String channelId)
	{
		final InMemChannel imc = map.get(channelId);
		if(imc == null)
			throw new RuntimeException(String.format("Channel id does not exist: %s", channelId));
		return new MessageStream(imc.channel, imc.stream(), 0);
	}

	@Override
	public Channel watchChannel(final String channelName, final String userId)
	{
		final String id = keyize(channelName);
		final InMemChannel imc = map.get(id);
		if(imc == null)
			throw new RuntimeException(String.format("Channel does not exist: %s", channelName));
		watched.computeIfAbsent(userId, k -> new HashSet<>()).add(id);
		return imc.channel;
	}

	@Override
	public List<Channel> retrieveWatchedChannelsForUser(final String userId)
	{
		return watched.getOrDefault(userId, Set.of()).stream().map(map::get).map(imc -> imc.channel).collect(Collectors.toList());
	}

	@Override
	public Optional<Channel> leaveChannel(final String userId, final String channelName)
	{
		final String id = keyize(channelName);
		if (watched.containsKey(userId))
			watched.get(userId).remove(id);

		final Optional<Channel> o = Optional.ofNullable(map.get(id)).map(c -> c.channel);

		return o;
	}

	@Override
	public void persistMessage(final String channelId, final Message message)
	{
		map.get(channelId).q.add(message);
	}

	private String keyize(final String name)
	{
		return map.entrySet().stream()
			.filter(e -> e.getValue().channel.name.equalsIgnoreCase(name))
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
		final Channel channel;
		private final LinkedBlockingQueue<Message> q;

		InMemChannel(final Channel channel)
		{
			this.channel = Objects.requireNonNull(channel,"channel");
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
