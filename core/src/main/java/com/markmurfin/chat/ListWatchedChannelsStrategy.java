package com.markmurfin.chat;

import java.util.List;
import java.util.Objects;

public class ListWatchedChannelsStrategy implements ListWatchedChannelsUseCase
{
	@FunctionalInterface
	public interface Repository
	{
		List<Channel> retrieveWatchedChannelsForUser(final String userId);
	}

	public ListWatchedChannelsStrategy(final Repository repository)
	{
		this.repository = Objects.requireNonNull(repository, "repository");
	}

	@Override
	public Response listWatchedChannels(Request request)
	{
		return new Response(request.userId, repository.retrieveWatchedChannelsForUser(request.userId));
	}

	private final Repository repository;
}