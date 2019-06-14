package com.markmurfin.chat;

import java.time.Instant;
import java.util.Objects;

public class SendMessageStrategy implements SendMessageUseCase
{
	@FunctionalInterface
	public interface Repository
	{
		void persistMessage(String channelId, Message message);
	}

	public SendMessageStrategy(final Repository repository)
	{
		this.repository = Objects.requireNonNull(repository, "repository");
	}

	@Override
	public Response sendMessage(Request request)
	{
		final Message m = new Message(request.senderId, Instant.now(), request.content);
		repository.persistMessage(request.channelId, m);
		return new Response(request.channelId, m);
	}

	private final Repository repository;
}
