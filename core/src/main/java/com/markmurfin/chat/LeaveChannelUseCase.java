package com.markmurfin.chat;

import java.util.Objects;

@FunctionalInterface
public interface LeaveChannelUseCase
{
	Response leaveChannel(final Request request);

	class Request
	{
		public final String userId;
		public final String channelName;

		public Request(final String userId, final String channelName)
		{
			this.userId = Objects.requireNonNull(userId, "userId");
			this.channelName = Objects.requireNonNull(channelName, "channelName");
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
