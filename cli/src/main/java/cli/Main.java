package cli;

import com.markmurfin.chat.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Main
{
	public static void main(final String[] args) throws Exception
	{
		System.out.println("Hello, world!");

		try (final CliPresenter p = new CliPresenter())
		{
			final InMemoryRepo r = new InMemoryRepo();
			final GeneralPresenter gp = new GeneralPresenter();
			gp.registerAllPublicSingleParameterMethods(p);

			final CreateChannelUseCase createChannel = gp.proxy(CreateChannelUseCase.class, new CreateChannelStrategy(r));
			final ExploreChannelsUseCase exploreChannelsUseCase = gp.proxy(ExploreChannelsUseCase.class, new ExploreChannelsStrategy(r));
			final ConnectChannelUseCase connectChannel = gp.proxy(ConnectChannelUseCase.class, new ConnectChannelStrategy(r));
			final ListWatchedChannelsUseCase listWatchedChannels = gp.proxy(ListWatchedChannelsUseCase.class, new ListWatchedChannelsStrategy(r));
			final WatchChannelUseCase joinChannel = gp.proxy(WatchChannelUseCase.class, new WatchChannelStrategy(r));
			final LeaveChannelUseCase leaveChannel = gp.proxy(LeaveChannelUseCase.class, new LeaveChannelStrategy(r));
			final SendMessageUseCase sendMessage = gp.proxy(SendMessageUseCase.class, new SendMessageStrategy(r));

			//final CreateChannelUseCase createChannel = gp.prepare(CreateChannelUseCase.class, new CreateChannelStrategy(r), CreateChannelUseCase.Response.class, p::createChannel);
			//final ListWatchedChannelsUseCase listWatchedChannels = gp.prepare(ListWatchedChannelsUseCase.class, new ListWatchedChannelsStrategy(r), ListWatchedChannelsUseCase.Response.class, p::listWatchedChannels);
			//final WatchChannelUseCase joinChannel = gp.prepare(WatchChannelUseCase.class, new WatchChannelStrategy(r), WatchChannelUseCase.Response.class, p::watchChannel);
			//final LeaveChannelUseCase leaveChannel = gp.prepare(LeaveChannelUseCase.class, new LeaveChannelStrategy(r), LeaveChannelUseCase.Response.class, p::leaveChannel);
			//final SendMessageUseCase sendMessage = gp.prepare(SendMessageUseCase.class, new SendMessageStrategy(r), SendMessageUseCase.Response.class, p::sendMessage);

			//final CreateChannelUseCase createChannel = new CreateChannelStrategy(r);
			//final ListWatchedChannelsUseCase listWatchedChannels = new ListWatchedChannelsStrategy(r);
			//final WatchChannelUseCase joinChannel = new WatchChannelStrategy(r);
			//final LeaveChannelUseCase leaveChannel = new LeaveChannelStrategy(r);
			//final SendMessageUseCase sendMessage = new SendMessageStrategy(r);

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line = null;
			String userId = null;
			String currentChannel = null;
			while ((line = br.readLine()) != null && !"/quit".equalsIgnoreCase(line))
			{
				line = line.trim();
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
					else if (line.equalsIgnoreCase("/explore"))
					{
						final ExploreChannelsUseCase.Response response = exploreChannelsUseCase.exploreChannels(new ExploreChannelsUseCase.Request(10, Optional.empty()));
					}
					else if (line.startsWith("/join "))
					{
						final String name = line.substring(6);
						gp.ignoreNextPresentation();
						final ExploreChannelsUseCase.Response ecr = exploreChannelsUseCase.exploreChannels(new ExploreChannelsUseCase.Request(Integer.MAX_VALUE, Optional.empty()));
						final ConnectChannelUseCase.Response response = ecr.channels.stream()
							.filter(c -> c.name.equalsIgnoreCase(name))
							.findAny()
							.map(c -> new ConnectChannelUseCase.Request(c.id))
							.map(connectChannel::connectChannel)
							.orElse(null);
						if (response == null)
							System.out.println("INVALID CHANNEL");
						else
							currentChannel = response.messageStream.channel.id;
					}
					else if (line.startsWith("/watch "))
					{
						final String name = line.substring(7);
						final WatchChannelUseCase.Response response = joinChannel.watchChannel(new WatchChannelUseCase.Request(name, userId));
						//p.watchChannel(response);
					}
					else if (line.startsWith("/leave "))
					{
						final String name = line.substring(7);
						final LeaveChannelUseCase.Response response = leaveChannel.leaveChannel(new LeaveChannelUseCase.Request(userId, name));
						//p.leaveChannel(response);
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
				else if (currentChannel == null)
				{
					System.out.println("You are not watching a channel. Before sending a message, you must /join <channel>");
				}
				else
				{
					final SendMessageUseCase.Response response = sendMessage.sendMessage(new SendMessageUseCase.Request(userId, currentChannel, line));
					//p.sendMessage(response);
				}
			}
		}

	}
}
