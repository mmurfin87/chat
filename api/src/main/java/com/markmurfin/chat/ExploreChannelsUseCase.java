package com.markmurfin.chat;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@FunctionalInterface
public interface ExploreChannelsUseCase
{
	Response exploreChannels(Request request);

	class Request
	{
		public final int count;
		public final Optional<String> afterName;

		public Request(final int count, final Optional<String> afterName)
		{
			if (count < 0)
				throw new IllegalArgumentException("negative count");
			this.count = count;
			this.afterName = Objects.requireNonNull(afterName, "afterName");
		}
	}

	class Response
	{
		public final Optional<String> afterName;
		public final List<Channel> channels;

		public Response(final Optional<String> afterName, final List<Channel> channels)
		{
			this.afterName = Objects.requireNonNull(afterName, "afterName");
			this.channels = List.copyOf(Objects.requireNonNull(channels, "channels"));
		}
	}
}
