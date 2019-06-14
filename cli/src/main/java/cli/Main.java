package cli;

import com.markmurfin.chat.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Main
{
	public static void main(final String[] args) throws Exception
	{
		System.out.println("Hello, world!");

		try (final CliPresenter p = new CliPresenter())
		{
			final GeneralPresenter gp = new GeneralPresenter();
			final InMemoryRepo r = new InMemoryRepo();
			final CreateChannelUseCase createChannel = gp.prepare(CreateChannelUseCase.class, new CreateChannelStrategy(r), CreateChannelUseCase.Response.class, p::createChannel);
			final ListWatchedChannelsUseCase listWatchedChannels = gp.prepare(ListWatchedChannelsUseCase.class, new ListWatchedChannelsStrategy(r), ListWatchedChannelsUseCase.Response.class, p::listWatchedChannels);
			final WatchChannelUseCase joinChannel = gp.prepare(WatchChannelUseCase.class, new WatchChannelStrategy(r), WatchChannelUseCase.Response.class, p::watchChannel);
			final LeaveChannelUseCase leaveChannel = gp.prepare(LeaveChannelUseCase.class, new LeaveChannelStrategy(r), LeaveChannelUseCase.Response.class, p::leaveChannel);
			final SendMessageUseCase sendMessage = gp.prepare(SendMessageUseCase.class, new SendMessageStrategy(r), SendMessageUseCase.Response.class, p::sendMessage);
			//final CreateChannelUseCase createChannel = new CreateChannelStrategy(r);
			//final ListWatchedChannelsUseCase listWatchedChannels = new ListWatchedChannelsStrategy(r);
			//final WatchChannelUseCase joinChannel = new WatchChannelStrategy(r);
			//final LeaveChannelUseCase leaveChannel = new LeaveChannelStrategy(r);
			//final SendMessageUseCase sendMessage = new SendMessageStrategy(r);

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line = null;
			String userId = null;
			final Set<Channel> activeChannels = new HashSet<>();
			while ((line = br.readLine()) != null && !"/quit".equalsIgnoreCase(line))
			{
				if (userId == null)
				{
					if(!line.startsWith("/login "))
						System.out.println("You must first /login <userId>");
					else
					{
						userId = line.substring(7);
						System.out.println("LOGIN: " + userId);
					}
					continue;
				}

				if (line.startsWith("/"))
				{
					if(line.startsWith("/create "))
					{
						final String name = line.substring(8);
						final CreateChannelUseCase.Response response = createChannel.createChannel(new CreateChannelUseCase.Request(name, userId));
						//p.createChannel(response);
					}
					else if (line.startsWith("/watch "))
					{
						//if (currentChannel != null)
						//{
						//	final LeaveChannelUseCase.Response response = leaveChannel.leaveChannel(new LeaveChannelUseCase.Request(userId, currentChannel));

						//}
						final String name = line.substring(7);
						final WatchChannelUseCase.Response response = joinChannel.watchChannel(new WatchChannelUseCase.Request(name, userId));
						//p.watchChannel(response);
						activeChannels.add(response.messageStream.channel);
					}
					else if (line.startsWith("/leave "))
					{
						final String name = line.substring(7);
						final LeaveChannelUseCase.Response response = leaveChannel.leaveChannel(new LeaveChannelUseCase.Request(userId, name));
						//p.leaveChannel(response);
						activeChannels.remove(response.channel);
					}
					else if (line.equalsIgnoreCase("/list"))
					{
						ListWatchedChannelsUseCase.Response response = listWatchedChannels.listWatchedChannels(new ListWatchedChannelsUseCase.Request(userId));
						//p.listWatchedChannels(response);
					}
					else if (line.equalsIgnoreCase("/threads"))
					{
						System.out.println("Futures:" + r.map.values().size());
					}
					else
						System.out.println("Invalid command!");
				}
				else if (activeChannels.isEmpty())
				{
					System.out.println("You are not watching a channel. Before sending a message, you must /watch <channel>");
				}
				else
				{
					final String currentChannel = activeChannels.iterator().next().id;
					final SendMessageUseCase.Response response = sendMessage.sendMessage(new SendMessageUseCase.Request(userId, currentChannel, line));
					//p.sendMessage(response);
				}
			}
		}

	}
}
