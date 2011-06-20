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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import javax.swing.tree.DefaultMutableTreeNode;
import org.infoml.jaxb.*;
import org.infoml.jaxb.impl.*;


public class UniqueContentDialog extends AncestorDialog
{
	MainFrame mainFr;
	JPanel sPanel;
	JTextField to;
	MyBorder myBorder;
	UniqueContentModel model;
	UniqueContentListener listener;
	InfocardBuilder infocardBuilder;

	// this variable is used to make all the labels in the left side of the gui
	// be the same width.

	private int preferredWidth = 0;

	/* this set of variables is used to make each label corresponding
	 * to the text field/area in its left and that particular
	 * text field/area be the same height.
	 */
	private int nicknameHeight = 0;
	private int titleHeight = 0;
	private int contentHeight = 0;
	private int pagesHeight = 0;
	private int keywordsHeight = 0;
	private int notes1Height = 0;

	private boolean visible = false;

	Document titleDoc, contentDoc, page1Doc, page2Doc, keywordsDoc, notes1Doc, notes2Doc;

	/* This field indicates if the model contains any data or not.
	 * if  edit == true   the model is not an "empty model";
	 * if  edit == false  the model has just been created and
	 * 					  all its fields are blank;
	 */
	boolean edit;

	JComboBox chooseNickname = null;
	JTextField titleF;

	public UniqueContentDialog(Frame owner, String title, UniqueContentModel model,
			InfocardBuilder infocardBuilder,  MainFrame mainFr, boolean edit)
	{
		super(owner, title, mainFr);

		this.setAlwaysOnTop(false);

		this.mainFr = mainFr;
		this.model = model;
		this.infocardBuilder = infocardBuilder;
		this.edit = edit;

		myBorder = new MyBorder();
		listener = new UniqueContentListener(this);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(createBorder());
		this.getContentPane().add(panel);

		panel.add(createNorthPanel(), BorderLayout.NORTH);
		panel.add(createCenterPanel(), BorderLayout.CENTER);

		sPanel = createSouthPanel();
		panel.add(sPanel, BorderLayout.SOUTH);

		this.pack();
		this.setVisible(true);

		titleF.grabFocus();
	}

	public Border createBorder()
	{
		Border inner = BorderFactory.createTitledBorder(
				BorderFactory.createLoweredBevelBorder(), "      Unique  Content      ");

		Border cb = BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(10, 15, 5, 15), inner);

