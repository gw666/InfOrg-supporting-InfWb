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

public class MyDocumentListener implements DocumentListener
{
	private NotecardModel model;
	private String field;
	private NotecardDialog obj;

	public MyDocumentListener(NotecardDialog obj, NotecardModel model, String field)
	{
		this.model = model;
		this.field = field;
		this.obj = obj;
	}

	public void changedUpdate(DocumentEvent e)
	{
	}

	public void insertUpdate(DocumentEvent e)
	{
	  Document doc = e.getDocument();
	  try
	  {
		if (field.compareTo("title") == 0)
		{
			model.setTitle(doc.getText(0, doc.getLength()));
			obj.titleField = true;
		}

		if (field.compareTo("content") == 0)
		{
			model.setContentString(doc.getText(0, doc.getLength()));
			obj.contentField = true;
		}

		if (field.compareTo("keywords") == 0)
		{
			model.setSelectorsString(doc.getText(0, doc.getLength()));
			obj.keywordsField = true;
		}

		if (field.compareTo("notes1") == 0)
		{
		  model.setNotes1String(doc.getText(0, doc.getLength()));
		  obj.notes1Field = true;
		}

		if (field.compareTo("notes2") == 0)
		{
		  model.setNotes2String(doc.getText(0, doc.getLength()));
		  obj.notes2Field = true;
		}
	  }
	  catch (BadLocationException ee)
	  { ee.printStackTrace(); }

		obj.mainFr.lastAction = 0;
	}

	public void removeUpdate(DocumentEvent e)
	{
		Document doc = e.getDocument();
		  try
		  {
			if (field.compareTo("title") == 0)
			{
				model.setTitle(doc.getText(0, doc.getLength()));
			}

			if (field.compareTo("content") == 0)
			{
				model.setContentString(doc.getText(0, doc.getLength()));
			}

			if (field.compareTo("keywords") == 0)
			{
				model.setSelectorsString(doc.getText(0, doc.getLength()));
			}
			if (field.compareTo("notes1") == 0)
			{
				model.setNotes1String(doc.getText(0, doc.getLength()));
			}
			if (field.compareTo("notes2") == 0)
			{
				model.setNotes2String(doc.getText(0, doc.getLength()));
			}
		  }
		  catch (Exception ee)
		  { ee.printStackTrace(); }
	}
}
