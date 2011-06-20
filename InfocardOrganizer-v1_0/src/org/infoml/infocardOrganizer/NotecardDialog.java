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
import java.util.*;
import java.io.*;

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import org.infoml.jaxb.*;
import org.infoml.jaxb.impl.*;


public class NotecardDialog extends AncestorDialog
{
	MyBorder myBorder;
	MainFrame mainFr;
	Main infomlFile;
	InfocardBuilder infocardBuilder;

	private int preferredWidth = 0;

	// indicates if the "more fields" section is visible to the user
	private boolean visible = false;
	JPanel mainPanel;
	JPanel sPanel;

	// used to set the height of the corresponding labels
	private int titleHeight;
	private int contentHeight;
	private int keywordsHeight;
	private int notes1Height;

	/* The boolean xxxField indicates a change of the xxx component's Document
	 * <=> text has been added/removed to the xxx component of the GUI.
	 *
	 * true  - the xxx component's Document has changed <=> there was user action
	 *														in that text component
	 * false - the xxx component's Document hasn't changed <=>
	 * 						no user action in that text component
	 */
	public boolean titleField = false
	 			  , contentField = false
	 			  , keywordsField = false
	 			  , notes1Field = false
	 			  , notes2Field = false;

	NotecardModel model;

	JTextField title;
	JTextArea keywords, content, notes2, notes1;
	JButton moreFields, create, cancel;

	Document titleDoc, contentDoc, keywordsDoc, notes1Doc, notes2Doc;

	/* This field indicates if the model contains any data or not.
	 * if  edit == true   the model is not an "empty model";
	 * if  edit == false  the model has just been created and
	 * 					  all its fields are blank;
	 */
	boolean edit;

	/* The NotecardDialog has a mainPanel with a BorderLayout with 3 main subpanels :
	 * 	 -	the NorthPanel with a BorderLayout
	 *   -  the CenterPanel with a FlowLayout
	 *   -  the SouthPanel with a BorderLayout
	 */
	JScrollPane scrollPane1;

	public NotecardDialog(Frame owner, String title, NotecardModel model,
					InfocardBuilder infocardBuilder, MainFrame mainFr, boolean edit)
	{
		super(owner, title, mainFr);

		//setUndecorated(true);
		//getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

		this.setAlwaysOnTop(false);

		this.model = model;
		this.infocardBuilder = infocardBuilder;
		infocardBuilder.model = model;
		this.mainFr = mainFr;
		infomlFile = mainFr.infomlFile;
		myBorder = new MyBorder();

		this.edit = edit;

		mainPanel = new JPanel(new BorderLayout(0, 10));
		this.getContentPane().add(mainPanel);
		mainPanel.setBorder(createBorder());
		mainPanel.add(createNorthPanel(), BorderLayout.NORTH);

		sPanel = createSouthPanel();
		mainPanel.add(sPanel, BorderLayout.SOUTH);
		mainPanel.add(createCenterPanel(), BorderLayout.CENTER);

		ownUndoManager.discardAllEdits();

		setFocusVector();


		this.setResizable(false);

		this.pack();
		this.setVisible(true);
	}