		return cb;
	}

	public void makeNicknameList(JPanel parent)
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 3, 0));
		parent.add(panel);

		chooseNickname = new JComboBox(mainFr.nicknames);

		if (model.shared == null)
		{
			chooseNickname.setSelectedIndex(MainFrame.nicknames.size()-1);
			model.setShared((String) MainFrame.nicknames.lastElement(),
													MainFrame.sharedContent);
		}
		else
		{
			chooseNickname.setSelectedItem(model.shared.getNickname());
		}

		chooseNickname.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String sel = (String) chooseNickname.getSelectedItem();

				model.setShared(sel, MainFrame.sharedContent);
			}
		});
		panel.add(chooseNickname);

		nicknameHeight = chooseNickname.getPreferredSize().height;
	}

	public void makeContentcardTitle(JPanel parent)
	{
		JPanel grid = new JPanel(new GridLayout());
		grid.setBorder(BorderFactory.createEmptyBorder(13, 5, 10, 5));
		parent.add(grid);

		titleF = new JTextField(20);

		titleF.addFocusListener(mainFr.focusListener);
		titleF.addCaretListener(mainFr.caretListener);

		if (edit)
		  if (! model.getTitle().equals("No title"))
			titleF.setText(model.getTitle());

		titleDoc = titleF.getDocument();
		titleDoc.addDocumentListener(listener);
		titleDoc.addUndoableEditListener(ownUndoListener);

		titleF.setBorder(BorderFactory.createLoweredBevelBorder());
		grid.add(titleF);

		titleHeight = grid.getPreferredSize().height;
	}

	public void makeContentTextArea(JPanel parent)
	{
		JTextArea content = new JTextArea(12, 50);
		content.setLineWrap(true);
		content.setWrapStyleWord(true);
		invertFocusTraversalBehaviour(content);

		content.addFocusListener(mainFr.focusListener);
		content.addCaretListener(mainFr.caretListener);

		if (edit) content.setText(model.getContentString());

		contentDoc = content.getDocument();
		contentDoc.addDocumentListener(listener);
		contentDoc.addUndoableEditListener(ownUndoListener);

		JScrollPane sPane = new JScrollPane(content);
		parent.add(sPane);
		sPane.setBorder(myBorder.getBorder());

		contentHeight = sPane.getPreferredSize().height;
	}

	public void makePagesPanel(JPanel parent)
	{
		JPanel pages = new JPanel(new FlowLayout(FlowLayout.LEFT));

		final JTextField from = new JTextField(5);
		from.addCaretListener(mainFr.caretListener);
		from.addFocusListener(mainFr.focusListener);

		to = new JTextField(5);
		to.addFocusListener(mainFr.focusListener);
		to.addCaretListener(mainFr.caretListener);

		if (edit)
		{
			from.setText(model.getPage1());
			if (model.getPage1().compareTo("") != 0)
				to.setEditable(true);
		}

		page1Doc = from.getDocument();
		page1Doc.addDocumentListener(listener);
		page1Doc.addUndoableEditListener(ownUndoListener);

		from.setBorder(BorderFactory.createLoweredBevelBorder());
		pages.add(from);

		JLabel label = new JLabel(" through ");
		pages.add(label);


		if (edit) to.setText(model.getPage2());

		to.setEditable(false);

		page2Doc = to.getDocument();
		page2Doc.addDocumentListener(listener);
		page2Doc.addUndoableEditListener(ownUndoListener);

		to.setBorder(BorderFactory.createLoweredBevelBorder());
		pages.add(to);

		parent.add(pages);

		pagesHeight = pages.getPreferredSize().height;
	}

	public void makeKeywordsTextArea(JPanel parent)
	{
		JTextArea keywords = new JTextArea(3, 40);
		keywords.setLineWrap(true);
		keywords.setWrapStyleWord(true);
		invertFocusTraversalBehaviour(keywords);

		keywords.addFocusListener(mainFr.focusListener);
		keywords.addCaretListener(mainFr.caretListener);

		if (edit) keywords.setText(model.getSelectorsString());

		keywordsDoc = keywords.getDocument();
		keywordsDoc.addDocumentListener(listener);
		keywordsDoc.addUndoableEditListener(ownUndoListener);

		JScrollPane scrollPane2 = new JScrollPane(keywords);
		parent.add(scrollPane2);
		scrollPane2.setBorder(myBorder.getBorder());

		keywordsHeight = scrollPane2.getPreferredSize().height;
	}

	public void makeNotes1TextArea(JPanel parent)
	{
		JTextArea notes1 = new JTextArea(3, 40);
		notes1.setLineWrap(true);
		notes1.setWrapStyleWord(true);
		invertFocusTraversalBehaviour(notes1);

		notes1.addFocusListener(mainFr.focusListener);
		notes1.addCaretListener(mainFr.caretListener);

		if (edit) notes1.setText(model.getNotes1String());

		notes1Doc = notes1.getDocument();
		notes1Doc.addDocumentListener(listener);
		notes1Doc.addUndoableEditListener(ownUndoListener);

		JScrollPane scrollPane3 = new JScrollPane(notes1);
		parent.add(scrollPane3);
		scrollPane3.setBorder(myBorder.getBorder());

		notes1Height = scrollPane3.getPreferredSize().height;
	}

	public void makeNicknameLabel(JPanel parent)
	{
		JPanel panel = new JPanel();
		JLabel nick = new JLabel("", SwingConstants.CENTER);
		nick.setText("<html>\n <p> Use shared </p> \n <p> content </p>");
		panel.add(nick);

		parent.add(panel);
		if (nick.getPreferredSize().width > preferredWidth)
			preferredWidth = nick.getPreferredSize().width;
	}

	public void makeTitleLabel(JPanel parent)
	{
		JPanel panel = new JPanel();
		JLabel title = new JLabel("", SwingConstants.CENTER);
		title.setText("<html>\n <p> Content card </p> \n <p> title </p>");
		panel.add(title);

		parent.add(panel);
		if (title.getPreferredSize().width > preferredWidth)
			preferredWidth = title.getPreferredSize().width;
	}

	public void makeContentLabel(JPanel parent)
	{
		JPanel panel = new JPanel();
		JLabel content = new JLabel("   Content   ");
		content.setPreferredSize(new Dimension(content.getPreferredSize().width,
				contentHeight));
		panel.add(content);

		parent.add(panel);

		if (content.getPreferredSize().width > preferredWidth)
			preferredWidth = content.getPreferredSize().width;
	}

	public void makePagesLabel(JPanel parent)
	{
		JPanel panel = new JPanel();
		JLabel pages = new JLabel(" Page(s) ");
		panel.setPreferredSize(new Dimension(pages.getPreferredSize().width,
				pagesHeight));
		panel.add(pages);

		parent.add(panel);

		if (pages.getPreferredSize().width > preferredWidth)
			preferredWidth = pages.getPreferredSize().width;
	}

	public void makeKeywordsLabel(JPanel parent)
	{
		JPanel panel = new JPanel();
		JLabel keywords = new JLabel("   Tags   ");
		panel.add(keywords);

		keywords.setPreferredSize(new Dimension(
			keywords.getPreferredSize().width, keywordsHeight));

		parent.add(panel);
		if (keywords.getPreferredSize().width > preferredWidth)
			preferredWidth = keywords.getPreferredSize().width;
	}

	public void makeNotes1Label(JPanel parent)
	{
		JPanel panel = new JPanel();
		JLabel notes1 = new JLabel(" Notes1 ");
		panel.add(notes1);

		notes1.setPreferredSize(new Dimension(
			notes1.getPreferredSize().width, notes1Height));

		parent.add(panel);
		if (notes1.getPreferredSize().width > preferredWidth)
			preferredWidth = notes1.getPreferredSize().width;
	}

	public JPanel createNorthPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());

		JPanel cPanel = new JPanel();
		cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.Y_AXIS));
		panel.add(cPanel, BorderLayout.CENTER);

			// create nickname pop-up list
			makeNicknameList(cPanel);

			// create content card title
			makeContentcardTitle(cPanel);

			// create content text area
			makeContentTextArea(cPanel);

			// create pages panel
			makePagesPanel(cPanel);

			// create keywords text area
			makeKeywordsTextArea(cPanel);

			makeNotes1TextArea(cPanel);

		JPanel wPanel = new JPanel();
		wPanel.setLayout(new BoxLayout(wPanel, BoxLayout.Y_AXIS));
		panel.add(wPanel, BorderLayout.WEST);

			makeNicknameLabel(wPanel);

			makeTitleLabel(wPanel);

			makeContentLabel(wPanel);

			makePagesLabel(wPanel);

			makeKeywordsLabel(wPanel);

			makeNotes1Label(wPanel);

		return panel;
	}

	public JPanel createPanelA()
	{
		JPanel panelA = new JPanel(new BorderLayout());

		JPanel cPanel = new JPanel();
		cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.Y_AXIS));
		panelA.add(cPanel, BorderLayout.CENTER);

			// make radio button panel
			JPanel rbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			cPanel.add(rbPanel);

			ButtonGroup group = new ButtonGroup();

			JRadioButton yes = new JRadioButton(" Yes ");
			yes.setSelected(true);
			yes.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					model.setExact(true);
				}
			});
			group.add(yes);
			rbPanel.add(yes);

			JRadioButton no = new JRadioButton(" No ");
			no.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					model.setExact(false);
				}
			});
			group.add(no);
			rbPanel.add(no);

			// make notes2 text field
			JTextArea notes2 = new JTextArea(3, 50);
			notes2.setLineWrap(true);
			notes2.setWrapStyleWord(true);
			invertFocusTraversalBehaviour(notes2);

			notes2.addFocusListener(mainFr.focusListener);
			notes2.addCaretListener(mainFr.caretListener);

			if (edit) notes2.setText(model.getNotes2String());

			notes2Doc = notes2.getDocument();
			notes2Doc.addDocumentListener(listener);
			notes2Doc.addUndoableEditListener(ownUndoListener);

			JScrollPane scrollPane= new JScrollPane(notes2);
			cPanel.add(scrollPane);
			scrollPane.setBorder(myBorder.getBorder());

		JPanel wPanel = new JPanel();
		wPanel.setLayout(new BoxLayout(wPanel, BoxLayout.Y_AXIS));
		panelA.add(wPanel, BorderLayout.WEST);

			JPanel panel = new JPanel();

			JLabel quotation = new JLabel("", SwingConstants.CENTER);
			quotation.setText("<html>\n <p> Exact </p> \n <p> quotation </p>");
			quotation.setPreferredSize(new Dimension(preferredWidth,
					rbPanel.getPreferredSize().height));

			panel.add(quotation);

			wPanel.add(panel);

			// make notes2 label
			JPanel panelD = new JPanel();
			JLabel notes2L = new JLabel(" Notes2 ", SwingConstants.CENTER);
			notes2L.setPreferredSize(new Dimension(preferredWidth, notes1Height));
			panelD.add(notes2L);
			wPanel.add(panelD);

		return panelA;
	}

	public JPanel createCenterPanel()
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		final JPanel panelA = createPanelA();

			final JButton button = new JButton(" More fields ");
			button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
				/* It is not good practice to block the awt event-dipatching thread
				 * with time consuming tasks; this is why invokeLater is called.
				 */
					SwingUtilities.invokeLater(new Runnable(){
						public void run()
						{
						  if (!visible)
						  {
							sPanel.add(panelA, BorderLayout.NORTH);
							UniqueContentDialog.this.pack();
							UniqueContentDialog.this.repaint();
							visible = true;
						  }
						  else
						  {
							sPanel.remove(panelA);
							UniqueContentDialog.this.pack();
							UniqueContentDialog.this.repaint();
							visible = false;
						  }
						}
					});
				}
			});
			panel.add(button);

		return panel;
	}

	public void buildContentTree()
	{
		infocardBuilder.setStandardInfocard();

		infocardBuilder.setCardId();

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
		infocardBuilder.setAgentX(model.shared.getAgent1());
		infocardBuilder.agentType = null;
		infocardBuilder.setAgentX(model.shared.getAgent2());
		infocardBuilder.agentType = null;
		infocardBuilder.setAgentX(model.shared.getAgent3());
		infocardBuilder.agentType = null;
		infocardBuilder.setAgentX(model.shared.getAgent4());
		infocardBuilder.agentType = null;
		infocardBuilder.setEnclosingAgentX(model.shared.getAgent5());
		infocardBuilder.agentType = null;
		infocardBuilder.setEnclosingAgentX(model.shared.getAgent6());
		infocardBuilder.agentType = null;
		infocardBuilder.setEnclosingContainerVolumeIssue();
		infocardBuilder.setEnclosingContainerTitle();
		infocardBuilder.setCorporateAgent();

		mainFr.infomlFile.hasChanged = true;
	}

	public void editContentTree()
	{
		infocardBuilder.modifyInfocard(mainFr.infomlFile.findInfocard(model));

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
		infocardBuilder.setAgentX(model.shared.getAgent1());
		infocardBuilder.agentType = null;
		infocardBuilder.setAgentX(model.shared.getAgent2());
		infocardBuilder.agentType = null;
		infocardBuilder.setAgentX(model.shared.getAgent3());
		infocardBuilder.agentType = null;
		infocardBuilder.setAgentX(model.shared.getAgent4());
		infocardBuilder.agentType = null;
		infocardBuilder.setEnclosingAgentX(model.shared.getAgent5());
		infocardBuilder.agentType = null;
		infocardBuilder.setEnclosingAgentX(model.shared.getAgent6());
		infocardBuilder.agentType = null;
		infocardBuilder.setEnclosingContainerVolumeIssue();
		infocardBuilder.setEnclosingContainerTitle();
		infocardBuilder.setCorporateAgent();

		mainFr.infomlFile.hasChanged = true;
	}

	void createContent()
	{
		if (mainFr.setupModel.setupInf())
		  {
			if (model.getSelectorsString().compareTo("") != 0)
				model.setSelectorsList(model.getSelectorsString());

			if (model.getContentString().compareTo("") != 0)
				model.setContentList(model.getContentString());

			if ((model.getNotes1String() != null) &&
					(model.getNotes1String().compareTo("") != 0))
				model.setNotes1List(model.getNotes1String());

			if ((model.getNotes2String() != null) &&
					(model.getNotes2String().compareTo("") != 0))
				model.setNotes2List(model.getNotes2String());

			model.shared.formatDate();

			if (! model.isEmpty())
			{
				infocardBuilder.generateCardId();

				MainFrame.setupModel.increaseSequenceNumber("unique");
				// serialize the new sequence number
				MainFrame.serializator.run();

				mainFr.infoWindow.addNode(model, 2);
				mainFr.infomlFile.addInfocard(infocardBuilder.infocard, model);

				buildContentTree();

				mainFr.nicknameKeeper.nickToId.put
					(model.getCardId(), model.shared.getNickname());

				//MainFrame.infocardRecoverer.situation = 0;
				//MainFrame.infocardRecoverer.writeInfocard(mainFr.absFilePath,
						//model);

				mainFr.equalizer.setModifiedContentcardModel(model);
				//mainFr.equalizer.equalizeContentcards();

				//mainFr.equalizer.flag = 1;
				mainFr.equalizer.run();

				UniqueContentDialog.this.dispose();

				mainFr.undoAction.setEnabled(false);
				mainFr.redoAction.setEnabled(false);
				mainFr.focusedDialog = null;
			}
			else
			{
				JOptionPane.showMessageDialog(UniqueContentDialog.this,
			    "Content card is empty \n" +
			    "Please add text to at least one of the fields \n " +
			    "in addition to the nickname field ",
			    "Validation Error",	JOptionPane.ERROR_MESSAGE);
			}
		  }
		  else
		  {
			JOptionPane pane = new JOptionPane(
			"This program needs certain setup information \n" +
			"before it can create infocards. \n" +
			"Click OK to open the Setup Information Dialog",
			JOptionPane.ERROR_MESSAGE);

			JDialog dialog = pane.createDialog(UniqueContentDialog.this,
							"Setup Information Missing");
			dialog.setVisible(true);

			while (pane.getValue() == JOptionPane.UNINITIALIZED_VALUE);

			if (pane.getValue() != null)
			{ SetupInformationDialog2 setup =
					new SetupInformationDialog2(UniqueContentDialog.this,
								"Setup Information", mainFr);
			}
		  }
	}

	void editContent()
	{
		model.getSelectorsList().clear();
		  model.getContentList().clear();
		  model.getNotes1List().clear();

		  if (mainFr.setupModel.setupInf())
		  {
			//if (model.getSelectorsString().compareTo("") != 0)
			model.setSelectorsList(model.getSelectorsString());

			if (model.getContentString().compareTo("") != 0)
				model.setContentList(model.getContentString());

			if ((model.getNotes1String() != null) &&
					(model.getNotes1String().compareTo("") != 0))
				model.setNotes1List(model.getNotes1String());

			if ((model.getNotes2String() != null) &&
					(model.getNotes2String().compareTo("") != 0))
				model.setNotes2List(model.getNotes2String());

			//if (model.shared != null)
			model.shared.formatDate();

			if (! model.isEmpty())
			{
				editContentTree();

				mainFr.infoWindow.editNode(model);


				//MainFrame.infocardRecoverer.situation = 0;
				//MainFrame.infocardRecoverer.writeInfocard(mainFr.absFilePath,
						//model);

				mainFr.equalizer.setMainFr(mainFr);
				mainFr.equalizer.setModifiedInfomlType(
						(InfomlType) mainFr.infocards.get(model));
				mainFr.equalizer.setModifiedContentcardModel(model);
				mainFr.equalizer.equalizeContentcards();

				//mainFr.equalizer.flag = 1;
				//mainFr.equalizer.run();

				UniqueContentDialog.this.dispose();

				mainFr.undoAction.setEnabled(false);
				mainFr.redoAction.setEnabled(false);

				mainFr.focusedDialog = null;
			}
			else
			{
				JOptionPane.showMessageDialog(UniqueContentDialog.this,
			    "Content card is empty \n" +
			    "Please add text to at least one of the fields \n " +
			    "in addition to the nickname field ",
			    "Validation Error",	JOptionPane.ERROR_MESSAGE);
			}
		  }
		  else
		  {
			JOptionPane pane = new JOptionPane(
			"This program needs certain setup information \n" +
			"before it can create infocards. \n" +
			"Click OK to open the Setup Information Dialog",
			JOptionPane.ERROR_MESSAGE);

			JDialog dialog = pane.createDialog(UniqueContentDialog.this,
							"Setup Information Missing");
			dialog.setVisible(true);

			while (pane.getValue() == JOptionPane.UNINITIALIZED_VALUE);

			if (pane.getValue() != null)
			{ SetupInformationDialog2 setup =
					new SetupInformationDialog2(UniqueContentDialog.this,
								"Setup Information", mainFr);
			}
		  }
	}

	public JPanel createSouthPanel()
	{
		Color color1 = new Color(100, 10, 75);
		Color color2 = new Color(75, 10, 100);

		JPanel panel = new JPanel(new BorderLayout());

		JPanel panelB = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(panelB, BorderLayout.SOUTH);

		JButton another = new JButton("Done/Another");
		another.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
			  if (!edit)
			  {
				createContent();

				if (mainFr.setupModel.setupInf())
					mainFr.doSave();

				UniqueContentModel uniqueModel =
						new UniqueContentModel();
				InfocardBuilder infocardBuilder =
						new InfocardBuilder(mainFr.infomlFile, uniqueModel);
				UniqueContentDialog unique =
					new UniqueContentDialog(mainFr,
							"New Content Card : Unique content", uniqueModel,
							infocardBuilder, mainFr, false);
			  }
			  else
			  {
					editContent();
					mainFr.doSave();

					UniqueContentModel uniqueModel =
							new UniqueContentModel();
					InfocardBuilder infocardBuilder =
							new InfocardBuilder(mainFr.infomlFile, uniqueModel);
					UniqueContentDialog unique =
						new UniqueContentDialog(mainFr,
								"Edit Content Card : Unique content", uniqueModel,
								infocardBuilder, mainFr, false);
				  }
			}
		});
		another.setForeground(color2);
		panelB.add(another);
		panelB.add(new JLabel("                  "));

		if (!edit)
		{
			JButton create = new JButton(" Create ");
			create.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					createContent();

					if (mainFr.setupModel.setupInf())
						mainFr.doSave();
					/*Thread saver = new Thread(new Runnable(){
						public void run()
						{
							while (!mainFr.setupModel.setupInf())
							  try
							  { Thread.currentThread().sleep(100); }
							  catch (Exception exp)
							  { exp.printStackTrace(); }


							mainFr.doSave();
						}
					});
					saver.start();*/
				}
			});
			create.setForeground(color1);
			panelB.add(create);
		}
		else
		{
			JButton done = new JButton(" Done ");
			done.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					editContent();

					mainFr.doSave();
				}
			});
			done.setForeground(color1);
			panelB.add(done);
		}

			JButton cancel = new JButton(" Cancel ");
			cancel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					UniqueContentDialog.this.dispose();

					mainFr.undoAction.setEnabled(false);
					mainFr.redoAction.setEnabled(false);
					mainFr.focusedDialog = null;

					AncestorDialog.viewableInstances--;
				}
			});
			panelB.add(cancel);

		return panel;
	}

	public static void invertFocusTraversalBehaviour(JTextArea textArea)
	{
		Set forwardKeys  = textArea.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set backwardKeys = textArea.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);

		// check that we WANT to modify current focus traversal keystrokes
		if (forwardKeys.size() != 1 || backwardKeys.size() != 1)
			return;

		final AWTKeyStroke fks = (AWTKeyStroke) forwardKeys.iterator().next();
		final AWTKeyStroke bks = (AWTKeyStroke) backwardKeys.iterator().next();
		final int fkm = fks.getModifiers();
		final int bkm = bks.getModifiers();
		final int ctrlMask      = KeyEvent.CTRL_MASK + KeyEvent.CTRL_DOWN_MASK;
		final int ctrlShiftMask = KeyEvent.SHIFT_MASK+KeyEvent.SHIFT_DOWN_MASK+ctrlMask;
		if (fks.getKeyCode() != KeyEvent.VK_TAB || (fkm & ctrlMask) == 0 || (fkm & ctrlMask) != fkm)
		{	// not currently CTRL+TAB for forward focus traversal
			return;
		}
		if (bks.getKeyCode() != KeyEvent.VK_TAB || (bkm & ctrlShiftMask) == 0 || (bkm & ctrlShiftMask) != bkm)
		{	// not currently CTRL+SHIFT+TAB for backward focus traversal
			return;
		}

		// bind our new forward focus traversal keys
		Set newForwardKeys = new HashSet(1);
		newForwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB,0));
		textArea.setFocusTraversalKeys(
			KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
			Collections.unmodifiableSet(newForwardKeys)
		);
		// bind our new backward focus traversal keys
		Set newBackwardKeys = new HashSet(1);
		newBackwardKeys.add(AWTKeyStroke.getAWTKeyStroke
				(KeyEvent.VK_TAB,KeyEvent.SHIFT_MASK+KeyEvent.SHIFT_DOWN_MASK));
		textArea.setFocusTraversalKeys(
			KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
			Collections.unmodifiableSet(newBackwardKeys)
		);
	}

}




