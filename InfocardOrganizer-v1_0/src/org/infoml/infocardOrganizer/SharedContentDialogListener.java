package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;


/* This class implements DocumentListener
 * and respons to the events triggered by any of the text fields in the
 * SharedContentDialog.
 */
public class SharedContentDialogListener implements DocumentListener
{
	SharedContentDialog dialog;
	SharedContentModel model;
	Agent agent1, agent2, agent3, agent4, agent5, agent6;

	public SharedContentDialogListener(SharedContentDialog dialog)
	{
		this.dialog = dialog;
		this.model = dialog.model;

		// the data in the text fields is copied in
		// model.agentX.dataY when Continue pressed
		agent1 = new Agent(model.getAgent1());
		agent2 = new Agent(model.getAgent2());
		agent3 = new Agent(model.getAgent3());
		agent4 = new Agent(model.getAgent4());
		agent5 = new Agent(model.getAgent5());
		agent6 = new Agent(model.getAgent6());
	}

	public void setAgent(Document doc, Document agentXDocY, Agent agentNr, int nr)
	{
	  try
	  {
	    if (nr == 1)
		if (doc.equals(agentXDocY))
		{ agentNr.setPrefix(doc.getText(0, doc.getLength())); }

	    if (nr == 2)
		if (doc.equals(agentXDocY))
		{ agentNr.setFirst(doc.getText(0, doc.getLength())); }

	    if (nr == 3)
		if (doc.equals(agentXDocY))
		{ agentNr.setMiddle1(doc.getText(0, doc.getLength())); }

	    if (nr == 4)
		if (doc.equals(agentXDocY))
		{ agentNr.setMiddle2(doc.getText(0, doc.getLength())); }

	    if (nr == 5)
		if (doc.equals(agentXDocY))
		{ agentNr.setSurname(doc.getText(0, doc.getLength()));	}

	    if (nr == 6)
		if (doc.equals(agentXDocY))
		{ agentNr.setPreSeparator(doc.getText(0, doc.getLength()));	}

	    if (nr == 7)
		if (doc.equals(agentXDocY))
		{ agentNr.setSuffix(doc.getText(0, doc.getLength())); }
	  }
	  catch (Exception excep)
	  { excep.printStackTrace(); }
	}

