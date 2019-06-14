package com.markmurfin.chat.listmessages;

public class MessageListRequest
{
	public final String Channel;
	public final boolean FromRecentElseEarliest; 
	public final int Skip;
	public final int Count;
	
	public MessageListRequest(String channel, boolean fromRecentElseEarliest, int skip, int count)
	{
		this.Channel = channel;
		this.FromRecentElseEarliest = fromRecentElseEarliest;
		this.Skip = skip;
		this.Count = count;
	}
}
