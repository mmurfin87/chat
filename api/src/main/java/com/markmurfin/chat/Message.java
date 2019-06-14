package com.markmurfin.chat;

import java.time.Instant;
import java.util.Objects;

public class Message
{
	public final String userId;
	public final Instant published;
	public final String content;

	public Message(final String userId, final Instant published, final String content)
	{
		this.userId = Objects.requireNonNull(userId, "userId");
		this.published = Objects.requireNonNull(published, "published");
		this.content = Objects.requireNonNull(content, "content");
	}
}
