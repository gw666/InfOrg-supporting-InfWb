package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import javax.swing.event.*;
import javax.swing.text.*;


public class SetupInformationListener implements DocumentListener
{
	NotecardModel setupModel;
	String caller;
	SetupInformationDialog2 dialog;

	static boolean validFirst        = false;
	static boolean validLast         = false;
	static boolean validPrefix       = false;
	static boolean validpreSeparator = false;
	static boolean validSuffix 	  = false;
	static boolean validMiddle 	  = false;

	public SetupInformationListener(SetupInformationDialog2 dialog)
	{
		this.dialog = dialog;
		this.setupModel = dialog.setupModel;
	}

	public void insertUpdate(DocumentEvent e)
	{
	  try
	  {
		Document doc = e.getDocument();
		String text = doc.getText(0, doc.getLength());

		if (doc.equals(dialog.honorificDoc))
		{
			setupModel.setHonorific(text);
		}

		if (doc.equals(dialog.firstDoc))
		{
			setupModel.setFirst(text);
		}

		if (doc.equals(dialog.middle1Doc))
		{
			setupModel.setMiddle1(text);
		}

		if (doc.equals(dialog.middle2Doc))
		{
			setupModel.setMiddle2(text);
		}

		if (doc.equals(dialog.lastDoc))
		{
			setupModel.setSurname(text);
		}

		if (doc.equals(dialog.punctuationDoc))
		{
			setupModel.setPunctuation(text);
		}

		if (doc.equals(dialog.suffixDoc))
		{
			setupModel.setSuffix(text);
		}

		if (doc.equals(dialog.numberDoc))
		{
			setupModel.setSessionNumber(text);
		}

		if (doc.equals(dialog.emailDoc))
		{
			setupModel.setSessionEmail(text);
		}

		/*if (doc.equals(dialog.domainDoc))
		{
			setupModel.setSessionDomain(text);
		}*/

		if (doc.equals(dialog.yahooIdDoc))
		{
			setupModel.setSessionYahooId(text);
		}
	  }
	  catch (Exception excep)
	  { excep.printStackTrace(); }
	}

	public void removeUpdate(DocumentEvent e)
	{
		try
		  {
			Document doc = e.getDocument();
			String text = doc.getText(0, doc.getLength());

			if (doc.equals(dialog.honorificDoc))
			{
				setupModel.setHonorific(text);
			}

			if (doc.equals(dialog.firstDoc))
			{
				setupModel.setFirst(text);
			}

			if (doc.equals(dialog.middle1Doc))
			{
				setupModel.setMiddle1(text);
			}

			if (doc.equals(dialog.middle2Doc))
			{
				setupModel.setMiddle2(text);
			}

			if (doc.equals(dialog.lastDoc))
			{
				setupModel.setSurname(text);
			}

			if (doc.equals(dialog.punctuationDoc))
			{
				setupModel.setPunctuation(text);
			}

			if (doc.equals(dialog.suffixDoc))
			{
				setupModel.setPunctuation(text);
			}

			if (doc.equals(dialog.numberDoc))
			{
				setupModel.setSessionNumber(text);
			}

			if (doc.equals(dialog.emailDoc))
			{
				setupModel.setSessionEmail(text);
			}

			/*if (doc.equals(dialog.domainDoc))
			{
				setupModel.setSessionDomain(text);
			}*/

			if (doc.equals(dialog.yahooIdDoc))
			{
				setupModel.setSessionYahooId(text);
			}
		  }
		  catch (Exception excep)
		  { excep.printStackTrace(); }
	}

	public void changedUpdate(DocumentEvent e)
	{

	}
}
