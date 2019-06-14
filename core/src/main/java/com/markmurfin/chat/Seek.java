package com.markmurfin.chat;

import java.util.Objects;

public class Seek
{
	public final String field;
	public final String value;
	public final boolean descendingElseAscending;

	public Seek(final String field, final String value, final boolean descendingElseAscending)
	{
		this.field = Objects.requireNonNull(field, "field");
		this.value = Objects.requireNonNull(value, "value");
		this.descendingElseAscending = descendingElseAscending;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(field, value) * (descendingElseAscending ? 1 : -1);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Seek))
			return false;
		final Seek o = (Seek)obj;
		return Objects.equals(field, o.field)
			&& Objects.equals(value, o.value)
			&& descendingElseAscending == o.descendingElseAscending;


	}
}
