package cli;

import com.markmurfin.chat.*;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CliPresenter implements AutoCloseable
{
	public void createChannel(CreateChannelUseCase.Response response)
	{
		output("CREATED: %s", response.channel.name);
	}

	public void exploreChannels(ExploreChannelsUseCase.Response response)
	{
		output("--- EXPLORE%s ---", response.afterName.map(s -> String.format(" after \"%s\" :", s)).orElse(":"));
		response.channels.forEach(c -> output("\t%s", c.name));
	}

	public void listWatchedChannels(ListWatchedChannelsUseCase.Response response)
	{
		output("--- WATCHING ---");
		response.channels.forEach(c -> output("\t%s", c.name));
	}

	public void watchChannel(WatchChannelUseCase.Response response)
	{
		output("WATCHING: %s", response.channel.name);
	}

	public void connectChannel(ConnectChannelUseCase.Response response)
	{
		connectChannelStream(response.messageStream);
	}


	public void sendMessage(SendMessageUseCase.Response response)
	{
		//output("SENT");
	}

	public void leaveChannel(LeaveChannelUseCase.Response response)
	{
		output("UNWATCHED: %s", response.channel.name);
	}

	private void connectChannelStream(final MessageStream messageStream)
	{
		leaveAllChannels();
		output("CONNECTED: %s", messageStream.channel.name);
		final Future<?> f = es.submit(() -> messageStream.messages.forEach(m -> output("[%s] (%s): %s", messageStream.channel.name, m.userId, m.content)));
		lastConnected = new ConnectedChannelStream(messageStream.channel, f);
		//joined.put(messageStream.channel.id, f);
	}

	private void output(String format, Object... replacements)
	{
		System.out.format(format+System.lineSeparator(), replacements);
	}

	private void leaveAllChannels()
	{
		if (lastConnected == null)
			return;
		lastConnected.future.cancel(true);
		output("LEFT: %s", lastConnected.channel.name);
		lastConnected = null;
		//joined.values().forEach(f -> f.cancel(true));
		//joined.clear();
	}

	@Override
	public void close() throws Exception
	{
		leaveAllChannels();
		es.shutdown();
		es.awaitTermination(1, TimeUnit.SECONDS);
	}

	private static class ConnectedChannelStream
	{
		public final Channel channel;
		public final Future<?> future;

		public ConnectedChannelStream(final Channel channel, final Future<?> future)
		{
			this.channel = Objects.requireNonNull(channel, "channel");
			this.future = Objects.requireNonNull(future, "future");
		}
	}

	private ConnectedChannelStream lastConnected = null;
	private final ExecutorService es = Executors.newCachedThreadPool();
	//private final Map<String, Future<?>> joined = new HashMap<>();
}
