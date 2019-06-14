package com.markmurfin.chat;

import java.util.Objects;

@FunctionalInterface
public interface ConnectChannelUseCase
{
	Response connectChannel(Request request);

	class Request
	{
		public final String channelId;

		public Request(final String channelId)
		{
			this.channelId = Objects.requireNonNull(channelId, "channelId");
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
