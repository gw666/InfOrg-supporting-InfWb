package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;

import org.infoml.jaxb.InfomlType;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class SharedContentDialog extends AncestorDialog
									implements PropertyChangeListener
{
	SharedContentModel model;
	JPanel sPanel;
	MainFrame mainFr;
	InfocardBuilder infocardBuilder;

	SharedContentDialogListener listener;
	Document nicknameDoc, containerNamePart1Doc, preSeparatorDoc, containerNamePart2Doc,
			 date1Doc, date2Doc, date3Doc, dateRoleDoc, sublocationPart1Doc,
			 sublocationPart2Doc, enclCntNamePart1Doc, enclCntNamePart2Doc,
			 enclPreSepDoc, corporateNameDoc,
			 agent1Doc1, agent1Doc2, agent1Doc3, agent1Doc4, agent1Doc5, agent1Doc6,
			 agent1Doc7,
			 agent2Doc1, agent2Doc2, agent2Doc3, agent2Doc4, agent2Doc5, agent2Doc6,
			 agent2Doc7,
			 agent3Doc1, agent3Doc2, agent3Doc3, agent3Doc4, agent3Doc5, agent3Doc6,
			 agent3Doc7,
			 agent4Doc1, agent4Doc2, agent4Doc3, agent4Doc4, agent4Doc5, agent4Doc6,
			 agent4Doc7,
			 agent5Doc1, agent5Doc2, agent5Doc3, agent5Doc4, agent5Doc5, agent5Doc6,
			 agent5Doc7,
			 agent6Doc1, agent6Doc2, agent6Doc3, agent6Doc4, agent6Doc5, agent6Doc6,
			 agent6Doc7
			 ;

	private boolean visible = false;
	private int prefWidth = 0;

	JTextField containerNamePart1, containerNamePart2;
	JTextField dateRole;
	JFormattedTextField  date3;
	JComboBox date1, date2; //date2, date3;

	boolean edit;
	// the nickname of the Shared content information before the edit operation
	String oldNickname;
	NotecardModel contentcardModel;

	Vector flickeringTextFields;

	public SharedContentDialog(Frame owner, String title, SharedContentModel model,
				InfocardBuilder infocardBuilder, MainFrame mainFr, boolean edit,
				NotecardModel contentcardModel)
	{
		super(owner, title, mainFr);

		this.setAlwaysOnTop(false);
		flickeringTextFields = new Vector(6);

		this.model = model;
		this.mainFr = mainFr;
		this.infocardBuilder = infocardBuilder;
		this.edit = edit;
		if (edit) oldNickname = model.getNickname();
		this.contentcardModel = contentcardModel;

		listener = new SharedContentDialogListener(SharedContentDialog.this);

		setPrefWidth();

		JPanel panel = new JPanel(new BorderLayout(0, 15));
		panel.setBorder(createBorder());
		this.getContentPane().add(panel);

		panel.add(createNorthPanel(), BorderLayout.NORTH);
		panel.add(createCenterPanel(), BorderLayout.CENTER);

		sPanel = createSouthPanel();
		panel.add(sPanel, BorderLayout.SOUTH);

		if (extraPanelShouldBeVisible())
		{
			sPanel.add(createExtraFieldsPanel(), BorderLayout.NORTH);
			SharedContentDialog.this.pack();
			SharedContentDialog.this.repaint();
			visible = true;
		}

		//this.setResizable(false);

		this.pack();
		this.setVisible(true);
	}

	private boolean extraPanelShouldBeVisible()
	{
		return  (!(model.getSublocationPart1().equals("")) ||
				   !(model.getSublocationPart2().equals("")) ||
				   !(model.getSublocationPart2().equals("")) ||
				   !(model.getEnclPreSepar().equals(""))     ||
				   !(model.getEnclCntNamePart2().equals("")) ||
				   !(model.getEnclCorporateName().equals("")));
	}

	public Border createBorder()
	{
		Border inner = BorderFactory.createTitledBorder(
				BorderFactory.createLoweredBevelBorder(), "  Shared Content  ");

		Border cb = BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 10, 25, 10), inner);

		return cb;
	}

	public void setPrefWidth()
	{
		JLabel refLabel = new JLabel("Enclosing container title");

		prefWidth = refLabel.getPreferredSize().width;
	}

	public void makeXXXLabel(JPanel parent, String title)
	{
		JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER));

			JLabel label = new JLabel(title, SwingConstants.CENTER);
			label.setPreferredSize(new Dimension(prefWidth,
											label.getPreferredSize().height));
			flow.add(label);

			//  the parent is a grid layout
			parent.add(flow);
	}

	/* This method creates a FlowLayout Panel with all the fields of the agent
	 * and registers the same DocumentListener to all of them.
	 */
	public JPanel createAgentPanel(int nr)
	{
		final int nrf = nr;
		String[] role = {"author", "card creator", "editor",
				"series editor","volume editor", "publisher", "agent",
				"chair", "producer", "writer/director", "director",
				"executive producer", "distributor", "actor", "musician",
				"singer", "speaker", "advisor"};

		JPanel agent = new JPanel(new FlowLayout(FlowLayout.LEFT));

			JTextField tfield = new JTextField(2);
			tfield.setToolTipText("Honorific");
			Document doc1 = tfield.getDocument();

			tfield.addFocusListener(mainFr.focusListener);
			tfield.addCaretListener(mainFr.caretListener);

			int prefHeight = tfield.getPreferredSize().height;

			final JComboBox choice = new JComboBox(role);

			if (nrf == 1)
			{
				if (!edit)
					model.getAgent1().setRole("author");

				if (edit)
				{
					choice.setSelectedItem(model.getAgent1().getRole());
				}
			}
			if (nrf == 2)
			{
				if (!edit)
					model.getAgent2().setRole("author");

				if (edit)
					choice.setSelectedItem(model.getAgent2().getRole());
			}
			if (nrf == 3)
			{
				if (!edit)
					model.getAgent3().setRole("author");

				if (edit)
					choice.setSelectedItem(model.getAgent3().getRole());
			}
			if (nrf == 4)
			{
				if (!edit)
					model.getAgent4().setRole("author");

				if (edit)
					choice.setSelectedItem(model.getAgent4().getRole());
			}
			if (nrf == 5)
			{
				if (!edit)
					model.getAgent5().setRole("author");

				if (edit)
					choice.setSelectedItem(model.getAgent5().getRole());
			}
			if (nrf == 6)
			{
				if (!edit)
					model.getAgent6().setRole("author");

				if (edit)
					choice.setSelectedItem(model.getAgent6().getRole());
			}

			// the code wich updates the model is handled by this listener
			// in the case of this combo box.
			if (nrf == 1)
			choice.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					String role = (String) choice.getSelectedItem();

					model.getAgent1().setRole(role);

					listener.agent1.setRole(role);
				}
			});
			if (nrf == 2)
				choice.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						String role = (String) choice.getSelectedItem();

						model.getAgent2().setRole(role);
						listener.agent2.setRole(role);
					}
				});
			if (nrf == 3)
				choice.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						String role = (String) choice.getSelectedItem();

						model.getAgent3().setRole(role);
						listener.agent3.setRole(role);
					}
				});
			if (nrf == 4)
				choice.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						String role = (String) choice.getSelectedItem();

						model.getAgent4().setRole(role);
						listener.agent4.setRole(role);
					}
				});
			if (nrf == 5)
				choice.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						String role = (String) choice.getSelectedItem();

						model.getAgent5().setRole(role);
						listener.agent5.setRole(role);
					}
				});
			if (nrf == 6)
				choice.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						String role = (String) choice.getSelectedItem();

						model.getAgent6().setRole(role);
						listener.agent6.setRole(role);
					}
				});

			agent.add(choice);
			choice.setPreferredSize(new Dimension(choice.getPreferredSize().width,
					prefHeight));

			tfield.setBorder(BorderFactory.createLoweredBevelBorder());
			agent.add(tfield);

			JTextField first = new JTextField(9);
			first.setToolTipText("First name");
			first.addFocusListener(mainFr.focusListener);
			first.addCaretListener(mainFr.caretListener);

			Document doc2 = first.getDocument();
			first.setBorder(BorderFactory.createLoweredBevelBorder());
			agent.add(first);

			JTextField middle1 = new JTextField(7);
			middle1.setToolTipText("Middle name");
			middle1.addFocusListener(mainFr.focusListener);
			middle1.addCaretListener(mainFr.caretListener);

			Document doc3 = middle1.getDocument();
			middle1.setBorder(BorderFactory.createLoweredBevelBorder());
			agent.add(middle1);

			JTextField middle2 = new JTextField(7);
			middle2.setToolTipText("Middle name");
			middle2.addFocusListener(mainFr.focusListener);
			middle2.addCaretListener(mainFr.caretListener);

			Document doc4 = middle2.getDocument();
			middle2.setBorder(BorderFactory.createLoweredBevelBorder());
			agent.add(middle2);

			JTextField surname = new JTextField(10);
			surname.setToolTipText("Last name");
			flickeringTextFields.addElement(surname);
			surname.addFocusListener(mainFr.focusListener);
			surname.addCaretListener(mainFr.caretListener);

			Document doc5 = surname.getDocument();
			surname.setBorder(BorderFactory.createLoweredBevelBorder());
			agent.add(surname);

			JTextField pre = new JTextField(2);
			pre.setToolTipText("punctuation");
			pre.addFocusListener(mainFr.focusListener);
			pre.addCaretListener(mainFr.caretListener);

			Document doc6 = pre.getDocument();
			pre.setBorder(BorderFactory.createLoweredBevelBorder());
			agent.add(pre);

			JTextField suffix = new JTextField(3);
			suffix.setToolTipText("Suffix");
			suffix.addFocusListener(mainFr.focusListener);
			suffix.addCaretListener(mainFr.caretListener);

			Document doc7 = suffix.getDocument();
			suffix.setBorder(BorderFactory.createLoweredBevelBorder());
			agent.add(suffix);

			if (nr == 1)
			{
				if (edit)
				{
					tfield.setText(model.getAgent1().getPrefix());
					first.setText(model.getAgent1().getFirst());
					middle1.setText(model.getAgent1().getMiddle1());
					middle2.setText(model.getAgent1().getMiddle2());
					surname.setText(model.getAgent1().getSurname());
					suffix.setText(model.getAgent1().getSuffix());
					pre.setText(model.getAgent1().getPreSeparator());
				}

				agent1Doc1 = doc1;
				agent1Doc1.addDocumentListener(listener);
				agent1Doc1.addUndoableEditListener(ownUndoListener);

				agent1Doc2 = doc2;
				agent1Doc2.addDocumentListener(listener);
				agent1Doc2.addUndoableEditListener(ownUndoListener);

				agent1Doc3 = doc3;
				agent1Doc3.addDocumentListener(listener);
				agent1Doc3.addUndoableEditListener(ownUndoListener);

				agent1Doc4 = doc4;
				agent1Doc4.addDocumentListener(listener);
				agent1Doc4.addUndoableEditListener(ownUndoListener);

				agent1Doc5 = doc5;
				agent1Doc5.addDocumentListener(listener);
				agent1Doc5.addUndoableEditListener(ownUndoListener);

				agent1Doc6 = doc6;
				agent1Doc6.addDocumentListener(listener);
				agent1Doc6.addUndoableEditListener(ownUndoListener);

				agent1Doc7 = doc7;
				agent1Doc7.addDocumentListener(listener);
				agent1Doc7.addUndoableEditListener(ownUndoListener);
			}
			if (nr == 2)
			{
				if (edit)
				{
					tfield.setText(model.getAgent2().getPrefix());
					first.setText(model.getAgent2().getFirst());
					middle1.setText(model.getAgent2().getMiddle1());
					middle2.setText(model.getAgent2().getMiddle2());
					surname.setText(model.getAgent2().getSurname());
					suffix.setText(model.getAgent2().getSuffix());
					pre.setText(model.getAgent2().getPreSeparator());
				}

				agent2Doc1 = doc1;
				agent2Doc1.addDocumentListener(listener);
				agent2Doc1.addUndoableEditListener(ownUndoListener);

				agent2Doc2 = doc2;
				agent2Doc2.addDocumentListener(listener);
				agent2Doc2.addUndoableEditListener(ownUndoListener);

				agent2Doc3 = doc3;
				agent2Doc3.addDocumentListener(listener);
				agent2Doc3.addUndoableEditListener(ownUndoListener);

				agent2Doc4 = doc4;
				agent2Doc4.addDocumentListener(listener);
				agent2Doc4.addUndoableEditListener(ownUndoListener);

				agent2Doc5 = doc5;
				agent2Doc5.addDocumentListener(listener);
				agent2Doc5.addUndoableEditListener(ownUndoListener);

				agent2Doc6 = doc6;
				agent2Doc6.addDocumentListener(listener);
				agent2Doc6.addUndoableEditListener(ownUndoListener);

				agent2Doc7 = doc7;
				agent2Doc7.addDocumentListener(listener);
				agent2Doc7.addUndoableEditListener(ownUndoListener);
			}
			if (nr == 3)
			{
				if (edit)
				{
					tfield.setText(model.getAgent3().getPrefix());
					first.setText(model.getAgent3().getFirst());
					middle1.setText(model.getAgent3().getMiddle1());
					middle2.setText(model.getAgent3().getMiddle2());
					surname.setText(model.getAgent3().getSurname());
					suffix.setText(model.getAgent3().getSuffix());
					pre.setText(model.getAgent3().getPreSeparator());
				}

				agent3Doc1 = doc1;
				agent3Doc1.addDocumentListener(listener);
				agent3Doc1.addUndoableEditListener(ownUndoListener);

				agent3Doc2 = doc2;
				agent3Doc2.addDocumentListener(listener);
				agent3Doc2.addUndoableEditListener(ownUndoListener);

				agent3Doc3 = doc3;
				agent3Doc3.addDocumentListener(listener);
				agent3Doc3.addUndoableEditListener(ownUndoListener);

				agent3Doc4 = doc4;
				agent3Doc4.addDocumentListener(listener);
				agent3Doc4.addUndoableEditListener(ownUndoListener);

				agent3Doc5 = doc5;
				agent3Doc5.addDocumentListener(listener);
				agent3Doc5.addUndoableEditListener(ownUndoListener);

				agent3Doc6 = doc6;
				agent3Doc6.addDocumentListener(listener);
				agent3Doc6.addUndoableEditListener(ownUndoListener);

				agent3Doc7 = doc7;
				agent3Doc7.addDocumentListener(listener);
				agent3Doc7.addUndoableEditListener(ownUndoListener);
			}
			if (nr == 4)
			{
				if (edit)
				{
					tfield.setText(model.getAgent4().getPrefix());
					first.setText(model.getAgent4().getFirst());
					middle1.setText(model.getAgent4().getMiddle1());
					middle2.setText(model.getAgent4().getMiddle2());
					surname.setText(model.getAgent4().getSurname());
					suffix.setText(model.getAgent4().getSuffix());
					pre.setText(model.getAgent4().getPreSeparator());
				}

				agent4Doc1 = doc1;
				agent4Doc1.addDocumentListener(listener);
				agent4Doc1.addUndoableEditListener(ownUndoListener);

				agent4Doc2 = doc2;
				agent4Doc2.addDocumentListener(listener);
				agent4Doc2.addUndoableEditListener(ownUndoListener);

				agent4Doc3 = doc3;
				agent4Doc3.addDocumentListener(listener);
				agent4Doc3.addUndoableEditListener(ownUndoListener);

				agent4Doc4 = doc4;
				agent4Doc4.addDocumentListener(listener);
				agent4Doc4.addUndoableEditListener(ownUndoListener);

				agent4Doc5 = doc5;
				agent4Doc5.addDocumentListener(listener);
				agent4Doc5.addUndoableEditListener(ownUndoListener);

				agent4Doc6 = doc6;
				agent4Doc6.addDocumentListener(listener);
				agent4Doc6.addUndoableEditListener(ownUndoListener);

				agent4Doc7 = doc7;
				agent4Doc7.addDocumentListener(listener);
				agent4Doc7.addUndoableEditListener(ownUndoListener);
			}
			if (nr == 5)
			{
				if (edit)
				{
					tfield.setText(model.getAgent5().getPrefix());
					first.setText(model.getAgent5().getFirst());
					middle1.setText(model.getAgent5().getMiddle1());
					middle2.setText(model.getAgent5().getMiddle2());
					surname.setText(model.getAgent5().getSurname());
					suffix.setText(model.getAgent5().getSuffix());
					pre.setText(model.getAgent5().getPreSeparator());
				}

				agent5Doc1 = doc1;
				agent5Doc1.addDocumentListener(listener);
				agent5Doc1.addUndoableEditListener(ownUndoListener);

				agent5Doc2 = doc2;
				agent5Doc2.addDocumentListener(listener);
				agent5Doc2.addUndoableEditListener(ownUndoListener);

				agent5Doc3 = doc3;
				agent5Doc3.addDocumentListener(listener);
				agent5Doc3.addUndoableEditListener(ownUndoListener);

				agent5Doc4 = doc4;
				agent5Doc4.addDocumentListener(listener);
				agent5Doc4.addUndoableEditListener(ownUndoListener);

				agent5Doc5 = doc5;
				agent5Doc5.addDocumentListener(listener);
				agent5Doc5.addUndoableEditListener(ownUndoListener);

				agent5Doc6 = doc6;
				agent5Doc6.addDocumentListener(listener);
				agent5Doc6.addUndoableEditListener(ownUndoListener);

				agent5Doc7 = doc7;
				agent5Doc7.addDocumentListener(listener);
				agent5Doc7.addUndoableEditListener(ownUndoListener);
			}
			if (nr == 6)
			{
				if (edit)
				{
					tfield.setText(model.getAgent6().getPrefix());
					first.setText(model.getAgent6().getFirst());
					middle1.setText(model.getAgent6().getMiddle1());
					middle2.setText(model.getAgent6().getMiddle2());
					surname.setText(model.getAgent6().getSurname());
					suffix.setText(model.getAgent6().getSuffix());
					pre.setText(model.getAgent6().getPreSeparator());
				}

				agent6Doc1 = doc1;
				agent6Doc1.addDocumentListener(listener);
				agent6Doc1.addUndoableEditListener(ownUndoListener);

				agent6Doc2 = doc2;
				agent6Doc2.addDocumentListener(listener);
				agent6Doc2.addUndoableEditListener(ownUndoListener);

				agent6Doc3 = doc3;
				agent6Doc3.addDocumentListener(listener);
				agent6Doc3.addUndoableEditListener(ownUndoListener);

				agent6Doc4 = doc4;
				agent6Doc4.addDocumentListener(listener);
				agent6Doc4.addUndoableEditListener(ownUndoListener);

				agent6Doc5 = doc5;
				agent6Doc5.addDocumentListener(listener);
				agent6Doc5.addUndoableEditListener(ownUndoListener);

				agent6Doc6 = doc6;
				agent6Doc6.addDocumentListener(listener);
				agent6Doc6.addUndoableEditListener(ownUndoListener);

				agent6Doc7 = doc7;
				agent6Doc7.addDocumentListener(listener);
				agent6Doc7.addUndoableEditListener(ownUndoListener);
			}

		return agent;
	}

	public JPanel createNorthPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());

		JPanel wPanel = new JPanel(new GridLayout(0, 1, 0, 5));
		panel.add(wPanel, BorderLayout.WEST);

			makeXXXLabel(wPanel, " Nickname ");
			makeXXXLabel(wPanel, " Container Title ");
			makeXXXLabel(wPanel, " Date(M/D/Y) ");
			makeXXXLabel(wPanel, " Agent(s) ");
			makeXXXLabel(wPanel, "          ");
			makeXXXLabel(wPanel, "          ");
			makeXXXLabel(wPanel, "          ");

		JPanel cPanel = new JPanel(new GridLayout(0, 1, 0, 5));
		panel.add(cPanel, BorderLayout.CENTER);

			// first text fields row in  form
			JPanel flow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JTextField nickname = new JTextField(20);

			nickname.addCaretListener(mainFr.caretListener);
			nickname.addFocusListener(mainFr.focusListener);

			if (edit) nickname.setText(model.getNickname());

			nicknameDoc = nickname.getDocument();
			nicknameDoc.addDocumentListener(listener);
			nicknameDoc.addUndoableEditListener(ownUndoListener);

			nickname.setBorder(BorderFactory.createLoweredBevelBorder());
			flow1.add(nickname);
			cPanel.add(flow1);


			// second text fields row in form
			JPanel flow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			containerNamePart1 = new JTextField(20);

			containerNamePart1.addFocusListener(mainFr.focusListener);
			containerNamePart1.addCaretListener(mainFr.caretListener);

			if (edit) containerNamePart1.setText(model.getContainerNamePart1());

			containerNamePart1Doc = containerNamePart1.getDocument();
			containerNamePart1Doc.addDocumentListener(listener);
			containerNamePart1Doc.addUndoableEditListener(ownUndoListener);

			containerNamePart1.setBorder(BorderFactory.createLoweredBevelBorder());
			flow2.add(containerNamePart1);

			JTextField preSeparator = new JTextField(3);

			preSeparator.addFocusListener(mainFr.focusListener);
			preSeparator.addCaretListener(mainFr.caretListener);

			if (edit) preSeparator.setText(model.getPreSepar());

			preSeparatorDoc = preSeparator.getDocument();
			preSeparatorDoc.addDocumentListener(listener);
			preSeparatorDoc.addUndoableEditListener(ownUndoListener);

			preSeparator.setBorder(BorderFactory.createLoweredBevelBorder());
			flow2.add(preSeparator);

			containerNamePart2 = new JTextField(20);

			containerNamePart2.addFocusListener(mainFr.focusListener);
			containerNamePart2.addCaretListener(mainFr.caretListener);

			if (edit) containerNamePart2.setText(model.getContainerNamePart2());

			containerNamePart2Doc = containerNamePart2.getDocument();
			containerNamePart2Doc.addDocumentListener(listener);
			containerNamePart2Doc.addUndoableEditListener(ownUndoListener);

			containerNamePart2.setBorder(BorderFactory.createLoweredBevelBorder());
			flow2.add(containerNamePart2);
			cPanel.add(flow2);

			JPanel flow3 = new JPanel(new FlowLayout(FlowLayout.LEFT));


			/*date1 = new JFormattedTextField(createFormatter("##"));
			date1.addPropertyChangeListener("value", this);
			date1.setColumns(3);
			//date1.setText("00");
			date1.addFocusListener(mainFr.focusListener);
			date1.addCaretListener(mainFr.caretListener);

			if (edit)
			{
				if (model.getDate1() != 0)
					date1.setText(new Integer(model.getDate1()).toString());
			}

			date1Doc = date1.getDocument();
			//date1Doc.addDocumentListener(listener);
			date1Doc.addUndoableEditListener(ownUndoListener);

			date1.setBorder(BorderFactory.createLoweredBevelBorder());
			flow3.add(date1);*/

			String[] months = {"N/A", "1", "2", "3", "4", "5", "6",
								"7", "8", "9", "10", "11", "12"};
			date1 = new JComboBox(months);
			//date1.addFocusListener(mainFr.focusListener);
			if (edit)
				if (model.getDate1() != 0)
					date1.setSelectedItem(new Integer(model.getDate1()).toString());

			date1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					String month = (String) date1.getSelectedItem();

					model.setDate1((new Integer(month)).intValue());
				}
			});
			date1.setPreferredSize(new Dimension(date1.getPreferredSize().width,
						containerNamePart1.getPreferredSize().height));
			flow3.add(date1);

			String[] days = {"N/A", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
				"11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
				"22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
			date2 = new JComboBox(days);
			if (edit)
				if (model.getDate2() != 0)
					date2.setSelectedItem(new Integer(model.getDate2()).toString());
			date2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					int day = date2.getSelectedIndex();

					model.setDate2(day);
				}
			});
			date2.setPreferredSize(new Dimension(date2.getPreferredSize().width,
					containerNamePart1.getPreferredSize().height));
			flow3.add(date2);



			/*date2 = new JFormattedTextField(createFormatter("##"));
			date2.addPropertyChangeListener("value", this);
			date2.setColumns(3);
			date2.addFocusListener(mainFr.focusListener);
			date2.addCaretListener(mainFr.caretListener);

			if (edit)
			{
				if (model.getDate2() != 0)
					date2.setText(new Integer(model.getDate2()).toString());
			}

			date2Doc = date2.getDocument();
			//date2Doc.addDocumentListener(listener);
			date2Doc.addUndoableEditListener(ownUndoListener);

			date2.setBorder(BorderFactory.createLoweredBevelBorder());
			flow3.add(date2);
			*/

			date3 = new JFormattedTextField(createFormatter("####"));
			date3.addPropertyChangeListener("value", this);
			date3.setColumns(3);
			date3.addFocusListener(mainFr.focusListener);
			date3.addCaretListener(mainFr.caretListener);

			if (edit)
			{
				if (model.getDate3() != 0)
					date3.setText(new Integer(model.getDate3()).toString());
			}

			date3Doc = date3.getDocument();
			//date3Doc.addDocumentListener(listener);
			date3Doc.addUndoableEditListener(ownUndoListener);

			date3.setBorder(BorderFactory.createLoweredBevelBorder());
			flow3.add(date3);

			JLabel label = new JLabel("    Date descriptor (blank if no date) ");
			flow3.add(label);

			dateRole = new JTextField("publication", 12);
			dateRole.addFocusListener(mainFr.focusListener);
			dateRole.addCaretListener(mainFr.caretListener);

			if (edit)
			{
				if (model.getDateRole().compareTo("") != 0)
					dateRole.setText(model.getDateRole());
				else
				{
					dateRole.setText("");
					date1.setEditable(false);
					date2.setEditable(false);
					date3.setEditable(false);
				}
			}
			else
				model.setDateRole("publication");

			dateRoleDoc = dateRole.getDocument();
			dateRoleDoc.addDocumentListener(listener);
			dateRoleDoc.addUndoableEditListener(ownUndoListener);

			dateRole.setBorder(BorderFactory.createLoweredBevelBorder());
			flow3.add(dateRole);

			cPanel.add(flow3);

			cPanel.add(createAgentPanel(1));
			cPanel.add(createAgentPanel(2));
			cPanel.add(createAgentPanel(3));
			cPanel.add(createAgentPanel(4));

		return panel;
	}

	public JPanel createExtraFieldsPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());

		JPanel wPanel = new JPanel(new GridLayout(0, 1, 0, 5));
		panel.add(wPanel, BorderLayout.WEST);

			makeXXXLabel(wPanel, " Volume ");

			makeXXXLabel(wPanel, "Enclosing container title");

			makeXXXLabel(wPanel, " Agent(s) ");

			makeXXXLabel(wPanel, "          ");

			makeXXXLabel(wPanel, " Corporate agent ");

		JPanel cPanel = new JPanel(new GridLayout(0, 1, 0, 5));
		panel.add(cPanel, BorderLayout.CENTER);

			JPanel flow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JTextField tfield1 = new JTextField(5);
			tfield1.addFocusListener(mainFr.focusListener);
			tfield1.addCaretListener(mainFr.caretListener);

			if (edit)
			{
				tfield1.setText(model.getSublocationPart1());
			}

			sublocationPart1Doc = tfield1.getDocument();
			sublocationPart1Doc.addDocumentListener(listener);
			sublocationPart1Doc.addUndoableEditListener(ownUndoListener);

			tfield1.setBorder(BorderFactory.createLoweredBevelBorder());
			flow1.add(tfield1);

			JLabel label = new JLabel("     Issue  ");
			flow1.add(label);

			JTextField tfield2 = new JTextField(5);
			tfield2.addFocusListener(mainFr.focusListener);
			tfield2.addCaretListener(mainFr.caretListener);

			if (edit)
			{
				tfield2.setText(model.getSublocationPart2());
			}

			sublocationPart2Doc = tfield2.getDocument();
			sublocationPart2Doc.addDocumentListener(listener);
			sublocationPart2Doc.addUndoableEditListener(ownUndoListener);

			tfield2.setBorder(BorderFactory.createLoweredBevelBorder());
			flow1.add(tfield2);
			cPanel.add(flow1);

			JPanel flow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JTextField field1 = new JTextField(24);
			field1.addFocusListener(mainFr.focusListener);
			field1.addCaretListener(mainFr.caretListener);

			if (edit)
			{
				field1.setText(model.getEnclCntNamePart1());
			}

			enclCntNamePart1Doc = field1.getDocument();
			enclCntNamePart1Doc.addDocumentListener(listener);
			enclCntNamePart1Doc.addUndoableEditListener(ownUndoListener);

			field1.setBorder(BorderFactory.createLoweredBevelBorder());
			flow2.add(field1);

			JTextField field2 = new JTextField(3);
			field2.addFocusListener(mainFr.focusListener);
			field2.addCaretListener(mainFr.caretListener);

			if (edit)
			{
				field2.setText(model.getEnclPreSepar());
			}

			enclPreSepDoc = field2.getDocument();
			enclPreSepDoc.addDocumentListener(listener);
			enclPreSepDoc.addUndoableEditListener(ownUndoListener);

			field2.setBorder(BorderFactory.createLoweredBevelBorder());
			flow2.add(field2);

			JTextField field3 = new JTextField(24);
			field3.addFocusListener(mainFr.focusListener);
			field3.addCaretListener(mainFr.caretListener);

			if (edit)
			{
				field3.setText(model.getEnclCntNamePart2());

			}

			enclCntNamePart2Doc = field3.getDocument();
			enclCntNamePart2Doc.addDocumentListener(listener);
			enclCntNamePart2Doc.addUndoableEditListener(ownUndoListener);

			field3.setBorder(BorderFactory.createLoweredBevelBorder());
			flow2.add(field3);
			cPanel.add(flow2);

			cPanel.add(createAgentPanel(5));
			cPanel.add(createAgentPanel(6));

			JPanel flow3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			String[] role = {"author", "card creator", "editor",
					"series editor","volume editor", "publisher", "agent",
					"chair", "producer", "writer/director", "director",
					"executive producer", "distributor", "actor", "musician",
					"singer", "speaker", "advisor"};
			int prefHeight = field1.getPreferredSize().height;

			final JComboBox choice = new JComboBox(role);
			if (edit) choice.setSelectedItem(model.getEnclRole());

			choice.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					model.setEnclRole((String) choice.getSelectedItem());
				}
			});

			choice.setPreferredSize(new Dimension(choice.getPreferredSize().width,
					prefHeight));
			flow3.add(choice);

			JTextField last = new JTextField(44);
			last.addFocusListener(mainFr.focusListener);
			last.addCaretListener(mainFr.caretListener);

			if (edit)
			{
				last.setText(model.getEnclCorporateName());
			}

			corporateNameDoc = last.getDocument();
			corporateNameDoc.addDocumentListener(listener);
			corporateNameDoc.addUndoableEditListener(ownUndoListener);

			last.setBorder(BorderFactory.createLoweredBevelBorder());
			flow3.add(last);
			cPanel.add(flow3);

		return panel;
	}

	public void addExtraPanel()
	{
		sPanel.add(createExtraFieldsPanel(), BorderLayout.NORTH);
		SharedContentDialog.this.pack();
		SharedContentDialog.this.repaint();
		visible = true;
	}

	public JPanel createCenterPanel()
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		final JPanel extraPanel = createExtraFieldsPanel();

			final JButton button = new JButton("Fields for enclosing container");
			button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{

					//SwingUtilities.invokeLater(new Runnable(){
					//	public void run()
						{
						  if (!visible)
						  {
							sPanel.add(extraPanel, BorderLayout.NORTH);
							SharedContentDialog.this.pack();
							SharedContentDialog.this.repaint();
							visible = true;
						  }
						  else
						  {
							sPanel.remove(extraPanel);
							SharedContentDialog.this.pack();
							SharedContentDialog.this.repaint();
							visible = false;
						  }
						}
					//});
				}
			});
			panel.add(button);

		return panel;
	}

	public boolean nicknameValid()
	{
		boolean nick = false;

		if ((model.getNickname()).compareTo("") != 0)
			nick = true;

		return nick;
	}

	public boolean checkValidity()
	{
		boolean valid = true;

		if (! model.isValid())
			valid = false;

		return valid;
	}

	public boolean agentValid(Document doc1, Document doc2, Document doc3,
			Document doc4, Document doc5, Document doc6, Document doc7)
	{
		if (((doc1.getLength() != 0) || (doc2.getLength() != 0) ||
			(doc3.getLength() != 0) || (doc4.getLength() != 0) ||
			(doc6.getLength() != 0) || (doc7.getLength() != 0)) &&
			(doc5.getLength() == 0))
			return false;
		else
			return true;
	}

	public boolean agentsValid()
	{
		return (agentValid(agent1Doc1, agent1Doc2, agent1Doc3, agent1Doc4,
			               agent1Doc5, agent1Doc6, agent1Doc7) &&
			    agentValid(agent2Doc1, agent2Doc2, agent2Doc3, agent2Doc4,
					       agent2Doc5, agent2Doc6, agent2Doc7) &&
			    agentValid(agent3Doc1, agent3Doc2, agent3Doc3, agent3Doc4,
				           agent3Doc5, agent3Doc6, agent3Doc7) &&
				agentValid(agent4Doc1, agent4Doc2, agent4Doc3, agent4Doc4,
				           agent4Doc5, agent4Doc6, agent4Doc7) &&
				agentValid(agent5Doc1, agent5Doc2, agent5Doc3, agent5Doc4,
					       agent5Doc5, agent5Doc6, agent5Doc7) &&
			    agentValid(agent6Doc1, agent6Doc2, agent6Doc3, agent6Doc4,
					       agent6Doc5, agent6Doc6, agent6Doc7));
	}

	public void editContentTree()
	{
		infocardBuilder.modifyInfocard(mainFr.infomlFile.
				findInfocard(contentcardModel));

		infocardBuilder.setContent();
		infocardBuilder.setTitle();
		infocardBuilder.setSelectors();
		infocardBuilder.setNotes1();
		infocardBuilder.setNotes2();
		infocardBuilder.setCreator(mainFr.setupModel);
		infocardBuilder.setDateCreated();
		infocardBuilder.setExactQuotation();
		infocardBuilder.setPages();
		infocardBuilder.setContainerNamePart1();
		infocardBuilder.setContainerNamePart2();
		infocardBuilder.setContainerDate();
		infocardBuilder.setAgentX(model.getAgent1());
		infocardBuilder.agentType = null;
		infocardBuilder.setAgentX(model.getAgent2());
		infocardBuilder.agentType = null;
		infocardBuilder.setAgentX(model.getAgent3());
		infocardBuilder.agentType = null;
		infocardBuilder.setAgentX(model.getAgent4());
		infocardBuilder.agentType = null;
		infocardBuilder.setEnclosingAgentX(model.getAgent5());
		infocardBuilder.agentType = null;
		infocardBuilder.setEnclosingAgentX(model.getAgent6());
		infocardBuilder.agentType = null;
		infocardBuilder.setEnclosingContainerVolumeIssue();
		infocardBuilder.setEnclosingContainerTitle();
		infocardBuilder.setCorporateAgent();

		mainFr.infomlFile.hasChanged = true;
	}

	public JPanel createSouthPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());

		JPanel panelB = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(panelB, BorderLayout.SOUTH);

		if (!edit)
		{
			JButton continueButton = new JButton(" Continue ");
			continueButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
				  listener.setAgents();
				  if (! nicknameValid())
				  {
					JOptionPane.showMessageDialog(SharedContentDialog.this,
						"Please add text to the nickname field", "Validation Error",
								JOptionPane.ERROR_MESSAGE);
				  }
				  else
				  {
					if (checkValidity())
					{
					  if (model.dateValid())
						if (((dateRole.getText().compareTo("") != 0)
						 && (model.titleValid())) || (dateRole.getText().compareTo("") == 0))
						{
						  if (agentsValid())
						  {
							if (model.enclosingSourceValid())
							{
							// placed the model in a Map bound to the nickname value
							mainFr.sharedContent.put(model.getNickname(), model);

							// 	add nickname to a String Vector
							mainFr.nicknames.add(model.getNickname());

							SharedContentDialog.this.dispose();
							mainFr.quickContentCardAction.setEnabled(true);

							UniqueContentModel uniqueModel =
													new UniqueContentModel();
							InfocardBuilder infocardBuilder =
								new InfocardBuilder(mainFr.infomlFile, uniqueModel);
							UniqueContentDialog unique =
									new UniqueContentDialog(mainFr,
						            "New Content Card : Unique content", uniqueModel,
						            infocardBuilder, mainFr, false);

							//mainFr.equalizer.setModifiedContentcardModel
							//	((UniqueContentModel) contentcardModel);
							//mainFr.equalizer.equalizeContentcards();

							mainFr.undoAction.setEnabled(false);
							mainFr.redoAction.setEnabled(false);
							mainFr.focusedDialog = null;
							}
							else
							{
								JOptionPane.showMessageDialog(SharedContentDialog.this,
							    "At least one of Volume/Issue fields must be \n" +
								"edited along with agent(s) and/or " +
								"EnclosingContainerTitle. \n",
								"Validation Error", JOptionPane.ERROR_MESSAGE);
							}
						  }
						  else
						  {
							  new Flipping().start();

							  JOptionPane.showMessageDialog(SharedContentDialog.this,
							  "Agents must always have a surname. \n" +
							  "Please check the agents in both the container \n" +
							  "and the enclosing container for possible errors.",
							  "Validation Error", JOptionPane.ERROR_MESSAGE);
						  }
						}
						else
						{
							JOptionPane.showMessageDialog(SharedContentDialog.this,
							"The date in the form refers to the Container title \n" +
							"Please add text to the - Container Title - text fields",
							"Validation Error",	JOptionPane.ERROR_MESSAGE);
						}
					  else
					  {
						JOptionPane.showMessageDialog(SharedContentDialog.this,
						"This combination of year, month and day is invalid \n" +
						"Please enter one of the following : year; or year and month; \n" +
						"or year, month and day.",
						"Invalid Date",	JOptionPane.ERROR_MESSAGE);
					  }
					}
					else
					{
						JOptionPane.showMessageDialog(SharedContentDialog.this,
							"Please add text to at least one field in this window in " +
							"addition to the nickname field", "Validation Error",
										JOptionPane.ERROR_MESSAGE);
					}
				  }
				}
			});
			panelB.add(continueButton);
		}
		else
		{
			JButton done = new JButton(" Done ");
			done.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
				  listener.setAgents();

				  if (! nicknameValid())
				  {
					JOptionPane.showMessageDialog(SharedContentDialog.this,
						"Please add text to the nickname field", "Validation Error",
								JOptionPane.ERROR_MESSAGE);
				  }
				  else
				  {
					if (checkValidity())
					{

					 // if (model.titleValid())
						if (model.dateValid())
						{
						  if (agentsValid())
						  {
							if (model.enclosingSourceValid())
							{

							editContentTree();


							/* If the nickname has been edited and changed
							 * don't forget to remove the associated entrySet
							 * from the mainFr.sharedContent map.
							 */

							MainFrame.sharedContent.remove(model.getNickname());
							MainFrame.sharedContent.put(model.getNickname(), model);

							MainFrame.nicknames.remove(oldNickname);
							MainFrame.nicknames.add(model.getNickname());
							//mainFr.nicknameKeeper.saveNicknames();
							//mainFr.nicknameKeeper.getNicknames();

							mainFr.nicknameKeeper.nickToId.remove
								(contentcardModel.getCardId());
							mainFr.nicknameKeeper.nickToId.put
								(contentcardModel.getCardId(), model.getNickname());

							mainFr.equalizer.setModifiedContentcardModel
										((UniqueContentModel) contentcardModel);
							mainFr.equalizer.setModifiedInfomlType(
								(InfomlType) mainFr.infocards.get(contentcardModel));
							mainFr.equalizer.equalizeContentcards();

							//mainFr.equalizer.flag = 1;
							mainFr.equalizer.run();

							SharedContentDialog.this.dispose();
							mainFr.quickContentCardAction.setEnabled(true);

							mainFr.undoAction.setEnabled(false);
							mainFr.redoAction.setEnabled(false);
							mainFr.focusedDialog = null;
							}
							else
							{
								JOptionPane.showMessageDialog(SharedContentDialog.this,
								"At least one of Volume/Issue fields must be \n" +
								"edited along with agent(s) and/or " +
								"EnclosingContainerTitle. \n", "Validation Error",
								JOptionPane.ERROR_MESSAGE);
							}
						  }
						  else
						  {
							  new Flipping().start();

							  JOptionPane.showMessageDialog(SharedContentDialog.this,
							  "Agents must always have a surname. \n" +
							  "Please check the agents in both the container \n" +
							  "and the enclosing container for possible errors.",
							  "Validation Error", JOptionPane.ERROR_MESSAGE);
						  }
						}
						else
						{
							JOptionPane.showMessageDialog(SharedContentDialog.this,
							"The date in the form refers to the Container title \n" +
							"Please add text to the - Container Title - text fields",
							"Validation Error",	JOptionPane.ERROR_MESSAGE);
						}
					  /*else
					  {
						JOptionPane.showMessageDialog(SharedContentDialog.this,
						"This combination of year, month and day is invalid \n" +
						"Please enter one of the following : year; or year and month; \n" +
						"or year, month and day.",
						"Invalid Date",	JOptionPane.ERROR_MESSAGE);
					  }*/
					}
					else
					{
						JOptionPane.showMessageDialog(SharedContentDialog.this,
							"Please add text to at least one field in this window in " +
							"addition to the nickname field", "Validation Error",
										JOptionPane.ERROR_MESSAGE);
					}
				  }
				}
			});
			panelB.add(done);
		}

			JButton cancel = new JButton(" Cancel ");
			cancel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					SharedContentDialog.this.dispose();
					mainFr.undoAction.setEnabled(false);
					mainFr.redoAction.setEnabled(false);
					mainFr.focusedDialog = null;

					AncestorDialog.viewableInstances--;
				}
			});
			panelB.add(cancel);

		return panel;
	}

	class Flipping extends Thread
	{
		public void run()
		{
		  try
		  {
		    JTextField tfield0 = (JTextField) flickeringTextFields.elementAt(0);
		    JTextField tfield1 = (JTextField) flickeringTextFields.elementAt(1);
		    JTextField tfield2 = (JTextField) flickeringTextFields.elementAt(2);
		    JTextField tfield3 = (JTextField) flickeringTextFields.elementAt(3);
		    JTextField tfield4 = (JTextField) flickeringTextFields.elementAt(4);
		    JTextField tfield5 = (JTextField) flickeringTextFields.elementAt(5);

		    Vector missingLast = new Vector();

		    if (! agentValid(agent1Doc1, agent1Doc2, agent1Doc3, agent1Doc4,
		               agent1Doc5, agent1Doc6, agent1Doc7))
	    		missingLast.addElement(tfield0);
	    	if (! agentValid(agent2Doc1, agent2Doc2, agent2Doc3, agent2Doc4,
				       agent2Doc5, agent2Doc6, agent2Doc7))
	    		missingLast.addElement(tfield1);
	    	if (! agentValid(agent3Doc1, agent3Doc2, agent3Doc3, agent3Doc4,
			           agent3Doc5, agent3Doc6, agent3Doc7))
	    		missingLast.addElement(tfield2);
	    	if (! agentValid(agent4Doc1, agent4Doc2, agent4Doc3, agent4Doc4,
			           agent4Doc5, agent4Doc6, agent4Doc7))
	    		missingLast.addElement(tfield3);
	    	if (! agentValid(agent5Doc1, agent5Doc2, agent5Doc3, agent5Doc4,
				       agent5Doc5, agent5Doc6, agent5Doc7))
	    		missingLast.addElement(tfield4);
	    	if (! agentValid(agent6Doc1, agent6Doc2, agent6Doc3, agent6Doc4,
				       agent6Doc5, agent6Doc6, agent6Doc7))
	    		missingLast.addElement(tfield5);

		    for (int i = 0; i < 6; i++)
		    {
		    	for (int j = 0; j < missingLast.size(); j++)
		    	{
		    		((JTextField) missingLast.elementAt(j)).
		    									setBackground(Color.GREEN);
		    	}

		    	sleep(70);

		    	for (int j = 0; j < missingLast.size(); j++)
		    	{
		    		((JTextField) missingLast.elementAt(j)).
		    									setBackground(Color.WHITE);
		    	}

		    	sleep(150);
		    }
		  }
		  catch (Exception e)
		  { e.printStackTrace(); }
		}
	}

	public MaskFormatter createFormatter(String s)
	{
		MaskFormatter formatter = null;

		try
		{
			formatter = new MaskFormatter(s);
			formatter.setValidCharacters("0123456789");
		}
		catch (Exception e)
		{ e.printStackTrace(); }

		return formatter;

	}

	public void propertyChange(PropertyChangeEvent e)
	{
		Object source = e.getSource();

		/*if (source == date1)
		{
			if (date1.getValue() != null)
			{
				String date1Str = (String) date1.getValue();

				Integer date1Int = new Integer(date1Str);

				model.setDate1(date1Int.intValue());
			}
		}
		if (source == date2)
		{
			if (date2.getValue() != null)
			{
				String date2Str = (String) date2.getValue();

				Integer date2Int = new Integer(date2Str);

				model.setDate2(date2Int.intValue());
			}
		}*/
		if (source == date3)
		{
			if (date3.getValue() != null)
			{
				String date3Str = (String) date3.getValue();

				Integer date3Int = new Integer(date3Str);

				model.setDate3(date3Int.intValue());
			}
		}
	}
}
