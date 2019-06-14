package com.markmurfin.chat.listmessages;

import java.util.Date;

public class Message
{
	public final String Content;
	public final String Creator;
	public final Date Created;
	
	public Message(String content, String creator, Date created)
	{
		this.Content = content;
		this.Creator = creator;
		this.Created = created;
	}
}
