package com.markmurfin.chat;

import java.util.Objects;

public class CreateChannelStrategy implements CreateChannelUseCase
{
	public interface Repository
	{
		boolean channelNameExists(final String name);
		Channel createChannelWithName(final String name, final String ownerId);
		//MessageStream createChannelWithNameAndRetrieveMessageStream(final String name, final String ownerId);
	}

	public CreateChannelStrategy(final Repository repository)
	{
		this.repository = Objects.requireNonNull(repository, "repository");
	}

	@Override
	public Response createChannel(Request request)
	{
		if (repository.channelNameExists(request.name))
			throw new RuntimeException();
		return new Response(repository.createChannelWithName(request.name, request.ownerId));
		//return new Response(repository.createChannelWithNameAndRetrieveMessageStream(request.name, request.ownerId));
	}

	private final Repository repository;
}
