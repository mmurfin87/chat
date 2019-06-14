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
		public final MessageStream messageStream;

		public Response(final MessageStream messageStream)
		{
			this.messageStream = Objects.requireNonNull(messageStream, "messageStream");
		}
	}

}
