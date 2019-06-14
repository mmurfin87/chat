package com.markmurfin.chat;

import java.util.Objects;

@FunctionalInterface
public interface CreateChannelUseCase
{
	Response createChannel(Request request);

	class Request
	{
		public final String name;
		public final String ownerId;

		public Request(final String name, final String ownerId)
		{
			this.name = Objects.requireNonNull(name, "name");
			this.ownerId = Objects.requireNonNull(ownerId, "ownerId");
		}
	}

	class Response
	{
		public final Channel channel;

		public Response(final Channel channel)
		{
			this.channel = Objects.requireNonNull(channel, "channel");
		}
	}
}
