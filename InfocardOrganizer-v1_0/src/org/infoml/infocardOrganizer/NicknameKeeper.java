package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import java.io.*;
import java.util.*;

public class NicknameKeeper
{
	/* This map bounds each content-card unmarshalled to the
	 * nickname of its shared content.
	 * The cardId is the key and the nickname is the value.
	 */
	static Map nickToId;

	public NicknameKeeper()
	{
		nickToId = new HashMap();
	}

	public void saveNicknames()
	{
		try
		  {
			  FileOutputStream fos = new FileOutputStream
			  		(System.getProperty("user.dir") + File.separator + "nicknames");
			  ObjectOutputStream oos = new ObjectOutputStream(fos);

			  for (Iterator i = nickToId.entrySet().iterator(); i.hasNext();)
			  {
				  Map.Entry e = (Map.Entry) i.next();

				  oos.writeObject(e.getKey());
				  oos.writeObject(e.getValue());
			  }

			  oos.flush();
			  oos.close();
		  }
		  catch (Exception e)
		  { e.printStackTrace(); }
	}

	public static void getNicknames()
	{
		try
		{
			FileInputStream fis =
				new FileInputStream(System.getProperty("user.dir") + 
						File.separator + "nicknames");
			
	
			ObjectInputStream ois = new ObjectInputStream(fis);

			while (fis.available() != 0)
			{
				String cardId = (String) ois.readObject();
				String nickname = (String) ois.readObject();

				nickToId.put(cardId, nickname);
			}

		}
		catch (Exception e)
		{ }
	}

}
