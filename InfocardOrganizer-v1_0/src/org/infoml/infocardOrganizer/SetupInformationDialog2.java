package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;


public class SetupInformationDialog2 extends JDialog
{
	// uses a NotecardModel for storing the data
	NotecardModel setupModel;
	MainFrame mainFr;
	SetupInformationListener listener;
	Document honorificDoc, firstDoc, middle1Doc, middle2Doc, lastDoc,
			 punctuationDoc, suffixDoc, numberDoc, emailDoc, /*domainDoc,*/ yahooIdDoc;
	//JTextField numberSequence;
	//JTextField domain;
	JTextField email;
	JTextField id;

	public SetupInformationDialog2(Frame owner, String title, MainFrame mainFr)
	{
		super(owner, title);

		this.mainFr = mainFr;
		this.setupModel = mainFr.setupModel;
		listener = new SetupInformationListener(SetupInformationDialog2.this);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.getContentPane().add(panel);

		panel.add(createNorthPanel(), BorderLayout.NORTH);
		panel.add(createCenterPanel(), BorderLayout.CENTER);
		panel.add(createSouthPanel(), BorderLayout.SOUTH);

		this.pack();
		this.setVisible(true);
	}

	public SetupInformationDialog2(JDialog owner, String title, MainFrame mainFr)
	{
		super(owner, title);

		this.mainFr = mainFr;
		this.setupModel = mainFr.setupModel;
		listener = new SetupInformationListener(this);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.getContentPane().add(panel);

		panel.add(createNorthPanel(), BorderLayout.NORTH);
		panel.add(createCenterPanel(), BorderLayout.CENTER);
		panel.add(createSouthPanel(), BorderLayout.SOUTH);

		this.pack();
		this.setVisible(true);
	}

