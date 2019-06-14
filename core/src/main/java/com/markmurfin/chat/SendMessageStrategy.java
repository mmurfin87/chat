package com.markmurfin.chat;

import java.time.Instant;
import java.util.Objects;

public class SendMessageStrategy implements SendMessageUseCase
{
	public interface Repository
	{
		boolean isUserWatchingChannel(String channelId, String userId);
		void persistMessage(String channelId, Message message);
	}

	public SendMessageStrategy(final Repository repository)
	{
		this.repository = Objects.requireNonNull(repository, "repository");
	}

	@Override
	public Response sendMessage(Request request)
	{
		if (!repository.isUserWatchingChannel(request.channelId, request.senderId))
			throw new IllegalArgumentException("Sender is not watching channel");
		final Message m = new Message(request.senderId, Instant.now(), request.content);
		repository.persistMessage(request.channelId, m);
		return new Response(request.channelId, m);
	}

	private final Repository repository;
}
