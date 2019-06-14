package com.markmurfin.chat;

import java.util.List;
import java.util.Objects;

public class ExploreChannelsStrategy implements ExploreChannelsUseCase
{
	@FunctionalInterface
	public interface Repository
	{
		List<Channel> seekChannels(String afterName, long count);
	}

	public ExploreChannelsStrategy(final Repository repository)
	{
		this.repository = Objects.requireNonNull(repository, "repository");
	}

	@Override
	public Response exploreChannels(Request request)
	{
		return new Response(request.afterName, repository.seekChannels(request.afterName.orElse(null), request.count));
	}

	private final Repository repository;
}
