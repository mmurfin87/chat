package cli;

import com.markmurfin.chat.*;

import java.util.HashMap;
import java.util.Map;
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

	public void listWatchedChannels(ListWatchedChannelsUseCase.Response response)
	{
		output("--- CHANNEL LIST ---");
		response.channels.forEach(c -> output("\t%s", c.name));
	}

	public void watchChannel(WatchChannelUseCase.Response response)
	{
		watchChannel(response.messageStream);
	}

	public void sendMessage(SendMessageUseCase.Response response)
	{
		//output("SENT");
	}

	public void leaveChannel(LeaveChannelUseCase.Response response)
	{
		joined.remove(response.channel.id).cancel(true);
		output("LEFT: %s", response.channel.name);
	}

	private void watchChannel(final MessageStream messageStream)
	{
		if (joined.containsKey(messageStream.channel.id))
			return;
		output("WATCHING: %s", messageStream.channel.name);
		final Future<?> f = es.submit(() -> messageStream.messages.forEach(m -> output("[%s] (%s): %s", messageStream.channel.name, m.userId, m.content)));
		joined.put(messageStream.channel.id, f);
	}

	private void output(String format, Object... replacements)
	{
		System.out.format(format+System.lineSeparator(), replacements);
	}

	@Override
	public void close() throws Exception
	{
		joined.values().forEach(f -> f.cancel(true));
		joined.clear();
		es.shutdown();
		es.awaitTermination(1, TimeUnit.SECONDS);
	}

	private final ExecutorService es = Executors.newCachedThreadPool();
	private final Map<String, Future<?>> joined = new HashMap<>();
}
