package com.markmurfin.chat;

import java.util.Objects;

@FunctionalInterface
public interface SendMessageUseCase
{
	Response sendMessage(Request request);

	class Request
	{
		public final String senderId;
		public final String channelId;
		public final String content;

		public Request(final String senderId, final String channelId, final String content)
		{
			this.senderId = Objects.requireNonNull(senderId, "senderId");
			this.channelId = Objects.requireNonNull(channelId, "channelName");
			this.content = Objects.requireNonNull(content, "content");
		}
	}

	class Response
	{
		public final String channelId;
		public final Message message;

		public Response(final String channelId, final Message message)
		{
			this.channelId = Objects.requireNonNull(channelId, "channelName");
			this.message = Objects.requireNonNull(message, "message");
		}
	}
}
