package com.markmurfin.chat;

import java.util.Objects;

public class WatchChannelStrategy implements WatchChannelUseCase
{
	@FunctionalInterface
	public interface Repository
	{
		Channel watchChannel(final String channelName, final String userId);
	}

	public WatchChannelStrategy(final Repository repository)
	{
		this.repository = Objects.requireNonNull(repository, "repository");
	}

	@Override
	public Response watchChannel(Request request)
	{
		return new Response(request.userId, repository.watchChannel(request.channelName, request.userId));
	}

	private final Repository repository;
}
