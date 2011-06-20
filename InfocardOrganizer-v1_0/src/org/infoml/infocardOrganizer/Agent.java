package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import java.io.*;

public class Agent implements Serializable
{
	private String role;
	private String prefix;
	private String first;
	private String middle1, middle2;
	private String surname;
	private String preSeparator;
	private String suffix;

	public Agent()
	{
		role = new String("author");
		prefix = new String("");
		first = new String("");
		middle1 = new String("");
		middle2 = new String("");
		surname = new String("");
		preSeparator = new String("");
		suffix = new String("");
	}

	public Agent(Agent agent)
	{
		role = agent.getRole();
		prefix = agent.getPrefix();
		first = agent.getFirst();
		middle1 = agent.getMiddle1();
		middle2 = agent.getMiddle2();
		surname = agent.getSurname();
		preSeparator = agent.getPreSeparator();
		suffix = agent.getSuffix();
	}

	public boolean isEmpty()
	{
		boolean empty = true;

		if ((prefix.compareTo("") != 0) || (first.compareTo("") != 0) ||
			(middle1.compareTo("") != 0) || (middle2.compareTo("") != 0) ||
			(surname.compareTo("") != 0) ||
			(preSeparator.compareTo("") != 0) || (suffix.compareTo("") != 0))
		empty = false;

		return empty;
	}

	public String getFirst()
	{
		return first;
	}

	public void setFirst(String first)
	{
		this.first = first;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public String getMiddle1()
	{
		return middle1;
	}

	public void setMiddle1(String middle1)
	{
		this.middle1 = middle1;
	}

	public String getMiddle2()
	{
		return middle2;
	}

	public void setMiddle2(String middle2)
	{
		this.middle2 = middle2;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getPreSeparator()
	{
		return preSeparator;
	}

	public void setPreSeparator(String preSeparator)
	{
		this.preSeparator = preSeparator;
	}

	public String getRole()
	{
		return role;
	}
	public void setRole(String role)
	{
		this.role = role;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

}
