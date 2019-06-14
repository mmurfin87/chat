package com.markmurfin.chat;

import java.util.List;
import java.util.Objects;

@FunctionalInterface
public interface ListWatchedChannelsUseCase
{
	Response listWatchedChannels(final Request request);

	class Request
	{
		public final String userId;

		public Request(final String userId)
		{
			this.userId = Objects.requireNonNull(userId, "userId");
		}
	}

	class Response
	{
		public final String userId;
		public final List<Channel> channels;

		public Response(final String userId, List<Channel> channels)
		{
			this.userId = Objects.requireNonNull(userId, "userId");
			this.channels = List.copyOf(Objects.requireNonNull(channels, "channels"));
		}
	}

}
