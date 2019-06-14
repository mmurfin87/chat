package com.markmurfin.chat;

import java.util.Objects;
import java.util.stream.Stream;

public class MessageStream
{
	public final Channel channel;
	public final long firstMessageOffset;
	public final Stream<Message> messages;

	public MessageStream(final Channel channel, final Stream<Message> messages, final long firstMessageOffset)
	{
		this.channel = Objects.requireNonNull(channel, "channel");
		this.messages = Objects.requireNonNull(messages, "messages");
		this.firstMessageOffset = firstMessageOffset;
	}
}