	public Border createBorder()
	{
		Border inner = BorderFactory.createTitledBorder(
				BorderFactory.createLoweredBevelBorder(), "   Notecard   ");

		Border cb = BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 15, 15, 15), inner);

		return cb;
	}

	/* This method creates and adds the title textField to the cPanel
	 */
	public void makeTitleTextField(JPanel parent)
	{
		JPanel intermediar = new JPanel(new GridLayout());

		intermediar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		title = new JTextField(40);

		title.addCaretListener(mainFr.caretListener);
		title.addFocusListener(mainFr.focusListener);

		if (edit)
		{
			if (! model.getTitle().equals("No title"))
				title.setText(model.getTitle());

		}

		MyDocumentListener listener = new MyDocumentListener(this, model, "title");
		titleDoc = title.getDocument();
		titleDoc.addDocumentListener(listener);
		titleDoc.addUndoableEditListener(ownUndoListener);

		intermediar.add(title);
		parent.add(intermediar);
		title.setBorder(BorderFactory.createLoweredBevelBorder());

		titleHeight = intermediar.getPreferredSize().height;

		intermediar.setMaximumSize(new Dimension(600,
				intermediar.getPreferredSize().height));
		intermediar.setMinimumSize(new Dimension(300,
				intermediar.getPreferredSize().height));

	}

	/* This method creates and adds the content textArea to the cPanel
	 */
	public void makeContentTextArea(JPanel parent)
	{
		//JPanel intermediar = new JPanel(new FlowLayout());

		content = new JTextArea(14, 50);

		contentDoc = content.getDocument();
		content.setLineWrap(true);
		content.setWrapStyleWord(true);
		invertFocusTraversalBehaviour(content);

		content.addCaretListener(mainFr.caretListener);
		content.addFocusListener(mainFr.focusListener);

		MyDocumentListener listener1 = new MyDocumentListener(this, model,
				new String("content"));
		contentDoc.addDocumentListener(listener1);
		contentDoc.addUndoableEditListener(ownUndoListener);

		if (edit)
		{
			content.setText(model.getContentString());
		}

		JScrollPane scrollPane1 = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane1.setBorder(myBorder.getBorder());

		scrollPane1.setMaximumSize(new Dimension(600,
				scrollPane1.getPreferredSize().height));
		scrollPane1.setMinimumSize(new Dimension(300,
				scrollPane1.getPreferredSize().height));

		//intermediar.add(scrollPane1);
		//parent.add(intermediar);

		parent.add(scrollPane1);

		contentHeight = scrollPane1.getPreferredSize().height;
	}

	/* This method creates and adds the keywords textArea to the cPanel
	 */
	public void makeKeywordsTextArea(JPanel parent)
	{
		//JPanel intermediar = new JPanel(new FlowLayout());

		keywords = new JTextArea(3, 40);

		keywords.setLineWrap(true);
		keywords.setWrapStyleWord(true);
		invertFocusTraversalBehaviour(keywords);

		keywords.addFocusListener(mainFr.focusListener);
		keywords.addCaretListener(mainFr.caretListener);

		keywordsDoc = keywords.getDocument();
		MyDocumentListener listener2 = new MyDocumentListener(this, model,
												new String("keywords"));
		keywordsDoc.addDocumentListener(listener2);

		keywordsDoc.addUndoableEditListener(ownUndoListener);

		if (edit)
			keywords.setText(model.getSelectorsString());

		JScrollPane scrollPane2 = new JScrollPane(keywords, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane2.setBorder(myBorder.getBorder());

		/*scrollPane2.setMaximumSize(new Dimension(600,
				scrollPane2.getPreferredSize().height));
		scrollPane2.setMinimumSize(new Dimension(300,
				scrollPane2.getPreferredSize().height));*/

		//intermediar.add(scrollPane2);
		//parent.add(intermediar);

		parent.add(scrollPane2);

		keywordsHeight = scrollPane2.getPreferredSize().height;
	}

	/* This method creates and adds the notes1 textArea to the cPanel
	 */
	public void makeNotes1TextArea(JPanel parent)
	{
		notes1 = new JTextArea(4, 40);

		notes1.setLineWrap(true);
		notes1.setWrapStyleWord(true);
		invertFocusTraversalBehaviour(notes1);

		notes1.addFocusListener(mainFr.focusListener);
		notes1.addCaretListener(mainFr.caretListener);

		if (edit) notes1.setText(model.getNotes1String());

		notes1Doc = notes1.getDocument();
		MyDocumentListener listener3 = new MyDocumentListener(this, model,
												new String("notes1"));
		notes1Doc.addDocumentListener(listener3);
		notes1Doc.addUndoableEditListener(ownUndoListener);

		JScrollPane scrollPane3 = new JScrollPane(notes1, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane3.setBorder(myBorder.getBorder());

		/*scrollPane3.setMaximumSize(new Dimension(600,
				scrollPane3.getPreferredSize().height));
		scrollPane3.setMinimumSize(new Dimension(300,
				scrollPane3.getPreferredSize().height));*/

		parent.add(scrollPane3);

		notes1Height = scrollPane3.getPreferredSize().height;
	}

	public void makeTitleLabel(JPanel parent)
	{
		JPanel panelA = new JPanel();
		JLabel titleL = new JLabel(" Title ");
		panelA.add(titleL);

		panelA.setPreferredSize(new Dimension(
			titleL.getPreferredSize().width, titleHeight));

		//panelA.setMaximumSize(panelA.getPreferredSize());
		parent.add(panelA);
		if (titleL.getPreferredSize().width > preferredWidth)
			preferredWidth = titleL.getPreferredSize().width;
	}

	public void makeContentLabel(JPanel parent)
	{
		JPanel panelB = new JPanel();
		JLabel contentL = new JLabel(" Content ");
		panelB.add(contentL);

		contentL.setPreferredSize(new Dimension(
			contentL.getPreferredSize().width, contentHeight));

		parent.add(panelB);
		if (contentL.getPreferredSize().width > preferredWidth)
			preferredWidth = contentL.getPreferredSize().width;
	}

	public void makeKeywordsLabel(JPanel parent)
	{
		JPanel panelC = new JPanel();
		JLabel keywordsL = new JLabel("   Tags   ");
		panelC.add(keywordsL);

		keywordsL.setPreferredSize(new Dimension(
			keywordsL.getPreferredSize().width, keywordsHeight));

		parent.add(panelC);
		if (keywordsL.getPreferredSize().width > preferredWidth)
			preferredWidth = keywordsL.getPreferredSize().width;
	}

	public void makeNotes1Label(JPanel parent)
	{
		JPanel panelD = new JPanel();
		JLabel notes1L = new JLabel(" Notes1 ");
		panelD.add(notes1L);

		notes1L.setPreferredSize(new Dimension(
			notes1L.getPreferredSize().width, notes1Height));

		parent.add(panelD);
		if (notes1L.getPreferredSize().width > preferredWidth)
			preferredWidth = notes1L.getPreferredSize().width;
	}

	/*	The NorthPanel has a BorderLayout with 2 main subpanels
	 *     -  the cPanel with a BoxLayout
	 * 	   -  the wPanel with a BoxLayout
	 */
	public JPanel createNorthPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());

		// the cPanel handles the text components
		JPanel cPanel = new JPanel();
		cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.Y_AXIS));
		panel.add(cPanel, BorderLayout.CENTER);

			makeTitleTextField(cPanel);

			// creation of the content textArea
			makeContentTextArea(cPanel);

			// creation of the keywords textArea
			makeKeywordsTextArea(cPanel);

			// creation of the notes1 textArea
			makeNotes1TextArea(cPanel);

		// the wPanel handles the labels
		JPanel wPanel = new JPanel();
		wPanel.setLayout(new BoxLayout(wPanel, BoxLayout.Y_AXIS));
		wPanel.setAlignmentY(Component.RIGHT_ALIGNMENT);
		panel.add(wPanel, BorderLayout.WEST);

			// creation of the title label
			makeTitleLabel(wPanel);

			// creation of the content label
			makeContentLabel(wPanel);

			// creation of the keywords label
			makeKeywordsLabel(wPanel);

			// creation of the notes1 label
			makeNotes1Label(wPanel);

		return panel;
	}

	/*  This method checks if it makes sens to build an infoml from the fields
	 * 	provided in the GUI
	 */
	/**
	 * @return <code>true</code> if infocard data is valid
	 */
	private boolean checkValidity()
	{
		boolean valid = false;

		if ((model.getTitle().compareTo("") != 0) || (model.getContentList().isEmpty() == false)
		|| (model.getNotes1List().isEmpty()) || (model.getNotes2List().isEmpty())
		|| ((model.getSelectorsList()).isEmpty() == false))
			valid = true;

		return valid;
	}

	/**
	 * creates new notecard infocard
	 * @see infocardBuilder
	 */
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

		mainFr.infomlFile.hasChanged = true;
	}

	public void editContentTree()
	{
		infocardBuilder.modifyInfocard(infomlFile.findInfocard(model));

		infocardBuilder.setContent();
		infocardBuilder.setTitle();
		infocardBuilder.setSelectors();
		infocardBuilder.setNotes1();
		infocardBuilder.setNotes2();
		infocardBuilder.setCreator(mainFr.setupModel);
		infocardBuilder.setDateCreated();

		mainFr.infomlFile.hasChanged = true;
	}

	boolean createContent()
	{
		 if (mainFr.setupModel.setupInf())
		  {
			if ((model.getSelectorsString() != null) &&
				(model.getSelectorsString().compareTo("") != 0))
				model.setSelectorsList(model.getSelectorsString());

			if ((model.getContentString() != null) &&
					(model.getContentString().compareTo("") != 0))
				model.setContentList(model.getContentString());

			if ((model.getNotes1String() != null) &&
					(model.getNotes1String().compareTo("") != 0))
				model.setNotes1List(model.getNotes1String());

			if ((model.getNotes2String() != null) &&
					(model.getNotes2String().compareTo("") != 0))
				model.setNotes2List(model.getNotes2String());

			if (checkValidity())
			{

				infocardBuilder.generateCardId();
				MainFrame.setupModel.increaseSequenceNumber("notecarddialog");
				// serialize the new number sequence
				MainFrame.serializator.run();

				mainFr.infoWindow.addNode(model, 1);

				// create the content tree
				buildContentTree();

				//mainFr.equalizer.setModifiedNotecardModel(model);
				//mainFr.equalizer.equalizeNotecards();

				mainFr.equalizer.setModifiedNotecardModel(model);
				//mainFr.equalizer.flag = 0;
				mainFr.equalizer.run();

				NotecardDialog.this.dispose();
				mainFr.doc = null;
				mainFr.undoAction.setEnabled(false);
				mainFr.redoAction.setEnabled(false);
				mainFr.focusedDialog = null;
			}
			else
				JOptionPane.showMessageDialog(NotecardDialog.this,
			"Please add text to at least one of the fields", "Validation Error",
				JOptionPane.ERROR_MESSAGE);
		  }
		 else
		  {
				JOptionPane pane = new JOptionPane(
				"This program needs certain setup information \n" +
				"before it can create infocards. \n" +
				"Click OK to open the Setup Information Dialog",
				JOptionPane.ERROR_MESSAGE);

				JDialog dialog = pane.createDialog(NotecardDialog.this,
						"Setup Information Missing");
				dialog.setVisible(true);

				while (pane.getValue() == JOptionPane.UNINITIALIZED_VALUE);

				if (pane.getValue() != null)
				{ SetupInformationDialog2 setup =
					new SetupInformationDialog2(NotecardDialog.this,
							"Setup Information", mainFr);
				}

				return false;
		  }

		 return true;
	}

	void editContent()
	{
		 model.getSelectorsList().clear();
		  model.getContentList().clear();
		  model.getNotes1List().clear();

		  if (mainFr.setupModel.setupInf())
		  {
			if (model.getSelectorsString() != null) //&&
				//(model.getSelectorsString().compareTo("") != 0))
				model.setSelectorsList(model.getSelectorsString());

			if ((model.getContentString() != null) &&
					(model.getContentString().compareTo("") != 0))
				model.setContentList(model.getContentString());

			if ((model.getNotes1String() != null) &&
					(model.getNotes1String().compareTo("") != 0))
				model.setNotes1List(model.getNotes1String());

			if ((model.getNotes2String() != null) &&
					(model.getNotes2String().compareTo("") != 0))
				model.setNotes2List(model.getNotes2String());

			if (checkValidity())
			{
				// create the content tree
				editContentTree();

				mainFr.infoWindow.editNode(model);

				mainFr.equalizer.setMainFr(mainFr);
				mainFr.equalizer.setModifiedNotecardModel(model);
				mainFr.equalizer.setModifiedInfomlType(
						(InfomlType) mainFr.infocards.get(model));
				mainFr.equalizer.equalizeNotecards();

				//mainFr.equalizer.flag = 0;
				//mainFr.equalizer.run();

				NotecardDialog.this.dispose();
				mainFr.doc = null;
				mainFr.undoAction.setEnabled(false);
				mainFr.redoAction.setEnabled(false);
				mainFr.focusedDialog = null;
			}
			else
				JOptionPane.showMessageDialog(NotecardDialog.this,
			"Please add text to at least one of the fields", "Validation Error",
				JOptionPane.ERROR_MESSAGE);
		  }
		  else
		  {
				JOptionPane pane = new JOptionPane(
				"This program needs certain setup information \n" +
				"before it can create infocards. \n" +
				"Click OK to open the Setup Information Dialog",
				JOptionPane.ERROR_MESSAGE);

				JDialog dialog = pane.createDialog(NotecardDialog.this,
						"Setup Information Missing");
				dialog.setVisible(true);

				while (pane.getValue() == JOptionPane.UNINITIALIZED_VALUE);

				if (pane.getValue() != null)
				{ SetupInformationDialog2 setup =
					new SetupInformationDialog2(NotecardDialog.this,
							"Setup Information", mainFr);
				}
		  }
	}

	public JPanel createSouthPanel()
	{
		// you set the red green blue from here
		Color color1 = new Color(100, 25, 60);
		Color color2 = new Color(60, 25, 100);

		JPanel panel = new JPanel(new BorderLayout());

		JPanel panelB = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(panelB, BorderLayout.SOUTH);

		/*JPanel grid = new JPanel(new GridLayout(2, 1));
		panelB.add(grid);

		JLabel label = new JLabel("Create  associated  entry and ");
		label.setFont(new Font("Serif", Font.ITALIC, 11));
		label.setForeground(color2);
		grid.add(label);

		JLabel label2 = new JLabel("open dialog for another one");
		label2.setFont(new Font("Serif", Font.ITALIC, 11));
		label2.setForeground(color2);
		grid.add(label2);

		JLabel im = new JLabel(new ImageIcon
				("resources" + File.separator + "Play16.gif"));
		panelB.add(im);*/

		JButton another = new JButton("Done/Another");
		another.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if (!edit)
				{
			        createContent();

					if (mainFr.setupModel.setupInf())
						mainFr.doSave();

					NotecardModel model = new NotecardModel();

					infocardBuilder = new InfocardBuilder(infomlFile, model);
					NotecardDialog dialog = new NotecardDialog(mainFr,
							"New Notecard",	model, infocardBuilder, mainFr, false);

				}
				else
				{
					editContent();
					mainFr.doSave();

					NotecardModel model = new NotecardModel();

					infocardBuilder = new InfocardBuilder(infomlFile, model);
					NotecardDialog dialog = new NotecardDialog(mainFr,
							"New Notecard",	model, infocardBuilder, mainFr, false);
				}
			}
		});
		another.setForeground(color2);
		panelB.add(another);
		panelB.add(new JLabel("                  "));


		/*JPanel grid2 = new JPanel(new GridLayout(0, 1));
		panelB.add(grid2);

		JLabel label3 = new JLabel("      Just create associated entry");
		label3.setFont(new Font("Serif", Font.ITALIC, 11));
		label3.setForeground(color1);
		grid2.add(label3);

		JLabel im2 = new JLabel(new ImageIcon
				("resources" + File.separator + "Play16.gif"));
		panelB.add(im2);*/

		if (!edit)
		{
			create = new JButton(" Create ");
			create.setForeground(color1);
			create.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					createContent();

					if (mainFr.setupModel.setupInf())
						mainFr.doSave();
				}
			});
			panelB.add(create);
		}
		else
		{
			create = new JButton(" Done ");
			create.setForeground(color1);
			create.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					editContent();

					mainFr.doSave();
				}
			});
			panelB.add(create);
		}



			cancel = new JButton(" Cancel ");
			cancel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					NotecardDialog.this.dispose();
					//mainFr.doc = null;
					mainFr.undoAction.setEnabled(false);
					mainFr.redoAction.setEnabled(false);
					mainFr.focusedDialog = null;

					AncestorDialog.viewableInstances--;
				}
			});

			panelB.add(cancel);

		return panel;
	}

	public JPanel createPanelA()
	{
		JPanel panelA = new JPanel(new BorderLayout());

		JPanel cPanel = new JPanel();
		cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.Y_AXIS));
		panelA.add(cPanel, BorderLayout.CENTER);

			notes2 = new JTextArea(4, 40);
			notes2.setLineWrap(true);
			notes2.setWrapStyleWord(true);
			invertFocusTraversalBehaviour(notes2);

			notes2.addFocusListener(mainFr.focusListener);
			notes2.addCaretListener(mainFr.caretListener);

			if (edit) notes2.setText(model.getNotes2String());

			MyDocumentListener listener4 =
								new MyDocumentListener(this, model, "notes2");
			notes2Doc = notes2.getDocument();
			notes2Doc.addDocumentListener(listener4);
			notes2Doc.addUndoableEditListener(ownUndoListener);

			JScrollPane scrollPane = new JScrollPane(notes2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			cPanel.add(scrollPane);
			scrollPane.setBorder(myBorder.getBorder());

			//scrollPane.setMaximumSize(new Dimension(600,
				//	scrollPane.getPreferredSize().height));

		JPanel wPanel = new JPanel();
		wPanel.setLayout(new BoxLayout(wPanel, BoxLayout.Y_AXIS));
		wPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		panelA.add(wPanel, BorderLayout.WEST);


			JPanel panelD = new JPanel();
			JLabel notes2L = new JLabel(" Notes2 ", SwingConstants.CENTER);
			notes2L.setPreferredSize(new Dimension(
					preferredWidth, notes1Height));
			panelD.add(notes2L);
			//notes2L.setPreferredSize(new Dimension(
				//notes2L.getPreferredSize().width, notes1Height));//notes2.getPreferredSize().height
			wPanel.add(panelD);

		return panelA;
	}

	/*	This method adds an action listener to the "more fields" button
	 * 	which implements the following behaviour :
	 *    - if the "more fields" panel is part of the GUI -> remove it from the GUI
	 *    - if the "more fields" panel is not part of the GUI -> add it to the GUI
	 */
	public JPanel createCenterPanel()
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		final JPanel panelA = createPanelA();

			moreFields = new JButton(" More fields ");
			moreFields.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
				/* It is not good practice to block the awt event-dipatching thread
				 * with time consuming tasks;
				 */
					SwingUtilities.invokeLater(new Runnable(){
						public void run()
						{
						  if (!visible)
						  {
							sPanel.add(panelA, BorderLayout.NORTH);
							NotecardDialog.this.pack();
							NotecardDialog.this.repaint();
							visible = true;
						  }
						  else
						  {
							sPanel.remove(panelA);
							NotecardDialog.this.pack();
							NotecardDialog.this.repaint();
							visible = false;
						  }
						}
					});
				}
			});
			panel.add(moreFields);

	    panel.setMaximumSize(new Dimension(600, panel.getPreferredSize().height));
	    panel.setMinimumSize(new Dimension(300, panel.getPreferredSize().height));

		return panel;
	}

	public void setFocusVector()
	{
		focusableComponents.addElement(title);
		focusableComponents.addElement(content);
		focusableComponents.addElement(keywords);
		focusableComponents.addElement(notes1);
		focusableComponents.addElement(moreFields);
		focusableComponents.addElement(notes2);
		focusableComponents.addElement(create);
		focusableComponents.addElement(cancel);
	}

	public static void invertFocusTraversalBehaviour(JTextArea textArea)
	{
		Set forwardKeys  = textArea.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set backwardKeys = textArea.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);

		// check that we WANT to modify current focus traversal keystrokes
		if (forwardKeys.size() != 1 || backwardKeys.size() != 1) return;
		final AWTKeyStroke fks = (AWTKeyStroke) forwardKeys.iterator().next();
		final AWTKeyStroke bks = (AWTKeyStroke) backwardKeys.iterator().next();
		final int fkm = fks.getModifiers();
		final int bkm = bks.getModifiers();
		final int ctrlMask      = KeyEvent.CTRL_MASK+KeyEvent.CTRL_DOWN_MASK;
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
		newForwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0));
		textArea.setFocusTraversalKeys(
			KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
			Collections.unmodifiableSet(newForwardKeys)
		);
		// bind our new backward focus traversal keys
		Set newBackwardKeys = new HashSet(1);
		newBackwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB,
				KeyEvent.SHIFT_MASK + KeyEvent.SHIFT_DOWN_MASK));
		textArea.setFocusTraversalKeys(
			KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
			Collections.unmodifiableSet(newBackwardKeys)
		);

		//TextInserter.applyTabBinding(textArea);
	}

	public static class TextInserter extends AbstractAction
	{
		private JTextArea textArea;
		private String insertable;

		private TextInserter(JTextArea textArea, String insertable)
		{
			this.textArea   = textArea;
			this.insertable = insertable;
		}

		public static void applyTabBinding(JTextArea textArea)
		{
			textArea.getInputMap(JComponent.WHEN_FOCUSED)
						.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
						KeyEvent.CTRL_MASK + KeyEvent.CTRL_DOWN_MASK),"tab");
			textArea.getActionMap()
			        .put("tab", new TextInserter(textArea, "\t"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			textArea.insert(insertable, textArea.getCaretPosition());
		}
	}

}

