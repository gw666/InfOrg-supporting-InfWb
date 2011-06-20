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


import java.awt.Color;

public class UniqueContentListener implements DocumentListener
{
	UniqueContentDialog dialog;
	UniqueContentModel model;

	public UniqueContentListener(UniqueContentDialog dialog)
	{
		this.dialog = dialog;
		model = dialog.model;
	}

	public void insertUpdate(DocumentEvent e)
	{
	  try
	  {
		Document doc = e.getDocument();

		if (doc.equals(dialog.titleDoc))
		{
			model.setTitle(doc.getText(0, doc.getLength()));
		}

		if (doc.equals(dialog.contentDoc))
		{
			model.setContentString(doc.getText(0, doc.getLength()));
		}

		if (doc.equals(dialog.page1Doc))
		{
			model.setPage1(doc.getText(0, doc.getLength()));

			if (doc.getText(0, doc.getLength()).compareTo("") != 0)
			{
				dialog.to.setEditable(true);
			}
			else
			{
				dialog.to.setEditable(false);
				dialog.page2Doc.remove(0, dialog.page2Doc.getLength());
			}
		}

		if (doc.equals(dialog.page2Doc))
		{
			model.setPage2(doc.getText(0, doc.getLength()));
		}

		if (doc.equals(dialog.keywordsDoc))
		{
			model.setSelectorsString(doc.getText(0, doc.getLength()));
		}

		if (doc.equals(dialog.notes1Doc))
		{
			model.setNotes1String(doc.getText(0, doc.getLength()));
		}

		if (doc.equals(dialog.notes2Doc))
		{
			model.setNotes2String(doc.getText(0, doc.getLength()));
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

			if (doc.equals(dialog.titleDoc))
			{
				model.setTitle(doc.getText(0, doc.getLength()));
			}

			if (doc.equals(dialog.contentDoc))
			{
				model.setContentString(doc.getText(0, doc.getLength()));
			}

			if (doc.equals(dialog.page1Doc))
			{
				model.setPage1(doc.getText(0, doc.getLength()));

				if (doc.getText(0, doc.getLength()).compareTo("") != 0)
				{
					dialog.to.setEditable(true);
				}
				else
				{
					dialog.to.setEditable(false);
					dialog.page2Doc.remove(0, dialog.page2Doc.getLength());
				}
			}

			if (doc.equals(dialog.page2Doc))
			{
				model.setPage2(doc.getText(0, doc.getLength()));
			}

			if (doc.equals(dialog.keywordsDoc))
			{
				model.setSelectorsString(doc.getText(0, doc.getLength()));
			}

			if (doc.equals(dialog.notes1Doc))
			{
				model.setNotes1String(doc.getText(0, doc.getLength()));
			}

			if (doc.equals(dialog.notes2Doc))
			{
				model.setNotes2String(doc.getText(0, doc.getLength()));
			}
		  }
		  catch (Exception excep)
		  { excep.printStackTrace(); }
	}

	public void changedUpdate(DocumentEvent e)
	{

	}
}
