package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/


import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.File;

public class LoadPath extends Thread
{
	public void run()
	{
		try
		{
			FileInputStream fis =
				new FileInputStream(System.getProperty("user.dir") + File.separator + "paths");
			ObjectInputStream ois = new ObjectInputStream(fis);

			MainFrame.defaultOpenPath = (String) ois.readObject();

			MainFrame.defaultSavePath = (String) ois.readObject();
		}
		catch (Exception e)
		{}
	}
}
