package com.markmurfin.chat;

import java.util.Objects;
import java.util.Optional;

public class LeaveChannelStrategy implements LeaveChannelUseCase
{
	public interface Repository
	{
		Optional<Channel> leaveChannel(String userId, String channelName);
	}

	public LeaveChannelStrategy(final Repository repository)
	{
		this.repository = Objects.requireNonNull(repository, "repository");
	}

	@Override
	public Response leaveChannel(Request request)
	{
		return repository.leaveChannel(request.userId, request.channelName)
			.map(c -> new Response(request.userId, c))
			.orElseThrow(() -> new IllegalArgumentException("No such channel"));
	}

	private final Repository repository;
}
