package com.markmurfin.chat;

import java.util.Objects;

@FunctionalInterface
public interface WatchChannelUseCase
{
	Response watchChannel(final Request request);

	class Request
	{
		public final String channelName;
		public final String userId;

		public Request(final String channelName, final String userId)
		{
			this.channelName = Objects.requireNonNull(channelName, "channelName");
			this.userId = Objects.requireNonNull(userId, "userId");
		}
	}

	class Response
	{
		public final String userId;
		public final Channel channel;

		public Response(final String userId, final Channel channel)
		{
			this.userId = Objects.requireNonNull(userId, "userId");
			this.channel = Objects.requireNonNull(channel, "channel");
		}
	}

}