	public JPanel createNorthPanel()
	{
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));

		JLabel label = new JLabel("The following information is needed " +
							"to insure that your infocards have a unique ID");
		panel.add(label);

		return panel;
	}

	public JPanel makeNameUnitPanel(String nameUnit, int size, int nr)
	{
		JPanel grid = new JPanel(new GridLayout(2, 1));

		JTextField unit = new JTextField(size);
		if (nr == 1)
		{
			honorificDoc = unit.getDocument();
			honorificDoc.addDocumentListener(listener);
			if (MainFrame.setupModel.getHonorific().compareTo("") != 0)
			{
				unit.setText(MainFrame.setupModel.getHonorific());
			}
		}
		if (nr == 2)
		{
			firstDoc = unit.getDocument();
			firstDoc.addDocumentListener(listener);
			if (MainFrame.setupModel.getFirst().compareTo("") != 0)
			{
				unit.setText(MainFrame.setupModel.getFirst());
			}
		}
		if (nr == 3)
		{
			middle1Doc = unit.getDocument();
			middle1Doc.addDocumentListener(listener);
			if (MainFrame.setupModel.getMiddle1().compareTo("") != 0)
			{
				unit.setText(MainFrame.setupModel.getMiddle1());
			}
		}
		if (nr == 4)
		{
			middle2Doc = unit.getDocument();
			middle2Doc.addDocumentListener(listener);
			if (MainFrame.setupModel.getMiddle2().compareTo("") != 0)
			{
				unit.setText(MainFrame.setupModel.getMiddle2());
			}
		}
		if (nr == 5)
		{
			lastDoc = unit.getDocument();
			lastDoc.addDocumentListener(listener);
			if (MainFrame.setupModel.getSurname().compareTo("") != 0)
			{
				unit.setText(MainFrame.setupModel.getSurname());
			}
		}
		if (nr == 6)
		{
			punctuationDoc = unit.getDocument();
			punctuationDoc.addDocumentListener(listener);
			if (MainFrame.setupModel.getPunctuation().compareTo("") != 0)
			{
				unit.setText(MainFrame.setupModel.getPunctuation());
			}
		}
		if (nr == 7)
		{
			suffixDoc = unit.getDocument();
			suffixDoc.addDocumentListener(listener);
			if (MainFrame.setupModel.getSuffix().compareTo("") != 0)
			{
				unit.setText(MainFrame.setupModel.getSuffix());
			}
		}
		if (nr == 8)
		{
			numberDoc = unit.getDocument();
			numberDoc.addDocumentListener(listener);

			//unit.setText(MainFrame.setupModel.getNumber());
			//numberSequence = unit;
		}
		unit.setBorder(BorderFactory.createLoweredBevelBorder());
		grid.add(unit);

		JLabel label = new JLabel("<html><font size=-2>" + nameUnit + "</font> </html>",
											SwingConstants.CENTER);
		grid.add(label);

		return grid;
	}

	public JPanel createCenterPanel()
	{
		JPanel panel = new JPanel(new GridLayout(0, 1));

		// create the name panel
		JPanel flow1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(flow1);

			JPanel grid = new JPanel(new GridLayout(2, 1));
			grid.add(new JLabel("   Your name  :   "));
			JLabel empty = new JLabel("<html><font size=-2>   </font> </html>",
															SwingConstants.CENTER);
			grid.add(empty);

			flow1.add(grid);

			flow1.add(makeNameUnitPanel("honorific", 5, 1));

			flow1.add(makeNameUnitPanel("first", 10, 2));

			flow1.add(makeNameUnitPanel("middle", 8, 3));

			flow1.add(makeNameUnitPanel("middle", 8, 4));

			flow1.add(makeNameUnitPanel("surname", 12, 5));

			flow1.add(makeNameUnitPanel("punctuation", 3, 6));

			flow1.add(makeNameUnitPanel("suffix", 4, 7));

		JPanel flow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		flow2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		panel.add(flow2);

			/*JLabel label = new JLabel("<html>\n <p> Next available </p>" +
						" \n <p> sequence number    : </p>", SwingConstants.CENTER);
			label.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 25));
			flow2.add(label);


			//flow2.add(makeNameUnitPanel("(if in doubt enter '1')", 5, 8));*/


		return panel;
	}

	public JPanel createSouthPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());

		JPanel cPanel = new JPanel(new GridLayout(0, 1));
		panel.add(cPanel, BorderLayout.CENTER);

			ButtonGroup group = new ButtonGroup();
			JRadioButton rb1 = new JRadioButton("<html>\n <p> An e-mail address </p>" +
			" \n <p> that you own </p>");
			//JRadioButton rb2 = new JRadioButton("<html>\n <p> A domain that </p>" +
			//" \n <p> you own </p>");
			JRadioButton rb3 = new JRadioButton("<html>\n <p> A yahoo.com id</p>" +
			" \n <p>that you own </p>");

			/*domain = new JTextField(20);
			domain.setEditable(false);
			if (MainFrame.setupModel.getDomain().compareTo("") != 0)
			{
				domain.setText(MainFrame.setupModel.getDomain());
				domain.setEditable(true);
				rb2.setSelected(true);
			}

			domainDoc = domain.getDocument();
			domainDoc.addDocumentListener(listener);*/

			email = new JTextField(25);
			email.setEditable(false);
			if (MainFrame.setupModel.getEmail().compareTo("") != 0)
			{
				email.setText(MainFrame.setupModel.getEmail());
				email.setEditable(true);
				rb1.setSelected(true);
			}

			emailDoc = email.getDocument();
			emailDoc.addDocumentListener(listener);

			id = new JTextField(15);
			id.setEditable(false);
			if (MainFrame.setupModel.getYahooId().compareTo("") != 0)
			{
				id.setText(MainFrame.setupModel.getYahooId());
				id.setEditable(true);
				rb3.setSelected(true);
			}

			yahooIdDoc = id.getDocument();
			yahooIdDoc.addDocumentListener(listener);

			JPanel flow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
			cPanel.add(flow1);

			// rb1
			int maxWidth = rb1.getPreferredSize().width;
			group.add(rb1);
			flow1.add(rb1);

			// email
			email.setBorder(BorderFactory.createLoweredBevelBorder());
			flow1.add(email);

			// rb1
			rb1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					email.setEditable(true);
					//domain.setEditable(false);
					//domain.setText("");
					id.setEditable(false);
					id.setText("");
				}
			});

			//JPanel flow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
			//cPanel.add(flow2);


			// rb2
			/*rb2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					email.setEditable(false);
					email.setText("");
					domain.setEditable(true);
					id.setEditable(false);
					id.setText("");
				}
			});
			rb2.setPreferredSize(new Dimension(maxWidth, rb2.getPreferredSize().height));
			group.add(rb2);
			flow2.add(rb2);*/

			// domain
			//domain.setBorder(BorderFactory.createLoweredBevelBorder());
			//flow2.add(domain);

			JPanel flow3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
			cPanel.add(flow3);

			// rb3
			rb3.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					email.setEditable(false);
					email.setText("");
					//domain.setEditable(false);
					//domain.setText("");
					id.setEditable(true);
				}
			});
			rb3.setPreferredSize(new Dimension(maxWidth, rb3.getPreferredSize().height));
			group.add(rb3);
			flow3.add(rb3);

			// id
			id.setBorder(BorderFactory.createLoweredBevelBorder());
			flow3.add(id);

			JLabel yahoo = new JLabel("@yahoo.com ");
			flow3.add(yahoo);

			JPanel flow4 = new JPanel();
			cPanel.add(flow4);

			JLabel label = new JLabel("<html><font size=-2>Entries cannot begin with a numeral or contain a period (.) before the '@'</font> </html>",
					SwingConstants.CENTER);
			flow4.add(label);

			MainFrame.setupModel.setSessionEmail("");
			MainFrame.setupModel.setSessionDomain("");
			MainFrame.setupModel.setSessionYahooId("");

	    JPanel wPanel = new JPanel(new GridLayout());
		panel.add(wPanel, BorderLayout.WEST);

		    JLabel choose1 = new JLabel("   Choose one  :  ", SwingConstants.CENTER);
		    choose1.setBorder(BorderFactory.createEmptyBorder(0, 5, 45, 30));
		    wPanel.add(choose1);

		JPanel sPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(sPanel, BorderLayout.SOUTH);

			JButton done = new JButton(" Done ");
			done.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
				  try
				  {
					if (lastDoc.getText(0, lastDoc.getLength()).compareTo("")  != 0)
					{
					  if (/*(domainDoc.getText(0, domainDoc.getLength()).compareTo("") == 0)
					   && */(emailDoc.getText(0, emailDoc.getLength()).compareTo("") == 0)
					   && (yahooIdDoc.getText(0, yahooIdDoc.getLength()).compareTo("") == 0))
					  {
						  JOptionPane.showMessageDialog(SetupInformationDialog2.this,
						"Please select one of the three options \n" +
						"and complete text field to the right", "Validation Error",
													JOptionPane.ERROR_MESSAGE);

					  }
					  else
					  {
						setupModel.setHonorific(honorificDoc.getText(0, honorificDoc.getLength()));
						setupModel.setFirst(firstDoc.getText(0, firstDoc.getLength()));
						setupModel.setMiddle1(middle1Doc.getText(0, middle1Doc.getLength()));
						setupModel.setMiddle2(middle2Doc.getText(0, middle2Doc.getLength()));
						setupModel.setPunctuation(punctuationDoc.getText(0, punctuationDoc.getLength()));
						setupModel.setSuffix(suffixDoc.getText(0, suffixDoc.getLength()));

						if (setupModel.getSessionNumber().compareTo("") != 0)
						{
							setupModel.setNumber(setupModel.getSessionNumber());
						}
						if (setupModel.getSessionEmail().compareTo("") != 0)
						{
							setupModel.setEmail(setupModel.getSessionEmail());
							setupModel.setDomain("");
							setupModel.setYahooId("");
						}
						if (setupModel.getSessionDomain().compareTo("") != 0)
						{
							setupModel.setDomain(setupModel.getSessionDomain());
							setupModel.setEmail("");
							setupModel.setYahooId("");
						}
						if (setupModel.getSessionYahooId().compareTo("") != 0)
						{
							setupModel.setYahooId(setupModel.getSessionYahooId());
							setupModel.setDomain("");
							setupModel.setEmail("");
						}

						SetupInformationDialog2.this.dispose();

						MainFrame.serializator.run();
					  }
					}
					else
					{
						JOptionPane.showMessageDialog(SetupInformationDialog2.this,
						"Please add text to the 'surname' text field", "Validation Error",
										JOptionPane.ERROR_MESSAGE);
					}
				  }
				  catch (Exception excep)
				  { excep.printStackTrace(); }
				}
			});
			sPanel.add(done);

			JButton cancel = new JButton(" Cancel ");
			cancel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					SetupInformationDialog2.this.dispose();

					/*SwingUtilities.invokeLater(new Runnable(){

					public void run()
					{
						try
						{
							honorificDoc.remove(0, honorificDoc.getLength());
							firstDoc.remove(0, firstDoc.getLength());
							middle1Doc.remove(0, middle1Doc.getLength());
							middle2Doc.remove(0, middle2Doc.getLength());

							lastDoc.remove(0, lastDoc.getLength());

							punctuationDoc.remove(0, punctuationDoc.getLength());
							suffixDoc.remove(0, suffixDoc.getLength());

							numberSequence.setText(MainFrame.setupModel.getNumber());

							//if (MainFrame.setupModel.getSessionEmail().compareTo("") != 0)
							{
								email.setText(MainFrame.setupModel.getEmail());
							}

							//if (MainFrame.setupModel.getSessionDomain().compareTo("") != 0)
							{
								domain.setText(MainFrame.setupModel.getDomain());
							}

							//if (MainFrame.setupModel.getSessionYahooId().compareTo("") != 0)
							{
								id.setText(MainFrame.setupModel.getYahooId());
							}
						}
						catch(Exception excep)
						{ excep.printStackTrace(); }
					}
					});*/
				}
			});
			sPanel.add(cancel);

		return panel;
	}
}
