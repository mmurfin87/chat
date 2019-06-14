package com.markmurfin.chat;

import java.util.Objects;

public class Channel
{
	public final String id;
	public final String name;

	public Channel(final String id, final String name)
	{
		this.id = Objects.requireNonNull(id, "id");
		this.name = Objects.requireNonNull(name, "name");
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, name);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Channel))
			return false;
		final Channel o = (Channel)obj;
		return Objects.equals(id, o.id) && Objects.equals(name, o.name);
	}
}
