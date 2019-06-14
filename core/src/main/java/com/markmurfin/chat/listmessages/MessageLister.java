package com.markmurfin.chat.listmessages;

public interface MessageLister
{
	public void listMessages(MessageListRequest request);
	
	public default void listMessages(String channel, boolean fromRecentElseEarliest, int skip, int count)
	{
		listMessages(new MessageListRequest(channel, fromRecentElseEarliest, skip, count));
	}
}