	public void setTextFields(Document doc)
	{
		try
		{
			if (doc.equals(dialog.nicknameDoc))
			{
				model.setNickname(doc.getText(0, doc.getLength()));
			}

			if (doc.equals(dialog.preSeparatorDoc))
			{
				model.setPreSepar(doc.getText(0, doc.getLength()));
			}

			/*if (doc.equals(dialog.date1Doc))
			{
				if (doc.getText(0, doc.getLength()).compareTo("") != 0)
					model.setDate1(new Integer(doc.getText(0, doc.getLength())).intValue());
				else
					model.setDate1(0);
			}*/

			if (doc.equals(dialog.date2Doc))
			{
				if (doc.getText(0, doc.getLength()).compareTo("") != 0)
					model.setDate2(new Integer(doc.getText(0, doc.getLength())).intValue());
				else
					model.setDate2(0);
			}

			if (doc.equals(dialog.date3Doc))
			{
				if (doc.getText(0, doc.getLength()).compareTo("") != 0)
					model.setDate3(new Integer(doc.getText(0, doc.getLength())).intValue());
				else
					model.setDate3(0);
			}

			if (doc.equals(dialog.dateRoleDoc))
			{
				model.setDateRole(doc.getText(0, doc.getLength()));

				if (doc.getText(0, doc.getLength()).compareTo("") == 0)
				{
					//dialog.date1.setText("");
					dialog.date1.setEnabled(false);
					//dialog.date2.setText("");
					dialog.date2.setEnabled(false);
					dialog.date3.setText("");
					dialog.date3.setEditable(false);
				}
				if (doc.getText(0, doc.getLength()).compareTo("") != 0)
				{
					dialog.date1.setEnabled(true);
					dialog.date2.setEnabled(true);
					dialog.date3.setEditable(true);
				}
			}

			if (doc.equals(dialog.sublocationPart1Doc))
			{
				model.setSublocationPart1(doc.getText(0, doc.getLength()));
			}

			if (doc.equals(dialog.sublocationPart2Doc))
			{
				model.setSublocationPart2(doc.getText(0, doc.getLength()));
			}

			if (doc.equals(dialog.enclCntNamePart1Doc))
			{
				model.setEnclCntNamePart1(doc.getText(0, doc.getLength()));
			}

			if (doc.equals(dialog.enclCntNamePart2Doc))
			{
				model.setEnclCntNamePart2(doc.getText(0, doc.getLength()));
			}

			if (doc.equals(dialog.enclPreSepDoc))
			{
				model.setEnclPreSepar(doc.getText(0, doc.getLength()));
			}

			if (doc.equals(dialog.corporateNameDoc))
			{
				model.setEnclCorporateName(doc.getText(0, doc.getLength()));
			}
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void insertUpdate(DocumentEvent e)
	{
	  try
	  {
		Document doc = e.getDocument();

		if (doc.equals(dialog.containerNamePart1Doc))
		{
			model.setContainerNamePart1(doc.getText(0, doc.getLength()));

		/*	if ((dialog.containerNamePart1Doc.getText
				    (0, dialog.containerNamePart1Doc.getLength()).compareTo("") != 0) ||
						    (dialog.containerNamePart2Doc.getText
						  (0, dialog.containerNamePart2Doc.getLength()).compareTo("") != 0))
			{
				dialog.date1.setEditable(true);
				dialog.date2.setEditable(true);
				dialog.date3.setEditable(true);
				dialog.dateRole.setEditable(true);
			}*/
		}

		if (doc.equals(dialog.containerNamePart2Doc))
		{
			model.setContainerNamePart2(doc.getText(0, doc.getLength()));

		/*	if ((dialog.containerNamePart1Doc.getText
				    (0, dialog.containerNamePart1Doc.getLength()).compareTo("") != 0) ||
						    (dialog.containerNamePart2Doc.getText
						  (0, dialog.containerNamePart2Doc.getLength()).compareTo("") != 0))
			{
				dialog.date1.setEditable(true);
				dialog.date2.setEditable(true);
				dialog.date3.setEditable(true);
				dialog.dateRole.setEditable(true);
			}*/
		}



		// checking all the rest of the text fields
		setTextFields(doc);

		// checking all agent fields
		setAgent(doc, dialog.agent1Doc1, agent1, 1);
		setAgent(doc, dialog.agent1Doc2, agent1, 2);
		setAgent(doc, dialog.agent1Doc3, agent1, 3);
		setAgent(doc, dialog.agent1Doc4, agent1, 4);
		setAgent(doc, dialog.agent1Doc5, agent1, 5);
		setAgent(doc, dialog.agent1Doc6, agent1, 6);
		setAgent(doc, dialog.agent1Doc7, agent1, 7);

		setAgent(doc, dialog.agent2Doc1, agent2, 1);
		setAgent(doc, dialog.agent2Doc2, agent2, 2);
		setAgent(doc, dialog.agent2Doc3, agent2, 3);
		setAgent(doc, dialog.agent2Doc4, agent2, 4);
		setAgent(doc, dialog.agent2Doc5, agent2, 5);
		setAgent(doc, dialog.agent2Doc6, agent2, 6);
		setAgent(doc, dialog.agent2Doc7, agent2, 7);

		setAgent(doc, dialog.agent3Doc1, agent3, 1);
		setAgent(doc, dialog.agent3Doc2, agent3, 2);
		setAgent(doc, dialog.agent3Doc3, agent3, 3);
		setAgent(doc, dialog.agent3Doc4, agent3, 4);
		setAgent(doc, dialog.agent3Doc5, agent3, 5);
		setAgent(doc, dialog.agent3Doc6, agent3, 6);
		setAgent(doc, dialog.agent3Doc7, agent3, 7);

		setAgent(doc, dialog.agent4Doc1, agent4, 1);
		setAgent(doc, dialog.agent4Doc2, agent4, 2);
		setAgent(doc, dialog.agent4Doc3, agent4, 3);
		setAgent(doc, dialog.agent4Doc4, agent4, 4);
		setAgent(doc, dialog.agent4Doc5, agent4, 5);
		setAgent(doc, dialog.agent4Doc6, agent4, 6);
		setAgent(doc, dialog.agent4Doc7, agent4, 7);

		setAgent(doc, dialog.agent5Doc1, agent5, 1);
		setAgent(doc, dialog.agent5Doc2, agent5, 2);
		setAgent(doc, dialog.agent5Doc3, agent5, 3);
		setAgent(doc, dialog.agent5Doc4, agent5, 4);
		setAgent(doc, dialog.agent5Doc5, agent5, 5);
		setAgent(doc, dialog.agent5Doc6, agent5, 6);
		setAgent(doc, dialog.agent5Doc7, agent5, 7);

		setAgent(doc, dialog.agent6Doc1, agent6, 1);
		setAgent(doc, dialog.agent6Doc2, agent6, 2);
		setAgent(doc, dialog.agent6Doc3, agent6, 3);
		setAgent(doc, dialog.agent6Doc4, agent6, 4);
		setAgent(doc, dialog.agent6Doc5, agent6, 5);
		setAgent(doc, dialog.agent6Doc6, agent6, 6);
		setAgent(doc, dialog.agent6Doc7, agent6, 7);
	  }
	  catch (Exception excep)
	  { excep.printStackTrace(); }
	}

	public void removeUpdate(DocumentEvent e)
	{
		try
		  {
			Document doc = e.getDocument();

			if (doc.equals(dialog.containerNamePart1Doc))
			{
				model.setContainerNamePart1(doc.getText(0, doc.getLength()));

		/*		if ((dialog.containerNamePart1Doc.getText
					    (0, dialog.containerNamePart1Doc.getLength()).compareTo("") == 0) &&
							    (dialog.containerNamePart2Doc.getText
							  (0, dialog.containerNamePart2Doc.getLength()).compareTo("") == 0))
				{
					dialog.date1.setEditable(false);
					dialog.date2.setEditable(false);
					dialog.date3.setEditable(false);
					dialog.dateRole.setEditable(false);
				}*/
			}

			if (doc.equals(dialog.containerNamePart2Doc))
			{
				model.setContainerNamePart2(doc.getText(0, doc.getLength()));

		/*		if ((dialog.containerNamePart1Doc.getText
					    (0, dialog.containerNamePart1Doc.getLength()).compareTo("") == 0) &&
							    (dialog.containerNamePart2Doc.getText
							  (0, dialog.containerNamePart2Doc.getLength()).compareTo("") == 0))
				{
					dialog.date1.setEditable(false);
					dialog.date2.setEditable(false);
					dialog.date3.setEditable(false);
					dialog.dateRole.setEditable(false);
				}*/
			}

			//	checking all the rest of the text fields
			setTextFields(doc);

			// checking all agent fields
			setAgent(doc, dialog.agent1Doc1, agent1, 1);
			setAgent(doc, dialog.agent1Doc2, agent1, 2);
			setAgent(doc, dialog.agent1Doc3, agent1, 3);
			setAgent(doc, dialog.agent1Doc4, agent1, 4);
			setAgent(doc, dialog.agent1Doc5, agent1, 5);
			setAgent(doc, dialog.agent1Doc6, agent1, 6);
			setAgent(doc, dialog.agent1Doc7, agent1, 7);

			setAgent(doc, dialog.agent2Doc1, agent2, 1);
			setAgent(doc, dialog.agent2Doc2, agent2, 2);
			setAgent(doc, dialog.agent2Doc3, agent2, 3);
			setAgent(doc, dialog.agent2Doc4, agent2, 4);
			setAgent(doc, dialog.agent2Doc5, agent2, 5);
			setAgent(doc, dialog.agent2Doc6, agent2, 6);
			setAgent(doc, dialog.agent2Doc7, agent2, 7);

			setAgent(doc, dialog.agent3Doc1, agent3, 1);
			setAgent(doc, dialog.agent3Doc2, agent3, 2);
			setAgent(doc, dialog.agent3Doc3, agent3, 3);
			setAgent(doc, dialog.agent3Doc4, agent3, 4);
			setAgent(doc, dialog.agent3Doc5, agent3, 5);
			setAgent(doc, dialog.agent3Doc6, agent3, 6);
			setAgent(doc, dialog.agent3Doc7, agent3, 7);

			setAgent(doc, dialog.agent4Doc1, agent4, 1);
			setAgent(doc, dialog.agent4Doc2, agent4, 2);
			setAgent(doc, dialog.agent4Doc3, agent4, 3);
			setAgent(doc, dialog.agent4Doc4, agent4, 4);
			setAgent(doc, dialog.agent4Doc5, agent4, 5);
			setAgent(doc, dialog.agent4Doc6, agent4, 6);
			setAgent(doc, dialog.agent4Doc7, agent4, 7);

			setAgent(doc, dialog.agent5Doc1, agent5, 1);
			setAgent(doc, dialog.agent5Doc2, agent5, 2);
			setAgent(doc, dialog.agent5Doc3, agent5, 3);
			setAgent(doc, dialog.agent5Doc4, agent5, 4);
			setAgent(doc, dialog.agent5Doc5, agent5, 5);
			setAgent(doc, dialog.agent5Doc6, agent5, 6);
			setAgent(doc, dialog.agent5Doc7, agent5, 7);

			setAgent(doc, dialog.agent6Doc1, agent6, 1);
			setAgent(doc, dialog.agent6Doc2, agent6, 2);
			setAgent(doc, dialog.agent6Doc3, agent6, 3);
			setAgent(doc, dialog.agent6Doc4, agent6, 4);
			setAgent(doc, dialog.agent6Doc5, agent6, 5);
			setAgent(doc, dialog.agent6Doc6, agent6, 6);
			setAgent(doc, dialog.agent6Doc7, agent6, 7);
		  }
		  catch (Exception excep)
		  { excep.printStackTrace(); }
	}

	public void changedUpdate(DocumentEvent e)
	{}

	public void setAgents()
	{
		model.setAgent1(agent1);
		model.setAgent2(agent2);
		model.setAgent3(agent3);
		model.setAgent4(agent4);
		model.setAgent5(agent5);
		model.setAgent6(agent6);

		model.printModel();
	}
}

