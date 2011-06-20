package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;



public class UniqueContentModel extends NotecardModel
{
	private String nickname;
	SharedContentModel shared;
	private String page1, page2;
	private boolean isExact = true;


	public UniqueContentModel()
	{
		super();

		this.type = "contentcard";

		page1 = new String("");
		page2 = new String("");
	}

	public boolean isEmpty()
	{
		boolean empty = true;

		if ((title.compareTo("") != 0) || (contentString.compareTo("") != 0) ||
			(page1.compareTo("") != 0) || (page2.compareTo("") != 0) ||
			(selectorsString.compareTo("") != 0) || (notes1String.compareTo("") != 0) ||
			(notes2String.compareTo("") != 0))
			empty = false;

		return empty;
	}

	public void setShared(String key, Map sharedContent)
	{
		shared = (SharedContentModel) sharedContent.get(key);
	}

	public void setShared(SharedContentModel shared)
	{
		this.shared = shared;
	}

	public boolean isExact() {
		return isExact;
	}
	public void setExact(boolean isExact) {
		this.isExact = isExact;
	}

	public String getPage1() {
		return page1;
	}
	public void setPage1(String page1) {
		this.page1 = page1;
	}
	public String getPage2() {
		return page2;
	}
	public void setPage2(String page2) {
		this.page2 = page2;
	}

	public String getNickname()
	{
		return nickname;
	}
}

