package com.markmurfin.chat;

import java.util.Objects;

public class ConnectChannelStrategy implements ConnectChannelUseCase
{
	@FunctionalInterface
	public interface Repository
	{
		MessageStream connectChannel(final String channelId);
	}

	public ConnectChannelStrategy(final Repository repository)
	{
		this.repository = Objects.requireNonNull(repository, "repository");
	}

	@Override
	public Response connectChannel(Request request)
	{
		return new Response(repository.connectChannel(request.channelId));
	}

	private final Repository repository;
}
