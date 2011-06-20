package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/


import java.io.*;

public class Deserializator extends Thread
{
	public Deserializator()
	{
	}

	public void run()
	{
		try
		{
			FileInputStream fis =
				new FileInputStream(System.getProperty("user.dir") + File.separator + "setup");
			ObjectInputStream ois = new ObjectInputStream(fis);

			String honorific = (String) ois.readObject();
			MainFrame.setupModel.setHonorific(honorific);

			String first = (String) ois.readObject();
			MainFrame.setupModel.setFirst(first);

			String middle1 = (String) ois.readObject();
			MainFrame.setupModel.setMiddle1(middle1);

			String middle2 = (String) ois.readObject();
			MainFrame.setupModel.setMiddle2(middle2);

			String surname = (String) ois.readObject();
			MainFrame.setupModel.setSurname(surname);

			String punctuation = (String) ois.readObject();
			MainFrame.setupModel.setPunctuation(punctuation);

			String suffix = (String) ois.readObject();
			MainFrame.setupModel.setSuffix(suffix);

			String number = (String) ois.readObject();
			MainFrame.setupModel.setNumber(number);

			String email = (String) ois.readObject();
			MainFrame.setupModel.setEmail(email);

			String domain = (String) ois.readObject();
			MainFrame.setupModel.setDomain(domain);

			String yahooId = (String) ois.readObject();
			MainFrame.setupModel.setYahooId(yahooId);
		}
		catch (Exception e)
		{}
	}
}
