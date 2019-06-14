package com.markmurfin.chat.listmessages;

import java.util.List;

public class MessageListResponse
{
	public final String Channel;
	
	/**
	 * Interpret null as meaning {@link Channel} does not exist
	 */
	public final List<Message> Messages;
	public final boolean FromRecentElseEarliest; 
	public final int Skip;
	public final int Count;
	
	public MessageListResponse(String channel, List<Message> messages, boolean fromRecentElseEarliest, int skip, int count)
	{
		this.Channel = channel;
		this.Messages = messages;
		this.FromRecentElseEarliest = fromRecentElseEarliest;
		this.Skip = skip;
		this.Count = count;
	}
}
