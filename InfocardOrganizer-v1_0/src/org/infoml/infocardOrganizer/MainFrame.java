package org.infoml.infocardOrganizer;

/*

 Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

 This file is part of Infocard Organizer.

 Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

 Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

 */

import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.codehaus.groovy.control.CompilationFailedException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.lang.Thread;
import java.lang.Exception;
import java.net.URI;
import java.util.*;

import groovy.lang.*;
import groovy.util.*;

public class MainFrame extends JFrame implements WindowListener,
		TreeSelectionListener {
	JPanel panel;
	InfocardWindow infoWindow;
	InfocardBuilder infocardBuilder;
	Main infomlFile;
	static NotecardModel setupModel;
	static Serializator serializator;
	static Deserializator deserializator;
	static String defaultSavePath;
	static String defaultOpenPath;
	static SavePath savePath;
	static LoadPath loadPath;
	static NicknameKeeper nicknameKeeper;

	JMenuItem save;
	JMenuItem saveAs;
	JMenuItem quickContentCard;
	JMenuItem undoMenu;
	JMenuItem editNotecard;
	// JMenuItem editContentcard;
	JMenuItem editSharedContent;
	JMenuItem editSelectedCard;
	JMenuItem left, right;

	// List infocardList;

	String name;
	String normalizedName;
	String namea;
	String activeWindowFilePath;

	static Vector windows;
	JMenu windowMenu;

	/**
	 * This structure maps every InfocardType (a data structure representing an
	 * infocard) in the infoml list to its corresponding NotecardModel object (a
	 * data structure representing the dialog-based data used to build an
	 * infocard). The key is the model and the value is the InfomlType object.
	 */
	Map infocards;

	static Map sharedContent;
	static Vector nicknames;
	Map uniqueContent;
	Map notecards;
	NotecardModel editableNotecardModel;
	UniqueContentModel editableContentModel;

	protected UndoManager undo;
	public UndoAction undoAction;
	public RedoAction redoAction;
	public Document doc;

	AbstractAction newAction, openAction, saveAction, newNotecardAction,
			newContentCardAction, cutAction, copyAction, pasteAction,
			editSelectedCardAction, quickContentCardAction, closeAction,
			quitAction, setupInformationAction, editSharedContentAction,
			exportAction, newFromSelectedAction, duplicateAction;

	JTextComponent focusedTextComponent;
	AncestorDialog focusedDialog;

	MyCaretListener caretListener;
	MyFocusListener focusListener;

	JButton moveRight, moveLeft, moveUp, moveDown, remove, clear, undoInf;
	JLabel dropStatusLabel;

	static int menuHeight;

	static Document document;
	static Vector modifiedCardIds;

	static InfocardEqualizer equalizer;
	static Map pathToOutline;
	PreviousState stateSaver;

	int lastAction;
	boolean undoIsEnabled;

	static String osName;

	AFileFilter infomlFilter;

	static {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"
			// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"
					// UIManager.getSystemLookAndFeelClassName()
					// "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"
					/* UIManager.getCrossPlatformLookAndFeelClassName() */);
		} catch (Exception e) {
			e.printStackTrace();
		}

		osName = System.getProperty("os.name");

		windows = new Vector();
		nicknames = new Vector();
		sharedContent = new HashMap();
		equalizer = new InfocardEqualizer();
		modifiedCardIds = new Vector();

		// SetupInformationDialog2 uses a NotecardModel
		setupModel = new NotecardModel();
		serializator = new Serializator();
		deserializator = new Deserializator();
		deserializator.start();

		nicknameKeeper = new NicknameKeeper();
		nicknameKeeper.getNicknames();

		loadPath = new LoadPath();
		loadPath.start();

		savePath = new SavePath();
		savePath.start();
	}

	/**
	 * Sets up UI, undo, actions, listeners, and file filters.
	 */
	public MainFrame() {
		super("Infocard Organizer : Untitled");

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");

		stateSaver = new PreviousState();

		undo = new UndoManager();

		caretListener = new MyCaretListener(this);
		focusListener = new MyFocusListener(this);

		// creates the context for marshalling infocards

		infomlFile = new Main(this);

		windows.add(this);
		name = new String();

		infomlFilter = new AFileFilter("xml");

		uniqueContent = new HashMap();
		notecards = new HashMap();

		infocards = new HashMap();

		newAction = new NewAction();
		saveAction = new SaveAction();
		openAction = new OpenAction();
		newNotecardAction = new NewNotecardAction();
		newContentCardAction = new NewContentCardAction();
		quickContentCardAction = new QuickContentCardAction();
		undoAction = new UndoAction();
		redoAction = new RedoAction();
		cutAction = new CutAction();
		copyAction = new CopyAction();
		pasteAction = new PasteAction();
		editSelectedCardAction = new EditSelectedCardAction();
		editSharedContentAction = new EditSharedContentAction();
		closeAction = new CloseAction();
		quitAction = new QuitAction();
		setupInformationAction = new SetupInformationAction();
		exportAction = new ExportAction();
		newFromSelectedAction = new NewFromSelectedAction();
		duplicateAction = new DuplicateAction();

		/* create menu bar */
		JMenuBar menuBar = createMenuBar();
		this.setJMenuBar(menuBar);

		/* asemble window from its components */
		panel = new JPanel(new BorderLayout());
		panel.add(infocardManipulatorBar(), BorderLayout.NORTH);
		// ///panel.add(buttonsGrid(), BorderLayout.WEST);

		panel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		panel.setPreferredSize(new Dimension(450, 500));
		this.getContentPane().add(panel, BorderLayout.CENTER);
		makeInfocardWindow();

		this.getContentPane().add(makeToolBar(), BorderLayout.NORTH);
		this.getContentPane().add(suplimentarPanel(), BorderLayout.SOUTH);

		this.setLocation(new Point((windows.size() - 1) * 30,
				(windows.size() - 1) * 30));
		this.addWindowListener(this);
		this.pack();
	}

	public MainFrame(String name) {
		this();

		this.name = new String(name);
		windowMenu.add(new JMenuItem(name));
	}

	public JPanel buttonsGrid() {
		JPanel panel = new JPanel(new GridLayout(0, 1));

		JPanel flow = new JPanel(new FlowLayout());
		panel.add(flow);

		JButton play = new JButton(new ImageIcon("resources/play16.gif"));
		play.setMargin(new Insets(0, 0, 0, 0));
		flow.add(play);

		return panel;
	}

	/**
	 * text buttons that appear just menu bar and row of icon buttons
	 */
	public JPanel infocardManipulatorBar() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		moveRight = new JButton(
				"<html><font size=-2><b> Move right  </b></font> </html>");
		moveRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoWindow.moveNodeToRight();
			}
		});
		moveRight.setMargin(new Insets(0, 0, 0, 0));
		moveRight.setEnabled(false);

		moveLeft = new JButton(
				"<html><font size=-2><b> Move left  </b></font> </html>");
		moveLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoWindow.moveNodeToLeft();
			}
		});
		moveLeft.setMargin(new Insets(0, 0, 0, 0));
		moveLeft.setEnabled(false);
		panel.add(moveLeft);
		panel.add(moveRight);

		remove = new JButton(
				"<html><b><font size=-2>Remove infocard</font></b> </html>");
		remove.setMargin(new Insets(0, 0, 0, 0));
		remove.setMnemonic(KeyEvent.VK_DELETE);
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (infoWindow.tree.getSelectionPath() != null) {
					infoWindow.removeNode();
				}
			}
		});
		remove.setEnabled(false);
		panel.add(remove);

		clear = new JButton(
				"<html><font size=-2><b> Clear  </b></font> </html>");

		undoInf = new JButton(
				"<html><b><font size=-2>    Undo last window item action</font></b> </html>"
		/* , new ImageIcon("resources/UndoInf.gif") */);
		undoInf.setMargin(new Insets(0, 0, 0, 0));
		undoInf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stateSaver.undoState();

				undoInf.setEnabled(false);
			}
		});
		undoInf.setEnabled(false);
		panel.add(undoInf);

		moveRight.setPreferredSize(new Dimension(
				moveRight.getPreferredSize().width,
				undoInf.getPreferredSize().height));
		moveLeft.setPreferredSize(new Dimension(
				moveLeft.getPreferredSize().width,
				undoInf.getPreferredSize().height));
		remove.setPreferredSize(new Dimension(remove.getPreferredSize().width,
				undoInf.getPreferredSize().height));

		return panel;
	}

	/**
	 * icon buttons that appear just menu bar and above row of text buttons
	 */
	public JToolBar makeToolBar() {
		JToolBar toolBar = new JToolBar();

		toolBar.add(newAction);
		toolBar.add(openAction);
		toolBar.add(saveAction);

		toolBar.addSeparator();

		toolBar.add(newNotecardAction);
		toolBar.add(newContentCardAction);
		toolBar.add(quickContentCardAction);

		toolBar.addSeparator();
		toolBar.addSeparator();

		toolBar.add(exportAction);

		toolBar.addSeparator();
		toolBar.addSeparator();

		toolBar.add(undoAction);
		toolBar.add(redoAction);

		toolBar.addSeparator();

		toolBar.add(cutAction);
		toolBar.add(copyAction);
		toolBar.add(pasteAction);

		return toolBar;
	}

	public void setDropStatusText(String text) {
		int linesNr = (text.length() / 70) + 1;
		int max = text.length();

		String[] text2 = new String[10];

		int i = 0;
		while (i < linesNr) {
			String aux = null;

			if (max > 70 * (i + 1))
				aux = text.substring(70 * i, 70 * (i + 1));
			else
				aux = text.substring(70 * i, max);

			aux = aux + "\n";
			text2[i] = aux;

			i++;
		}
		repaint();

		String status = null;
		if (i == 0)
			status = new String("<html><font size=-2><b>" + text
					+ "</b></font> </html>");
		else {
			StringBuffer buffer = new StringBuffer("<html><font "
					+ "size=-2><b>");

			for (int k = 0; k < i; k++)
				buffer = buffer.append(text2[k]
						+ "</b></font> \n <font size=-2><b>");

			buffer = buffer.append("</b></font> </html>");

			status = new String(buffer);
		}
		// status = new String("<html><font size=-2><b>" + text2 + "</b></font>
		// </html>");

		dropStatusLabel.setText(status);
	}

	public JPanel suplimentarPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		JPanel nPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		dropStatusLabel = new JLabel(
				"<html><font size=-2><b> ... </b></font> </html>");
		nPanel.add(dropStatusLabel);

		nPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEmptyBorder(0, 3, 0, 3), BorderFactory
				.createCompoundBorder(BorderFactory.createEtchedBorder(),
						BorderFactory.createLoweredBevelBorder())));

		panel.add(nPanel, BorderLayout.NORTH);

		JPanel sPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
		panel.add(sPanel, BorderLayout.SOUTH);

		moveUp = new JButton("<html><font size=-2>" + " Move Up "
				+ "</font> </html>");
		// panel.add(moveUp);

		moveDown = new JButton("<html><font size=-2>" + " Move Down "
				+ "</font> </html>");
		moveDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoWindow.moveDown();
			}
		});
		moveDown.setMargin(new Insets(0, 0, 0, 0));
		moveDown.setEnabled(false);
		// sPanel.add(moveDown);

		return panel;
	}

	public void setClearEnabled(boolean val) {
		clear.setEnabled(val);
	}

	public JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		// created here because used from open action listener
		windowMenu = new JMenu("Window");

		// the menu File with all its MenuItems
		JMenu file = new JMenu("File");
		menuBar.add(file);

		file.add(newAction);
		file.add(openAction);

		file.addSeparator();

		file.add(saveAction);

		saveAs = new JMenuItem(" Save As...", new ImageIcon(
				"resources/SaveAs24.gif"));
		saveAs.setEnabled(false);
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = 0;
				JFileChooser fchooser = new JFileChooser(defaultSavePath);

				fchooser.setFileFilter(infomlFilter);

				if (defaultSavePath != null) {
					fchooser.setCurrentDirectory(new File(defaultSavePath));
				}

				int returnVal = fchooser.showSaveDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					if (fchooser.getSelectedFile().exists()) {
						int response = JOptionPane
								.showConfirmDialog(
										MainFrame.this,
										new String(
												fchooser.getSelectedFile()
														.getAbsolutePath()
														+ " \n already exists. \n Do you want to replace it ? "),
										"Save as", JOptionPane.YES_NO_OPTION);

						if (response == 1) {
							return;
						}
					}

					String path = fchooser.getSelectedFile().getAbsolutePath();
					name = fchooser.getSelectedFile().getName();

					// if (MainFrame.this.infomlFile.hasChanged)
					{
						MainFrame.this.infomlFile.removeOutlineInfocards();
						MainFrame.this.infoWindow.getOutline();

						InfocardBuilder.buildOutlineInfocards(
								MainFrame.this.infoWindow.nodes,
								MainFrame.this.infoWindow.cardIds,
								MainFrame.this.infoWindow.preOrdCardIds,
								MainFrame.this.infomlFile);
					}

					result = infomlFile.writeInfomlToDiskWithValidating(path);
					defaultSavePath = path;
					defaultOpenPath = path;
					savePath.run();
					activeWindowFilePath = defaultSavePath;
					MainFrame.this.setTitle("Infocard Organizer : " + name);

					if (result == 0)
						JOptionPane.showMessageDialog(MainFrame.this,
								"The system cannot find the specified path \n "
										+ "File not saved", "System error",
								JOptionPane.ERROR_MESSAGE);
					else {
						defaultSavePath = path;

						MainFrame.this.setTitle("Infocard Organizer : " + name);
						// System.out.println(name);

						int index = windows.indexOf(MainFrame.this);

						for (int i = 0; i < windows.size(); i++) {
							JMenuItem menuItem = new JMenuItem(name);
							menuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									MainFrame.this.setState(Frame.NORMAL);
									MainFrame.this.requestFocus();
								}
							});
							((MainFrame) windows.elementAt(i)).windowMenu
									.remove(index);
							((MainFrame) windows.elementAt(i)).windowMenu
									.insert(menuItem, index);
						}

						MainFrame.this.infomlFile.hasChanged = false;
					}
				}
			}
		});
		file.add(saveAs);

		file.add(exportAction);

		file.addSeparator();

		JMenuItem title = new JMenuItem("Title");
		title.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		title.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				ActionEvent.CTRL_MASK));
		// file.add(title);

		file.add(closeAction);
		file.add(quitAction);

		JMenu edit = new JMenu("Edit");

		edit.add(undoAction);
		edit.add(redoAction);

		edit.addSeparator();

		edit.add(cutAction);
		edit.add(copyAction);
		edit.add(pasteAction);

		edit.addSeparator();

		edit.add(setupInformationAction);

		menuBar.add(edit);

		JMenu outline = new JMenu("Outline");
		menuBar.add(outline);

		right = new JMenuItem("Move Right", new ImageIcon(
				"resources/DocumentIn.gif"));
		right.setEnabled(false);
		right.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				ActionEvent.CTRL_MASK));
		right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoWindow.moveNodeToRight();
			}
		});
		outline.add(right);

		left = new JMenuItem("Move Left", new ImageIcon(
				"resources/DocumentOut.gif"));
		left.setEnabled(false);
		left.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				ActionEvent.CTRL_MASK));
		left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoWindow.moveNodeToLeft();
			}
		});
		outline.add(left);

		// the a"Infocards" menu with all its menuItems
		JMenu infCard = new JMenu("Infocards");
		menuBar.add(infCard);

		infCard.add(newNotecardAction);

		infCard.add(newContentCardAction);

		infCard.addSeparator();

		infCard.add(newFromSelectedAction);

		infCard.add(quickContentCardAction);

		infCard.add(duplicateAction);

		infCard.addSeparator();

		/*
		 * editSharedContent = new JMenuItem("Edit Shared Content");
		 * editSharedContent.addActionListener(new ActionListener(){ public void
		 * actionPerformed(ActionEvent e) { if (editableContentModel != null)
		 * editSharedContent(editableContentModel); } });
		 * editSharedContent.setEnabled(false);
		 * editSharedContent.setAccelerator(KeyStroke.getKeyStroke(
		 * KeyEvent.VK_6, ActionEvent.CTRL_MASK));
		 */
		infCard.add(editSharedContentAction);

		/*
		 * editSelectedCard = new JMenuItem("Edit Selected Card");
		 * editSelectedCard.addActionListener(new ActionListener(){ public void
		 * actionPerformed(ActionEvent e) { if (editableContentModel != null)
		 * editUniqueContent(editableContentModel); else if
		 * (editableNotecardModel != null) editNotecard(editableNotecardModel); }
		 * }); //editSelectedCard.setEnabled(false);
		 * editSelectedCard.setAccelerator(KeyStroke.getKeyStroke(
		 * KeyEvent.VK_7, ActionEvent.CTRL_MASK)); infCard.add(editContentcard);
		 */

		// editContentcard = new JMenuItem("Edit Unique Content");
		/*
		 * editContentcard.addActionListener(new ActionListener(){ public void
		 * actionPerformed(ActionEvent e) { if (editableContentModel != null)
		 * editUniqueContent(editableContentModel); } });
		 * editContentcard.setEnabled(false);
		 * editContentcard.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_7,
		 * ActionEvent.CTRL_MASK)); infCard.add(editContentcard);
		 */

		infCard.add(editSelectedCardAction);

		menuBar.add(windowMenu);

		return menuBar;
	}

	/* end of createMenuBar() */

	// used to determine whether the cut, copy actions are enabled\disabled
	public void setTextSelected(boolean value) {
		copyAction.setEnabled(value);
		cutAction.setEnabled(value);
	}

	public void enableSave() {
		saveAction.setEnabled(true);

		// save.setEnabled(true);
		saveAs.setEnabled(true);

		exportAction.setEnabled(true);
	}

	public void makeInfocardWindow() {
		infoWindow = new InfocardWindow(this);

		panel.add(infoWindow.treeView, BorderLayout.CENTER);
	}

	public void editNotecard(NotecardModel editableNotecardModel) {
		infocardBuilder = new InfocardBuilder(infomlFile, editableNotecardModel);

		NotecardDialog dialog = new NotecardDialog(MainFrame.this,
				"Edit Notecard", editableNotecardModel, infocardBuilder,
				MainFrame.this, true);
	}

	public void editSharedContent(UniqueContentModel editableContentModel) {
		infocardBuilder = new InfocardBuilder(infomlFile, editableContentModel);

		SharedContentDialog shared = new SharedContentDialog(MainFrame.this,
				"Edit Shared Content", editableContentModel.shared,
				infocardBuilder, MainFrame.this, true, editableContentModel);
	}

	public void editUniqueContent(UniqueContentModel editableContentModel) {
		infocardBuilder = new InfocardBuilder(infomlFile, editableContentModel);

		UniqueContentDialog unique = new UniqueContentDialog(MainFrame.this,
				"New Content Card : Unique content", editableContentModel,
				infocardBuilder, MainFrame.this, true);
	}

	public void checkMoveRightEnabled(DefaultMutableTreeNode node) {
		if (node.getPreviousSibling() == null) {
			moveRight.setEnabled(false);
			right.setEnabled(false);
		} else {
			moveRight.setEnabled(true);
			right.setEnabled(true);
		}
	}

	public void checkMoveLeftEnabled(DefaultMutableTreeNode node) {
		if (node.getParent() == infoWindow.top) {
			moveLeft.setEnabled(false);
			left.setEnabled(false);
		} else {
			moveLeft.setEnabled(true);
			left.setEnabled(true);
		}
	}

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = null;

		if (e.getNewLeadSelectionPath() != null) {
			node = (DefaultMutableTreeNode) e.getNewLeadSelectionPath()
					.getLastPathComponent();
			infoWindow.tree.thisEditor.myOffset = infoWindow.tree
					.getPathBounds(e.getNewLeadSelectionPath()).x;

			checkMoveRightEnabled(node);

			checkMoveLeftEnabled(node);
		} else {
			remove.setEnabled(false);
		}

		if (node == null) {
			editSelectedCardAction.setEnabled(false);
			// editContentcard.setEnabled(false);
			editSharedContentAction.setEnabled(false);
			newFromSelectedAction.setEnabled(false);
			duplicateAction.setEnabled(false);

			editableNotecardModel = null;
			editableContentModel = null;

			remove.setEnabled(false);
			return;
		}

		if (notecards.containsKey(node)) {
			editableNotecardModel = (NotecardModel) notecards.get(node);
			editableContentModel = null;

			// only one node can be selected
			// editContentcard.setEnabled(false);
			editSharedContentAction.setEnabled(false);
			newFromSelectedAction.setEnabled(false);
			editSelectedCardAction.setEnabled(true);
			duplicateAction.setEnabled(true);

			remove.setEnabled(true);
		} else if (uniqueContent.containsKey(node)) {
			editableContentModel = (UniqueContentModel) uniqueContent.get(node);
			editableNotecardModel = null;

			// only one node can be selected
			// editContentcard.setEnabled(true);
			editSharedContentAction.setEnabled(true);
			newFromSelectedAction.setEnabled(true);
			editSelectedCardAction.setEnabled(true);
			duplicateAction.setEnabled(true);

			remove.setEnabled(true);
		}
	}

	public void doClose() {
		if (AncestorDialog.viewableInstances > 0)
			JOptionPane.showMessageDialog(MainFrame.this,
					"Please close all dialogs before quitting.");
		else {
			Object[] options = { "Yes", "No", "Cancel" };
			String identifier;

			identifier = name;

			int result = 0;

			// only write the file to disk if it has suffered changes
			if (MainFrame.this.infomlFile.hasChanged) {
				result = JOptionPane
						.showOptionDialog(
								MainFrame.this,
								"The document "
										+ identifier
										+ " has been modified.\n Do you want to save your changes ? ",
								"Infocard Organizer",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);

				if (result == JOptionPane.YES_OPTION)
					if (MainFrame.this.infomlFile.if1.getInfoml().isEmpty()) {
						JOptionPane.showMessageDialog(MainFrame.this,
								"Document is empty. \n"
										+ "Document will not be saved");
						int index = windows.indexOf(MainFrame.this);

						for (int i = 0; i < windows.size(); i++) {
							MainFrame frame = ((MainFrame) windows.elementAt(i));

							frame.windowMenu.remove(index);
						}

						windows.remove(MainFrame.this);

						MainFrame.this.dispose();
					} else {
						doSave();

						int index = windows.indexOf(MainFrame.this);

						for (int i = 0; i < windows.size(); i++) {
							MainFrame frame = ((MainFrame) windows.elementAt(i));

							frame.windowMenu.remove(index);
						}

						windows.remove(MainFrame.this);

						MainFrame.this.dispose();
					}
				else if (result == JOptionPane.NO_OPTION) {
					int index = windows.indexOf(MainFrame.this);

					for (int i = 0; i < windows.size(); i++) {
						MainFrame frame = ((MainFrame) windows.elementAt(i));

						frame.windowMenu.remove(index);
					}

					windows.remove(MainFrame.this);

					MainFrame.this.dispose();
				} else if (result == JOptionPane.CANCEL_OPTION)
					;
				else if (result == JOptionPane.CLOSED_OPTION)
					;
			} else {
				int index = windows.indexOf(MainFrame.this);

				for (int i = 0; i < windows.size(); i++) {
					MainFrame frame = ((MainFrame) windows.elementAt(i));

					frame.windowMenu.remove(index);
				}

				windows.remove(MainFrame.this);

				MainFrame.this.dispose();
			}
		}
	}

	public void doSave() {
		int returnVal = 0;
		JFileChooser fchooser = null;

		if (activeWindowFilePath == null) {
			fchooser = new JFileChooser(defaultSavePath);

			if (defaultSavePath != null) {
				fchooser.setCurrentDirectory(new File(defaultSavePath));
			}

			// fchooser.setFileFilter(xmlFilter);

			returnVal = fchooser.showSaveDialog(MainFrame.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if (!fchooser.getSelectedFile().exists()) {
					defaultSavePath = fchooser.getSelectedFile()
							.getAbsolutePath();

					// defaultSavePath = xmlFilter.checkFormat(defaultSavePath);

					defaultOpenPath = defaultSavePath;
				} else {
					int response = JOptionPane
							.showConfirmDialog(
									fchooser,
									new String(
											fchooser.getSelectedFile()
													.getAbsolutePath()
													+ " \n already exists. \n Do you want to replace it ? "),
									"Save as", JOptionPane.YES_NO_OPTION);
					if (response == 1) {
						return;
					}

					defaultSavePath = fchooser.getSelectedFile()
							.getAbsolutePath();
					defaultOpenPath = defaultSavePath;
				}
			}
			savePath.run();

			loadPath.run();

			activeWindowFilePath = defaultSavePath;
		}

		int result = 0;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// if (MainFrame.this.infomlFile.hasChanged)
			{
				MainFrame.this.infomlFile.removeOutlineInfocards();
				MainFrame.this.infoWindow.getOutline();

				InfocardBuilder.buildOutlineInfocards(
						MainFrame.this.infoWindow.nodes,
						MainFrame.this.infoWindow.cardIds,
						MainFrame.this.infoWindow.preOrdCardIds,
						MainFrame.this.infomlFile);
			}

			equalizer.run();
			result = infomlFile
					.writeInfomlToDiskWithValidating(activeWindowFilePath);
			if (result == 1) {
				MainFrame.this.infomlFile.hasChanged = false;
			}

			if ((fchooser != null) && (result == 1)) {
				name = fchooser.getSelectedFile().getName();
				int index = windows.indexOf(MainFrame.this);

				MainFrame.this.setTitle("Infocard Organizer : " + name);

				for (int i = 0; i < windows.size(); i++) {
					JMenuItem menuItem = new JMenuItem(name);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							MainFrame.this.setState(Frame.NORMAL);
							MainFrame.this.requestFocus();
						}
					});
					((MainFrame) windows.elementAt(i)).windowMenu.remove(index);
					((MainFrame) windows.elementAt(i)).windowMenu.insert(
							menuItem, index);
				}
			}
		}

		if ((result == 0) && (returnVal == JFileChooser.APPROVE_OPTION)) {
			// FILE NOT SAVED error dialog
			JOptionPane.showMessageDialog(MainFrame.this,
					"The system cannot find the specified path \n "
							+ "File not saved", "System error",
					JOptionPane.ERROR_MESSAGE);

			defaultSavePath = null;
		}

		MainFrame.nicknameKeeper.saveNicknames();

		Thread dummy = new Thread(new Runnable() {
			public void run() {
				try {
					setDropStatusText("Document saved");
					Thread.currentThread().sleep(2500);
					setDropStatusText("   ...");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		dummy.start();

		AncestorDialog.viewableInstances = 0;
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		doClose();
	}

	public static void main(String[] args) {
		MainFrame obj = new MainFrame("Untitled");

		obj.setVisible(true);
	}

	public void performUndo() {
		try {
			if (focusedDialog != null)
				focusedDialog.ownUndoManager.undo();
			else {
				setEnabled(false);
				return;
			}
		} catch (CannotUndoException ex) {
			System.out.println("Unable to undo : " + ex);
			ex.printStackTrace();
		}

		undoAction.updateUndoState();
		redoAction.updateRedoState();
	}

	public void performRedo() {
		try {
			if (focusedDialog != null)
				focusedDialog.ownUndoManager.redo();
			else {
				setEnabled(false);
				return;
			}
		} catch (CannotRedoException ex) {
			System.out.println("Unable to redo: " + ex);
			ex.printStackTrace();
		}

		redoAction.updateRedoState();
		undoAction.updateUndoState();
	}

	class UndoAction extends AbstractAction {
		public UndoAction() {
			super(" Undo ", new ImageIcon("resources/Undo24.gif"));
			setEnabled(false);

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z,
					ActionEvent.CTRL_MASK));
			/*
			 * else putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			 * KeyEvent.VK_Z, ActionEvent.ALT_MASK));
			 */
		}

		public void actionPerformed(ActionEvent e) {
			if (lastAction == 1) {
				stateSaver.undoState();

				updateUndoState();
			} else
				performUndo();
		}

		protected void updateUndoState() {
			if (focusedDialog == null) {
				setEnabled(false);
				redoAction.setEnabled(false);

				return;
			}

			if (focusedDialog.ownUndoManager.canUndo()) // undo.canUndo())
			{
				setEnabled(true);
				putValue(Action.NAME, focusedDialog.ownUndoManager
						.getUndoPresentationName()); // undo
				putValue(SHORT_DESCRIPTION, focusedDialog.ownUndoManager
						.getUndoPresentationName());// undo
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Undo");
				putValue(SHORT_DESCRIPTION, "Undo");
			}
		}
	}

	class RedoAction extends AbstractAction {

		public RedoAction() {
			super(" Redo ", new ImageIcon("resources/Redo24.gif"));
			menuHeight = new ImageIcon("resources/Redo24.gif").getIconHeight();
			setEnabled(false);

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_Y, ActionEvent.ALT_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			performRedo();
		}

		protected void updateRedoState() {
			if (focusedDialog == null) {
				setEnabled(false);
				undoAction.setEnabled(false);
				return;
			}
			if (focusedDialog.ownUndoManager.canRedo()) {
				setEnabled(true);
				putValue(Action.NAME, focusedDialog.ownUndoManager
						.getRedoPresentationName());
				putValue(SHORT_DESCRIPTION, focusedDialog.ownUndoManager
						.getRedoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Redo");
				putValue(SHORT_DESCRIPTION, "Redo");
			}
		}
	}

	/*
	 * action for Save (file) menu item
	 */
	class SaveAction extends AbstractAction {
		public SaveAction() {
			super(" Save  ", new ImageIcon("resources/Save24.gif"));

			setEnabled(false);

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_S, ActionEvent.ALT_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			doSave();
		}
	}

	/*
	 * action for New (file) menu item
	 */
	class NewAction extends AbstractAction {
		public NewAction() {
			super(" New  ", new ImageIcon("resources/New24.gif"));

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_N, ActionEvent.ALT_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			final MainFrame frame = new MainFrame();

			try {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						frame.name = new String("Untitled");
						frame.setVisible(true);

						// add menu item to all main frames except frame
						for (int i = 0; i < windows.size() - 1; i++) {
							JMenuItem menuItem = new JMenuItem("Untitled");
							menuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									frame.setState(Frame.MAXIMIZED_BOTH);
									frame.requestFocus();
								}
							});
							((MainFrame) windows.elementAt(i)).windowMenu
									.add(menuItem);
						}

						// for frame add all menu items to windowMenu
						for (int i = 0; i < windows.size(); i++) {
							final String aux = ((MainFrame) windows
									.elementAt(i)).name;
							JMenuItem menuItem = new JMenuItem(aux);

							menuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									for (int i = 0; i < windows.size(); i++)
										if ((((MainFrame) windows.elementAt(i)).name)
												.compareTo(aux) == 0) {
											((MainFrame) windows.elementAt(i))
													.setState(Frame.MAXIMIZED_BOTH);
											((MainFrame) windows.elementAt(i))
													.requestFocus();
										}
								}
							});
							frame.windowMenu.add(menuItem);
						}
					}
				});
			} catch (Exception excep) {
				excep.printStackTrace();
			}
		}
	}

	/*
	 * action for Open (file) menu item
	 */
	class OpenAction extends AbstractAction {
		public OpenAction() {
			super(" Open ", new ImageIcon("resources/open.gif"));

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_O, ActionEvent.ALT_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			final JFileChooser fchooser = new JFileChooser(defaultOpenPath);
			if (defaultOpenPath != null) {
				fchooser.setCurrentDirectory(new File(defaultOpenPath));
			}
			// fchooser.setFileFilter(new AFileFilter("xml"));
			int returnVal = fchooser.showOpenDialog(MainFrame.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fchooser.getSelectedFile();
				final String filePath = file.getAbsolutePath();
				namea = fchooser.getSelectedFile().getName();

				if ((MainFrame.this.activeWindowFilePath == null)
						|| (filePath
								.compareTo(MainFrame.this.activeWindowFilePath) != 0)) {
					// Create and run(start) new thread not to block
					// event-dispatch thread
					Thread read = new Thread(new Runnable() {
						MainFrame frame;
						boolean fix = false;

						public void run() {
							// final MainFrame frame = new MainFrame();

							if (MainFrame.this.infoWindow.top.getChildCount() == 0)
								frame = MainFrame.this;
							else {
								frame = new MainFrame();
								fix = true;
							}

							frame.setTitle("Infocard Organizer : " + namea);
							frame.activeWindowFilePath = filePath;
							final InfomlReader reader = new InfomlReader(frame,
									frame.infomlFile, filePath);
							if (reader.readInfomlFile() == 0) {
								JOptionPane.showMessageDialog(MainFrame.this,
										"This is not a valid InfoML file",
										"Invalid Format",
										JOptionPane.ERROR_MESSAGE);

								if (fix) {
									frame.dispose();
									windows.remove(frame);
								}
							} else {
								try {
									defaultOpenPath = filePath;

									savePath.run();
									SwingUtilities
											.invokeAndWait(new Runnable() {
												public void run() {
													frame.name = new String(
															namea);
													frame.enableSave();
													frame.setVisible(true);

													// add menu item to all main
													// frames except frame
													for (int i = 0; i < windows
															.size() - 1; i++) {
														JMenuItem menuItem = new JMenuItem(
																namea);
														menuItem
																.addActionListener(new ActionListener() {
																	public void actionPerformed(
																			ActionEvent e) {
																		frame
																				.setState(Frame.NORMAL);
																		frame
																				.requestFocus();
																	}
																});
														((MainFrame) windows
																.elementAt(i)).windowMenu
																.add(menuItem);
													}

													// for frame add all menu
													// items to windowMenu
													for (int i = 0; i < windows
															.size(); i++) {
														final String aux = ((MainFrame) windows
																.elementAt(i)).name;
														JMenuItem menuItem = new JMenuItem(
																aux);

														menuItem
																.addActionListener(new ActionListener() {
																	public void actionPerformed(
																			ActionEvent e) {
																		for (int i = 0; i < windows
																				.size(); i++)
																			if ((((MainFrame) windows
																					.elementAt(i)).name)
																					.compareTo(aux) == 0) {
																				((MainFrame) windows
																						.elementAt(i))
																						.setState(Frame.NORMAL);
																				((MainFrame) windows
																						.elementAt(i))
																						.requestFocus();
																			}
																	}
																});
														frame.windowMenu.add(menuItem);
													}

													reader.displayInfoml(frame.infoWindow);
												}
											});
								}

								catch (Exception excep) {
									excep.printStackTrace();
								}
								MainFrame.equalizer.fileOpened(frame);
							}
						}
					});
					read.start();
				}
			}
		}
	}

	/*
	 * action for Close (file) menu item
	 */
	class CloseAction extends AbstractAction {
		public CloseAction() {
			super(" Close ", new ImageIcon("resources/close24.gif"));
		}

		public void actionPerformed(ActionEvent e) {
			doClose();
		}
	}

	/*
	 * action for Quit (the application) menu item
	 */
	class QuitAction extends AbstractAction {
		public QuitAction() {
			super(" Quit ", new ImageIcon("resources/closeAllWindows24.gif"));

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			doClose();

			if (AncestorDialog.viewableInstances == 0)
				System.exit(0);
		}
	}

	/*
	 * action for (create) New Notecard menu item
	 */
	class NewNotecardAction extends AbstractAction {
		public NewNotecardAction() {
			super(" New Notecard   ", new ImageIcon(
					"resources/new-notecard.gif"));

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_1,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_1, ActionEvent.ALT_MASK));

			putValue(SHORT_DESCRIPTION, " Create new notecard");

		}

		public void actionPerformed(ActionEvent e) {
			NotecardModel model = new NotecardModel();

			infocardBuilder = new InfocardBuilder(infomlFile, model);
			NotecardDialog dialog = new NotecardDialog(MainFrame.this,
					"New Notecard", model, infocardBuilder, MainFrame.this,
					false);
		}
	}

	/*
	 * action for (create) New Content Card menu item
	 */
	class NewContentCardAction extends AbstractAction {
		public NewContentCardAction() {
			super(" New Content Card   ", new ImageIcon(
					"resources/new-contentcard.gif"));

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_2,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_2, ActionEvent.ALT_MASK));

			putValue(SHORT_DESCRIPTION, " Create new content card");
		}

		public void actionPerformed(ActionEvent e) {
			SharedContentModel model = new SharedContentModel();
			SharedContentDialog scd = new SharedContentDialog(MainFrame.this,
					" New Content Card : Shared Content ", model,
					infocardBuilder, MainFrame.this, false, null);
		}
	}

	/*
	 * action for (create) New (infocard) from Selected (infocard) menu item
	 */
	class NewFromSelectedAction extends AbstractAction {
		public NewFromSelectedAction() {
			super("  New from Selected   ", new ImageIcon(
					"resources/new-from-selected.gif"));

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_3,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_3, ActionEvent.ALT_MASK));

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			UniqueContentModel uniqueModel = new UniqueContentModel();
			uniqueModel.setShared(editableContentModel.shared);

			InfocardBuilder infocardBuilder = new InfocardBuilder(
					MainFrame.this.infomlFile, uniqueModel);
			UniqueContentDialog unique = new UniqueContentDialog(
					MainFrame.this, "New Content Card : Unique content",
					uniqueModel, infocardBuilder, MainFrame.this, false);
		}
	}

	/*
	 * action for Duplicate (selected infocard) menu item
	 */
	public class DuplicateAction extends AbstractAction {
		UniqueContentModel newCModel;
		NotecardModel newNModel;

		public DuplicateAction() {
			super("  Duplicate ", new ImageIcon("resources/copy.gif"));

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_5,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_5, ActionEvent.ALT_MASK));

			setEnabled(false);
		}

		public void cloneContentcardModel(UniqueContentModel cloned) {
			newCModel.setContentList(cloned.getContentString());
			newCModel.setContentString(cloned.getContentString());

			newCModel.setTitle(cloned.getTitle());
			newCModel.setTime(cloned.getTime());

			newCModel.setSelectorsString(cloned.getSelectorsString());
			newCModel.setSelectorsList(cloned.getSelectorsString());

			newCModel.setNotes1String(cloned.getNotes1String());
			newCModel.setNotes1List(cloned.getNotes1String());

			newCModel.setNotes2String(cloned.getNotes2String());
			newCModel.setNotes2List(cloned.getNotes2String());

			newCModel.setDate(cloned.getDate());
			newCModel.setExact(cloned.isExact());

			newCModel.setPage1(cloned.getPage1());
			newCModel.setPage2(cloned.getPage2());

			newCModel.setShared(new SharedContentModel());

			newCModel.shared.setContainerNamePart1(cloned.shared
					.getContainerNamePart1());
			newCModel.shared.setContainerNamePart2(cloned.shared
					.getContainerNamePart2());
			newCModel.shared.setPreSepar(cloned.shared.getPreSepar());

			newCModel.shared.setDate1(cloned.shared.getDate1());
			newCModel.shared.setDate2(cloned.shared.getDate2());
			newCModel.shared.setDate3(cloned.shared.getDate3());
			newCModel.shared.setDateRole(cloned.shared.getDateRole());

			newCModel.shared.setAgent1(cloned.shared.getAgent1());
			newCModel.shared.setAgent2(cloned.shared.getAgent2());
			newCModel.shared.setAgent3(cloned.shared.getAgent3());
			newCModel.shared.setAgent4(cloned.shared.getAgent4());
			newCModel.shared.setAgent5(cloned.shared.getAgent5());
			newCModel.shared.setAgent6(cloned.shared.getAgent6());

			newCModel.shared.setEnclCntNamePart1(cloned.shared
					.getEnclCntNamePart1());
			newCModel.shared.setEnclCntNamePart2(cloned.shared
					.getEnclCntNamePart2());
			newCModel.shared.setEnclPreSepar(cloned.shared.getEnclPreSepar());
			newCModel.shared.setEnclRole(cloned.shared.getEnclRole());
			newCModel.shared.setEnclCorporateName(cloned.shared
					.getEnclCorporateName());

			newCModel.shared.setNickname(cloned.shared.getNickname());

			newCModel.shared.setSublocationPart1(cloned.shared
					.getSublocationPart1());
			newCModel.shared.setSublocationPart2(cloned.shared
					.getSublocationPart2());
		}

		public void cloneNotecardModel(NotecardModel cloned) {
			newNModel.setContentList(cloned.getContentString());
			newNModel.setContentString(cloned.getContentString());

			newNModel.setTitle(cloned.getTitle());
			newNModel.setTime(cloned.getTime());

			newNModel.setSelectorsString(cloned.getSelectorsString());
			newNModel.setSelectorsList(cloned.getSelectorsString());

			newNModel.setNotes1String(cloned.getNotes1String());
			newNModel.setNotes1List(cloned.getNotes1String());

			newNModel.setNotes2String(cloned.getNotes2String());
			newNModel.setNotes2List(cloned.getNotes2String());
		}

		public void actionPerformed(ActionEvent e) {
			if (editableContentModel != null) {
				newCModel = new UniqueContentModel();
				cloneContentcardModel(editableContentModel);

				InfocardBuilder infocardBuilder = new InfocardBuilder(
						MainFrame.this.infomlFile, newCModel);
				// infocardBuilder.modifyInfocard(
				// MainFrame.this.infomlFile.findInfocard(newCModel));

				infocardBuilder.setStandardInfocard();

				infocardBuilder.generateCardId();
				MainFrame.setupModel.increaseSequenceNumber("mainframe");
				// serialize the new sequence number
				MainFrame.serializator.run();
				infocardBuilder.setCardId();

				infocardBuilder.setContent();
				infocardBuilder.setTitle();
				infocardBuilder.setSelectors();
				infocardBuilder.setNotes1();
				infocardBuilder.setNotes2();
				infocardBuilder.setCreator(MainFrame.this.setupModel);
				infocardBuilder.setDateCreated();
				infocardBuilder.setExactQuotation();
				infocardBuilder.setPages();
				infocardBuilder.setContainerNamePart1();
				infocardBuilder.setContainerNamePart2();
				infocardBuilder.setContainerDate();
				infocardBuilder.setAgentX(newCModel.shared.getAgent1());
				infocardBuilder.agentType = null;
				infocardBuilder.setAgentX(newCModel.shared.getAgent2());
				infocardBuilder.agentType = null;
				infocardBuilder.setAgentX(newCModel.shared.getAgent3());
				infocardBuilder.agentType = null;
				infocardBuilder.setAgentX(newCModel.shared.getAgent4());
				infocardBuilder.agentType = null;
				infocardBuilder
						.setEnclosingAgentX(newCModel.shared.getAgent5());
				infocardBuilder.agentType = null;
				infocardBuilder
						.setEnclosingAgentX(newCModel.shared.getAgent6());
				infocardBuilder.agentType = null;
				infocardBuilder.setEnclosingContainerVolumeIssue();
				infocardBuilder.setEnclosingContainerTitle();
				infocardBuilder.setCorporateAgent();

				MainFrame.this.infomlFile.hasChanged = true;

				MainFrame.this.infoWindow.addNode(newCModel, 2);
			}

			if (editableNotecardModel != null) {
				newNModel = new NotecardModel();
				cloneNotecardModel(editableNotecardModel);

				InfocardBuilder infocardBuilder = new InfocardBuilder(
						infomlFile, newNModel);

				infocardBuilder.setStandardInfocard();

				infocardBuilder.generateCardId();
				MainFrame.setupModel.increaseSequenceNumber("mainframe");
				// serialize the new number sequence
				MainFrame.serializator.run();
				infocardBuilder.setCardId();

				infocardBuilder.setContent();
				infocardBuilder.setTitle();
				infocardBuilder.setSelectors();
				infocardBuilder.setNotes1();
				infocardBuilder.setNotes2();
				infocardBuilder.setCreator(MainFrame.this.setupModel);
				infocardBuilder.setDateCreated();

				MainFrame.this.infomlFile.hasChanged = true;

				MainFrame.this.infoWindow.addNode(newNModel, 1);
			}
		}
	}

	/*
	 * action for Quick Content Card (using same "shared" data from currently
	 * selected content card) menu item
	 */
	class QuickContentCardAction extends AbstractAction {
		public QuickContentCardAction() {
			super(" Quick Content Card   ", new ImageIcon(
					"resources/quick-content-card.gif"));

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_4,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_4, ActionEvent.ALT_MASK));

			putValue(SHORT_DESCRIPTION, " Create Quick Content Card");
			if (nicknames.size() == 0)
				setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			UniqueContentModel uniqueModel = new UniqueContentModel();
			infocardBuilder = new InfocardBuilder(infomlFile, uniqueModel);

			UniqueContentDialog unique = new UniqueContentDialog(
					MainFrame.this, "New Content Card : Unique content",
					uniqueModel, infocardBuilder, MainFrame.this, false);
		}
	}

	/*
	 * action for Cut menu item
	 */
	class CutAction extends AbstractAction {
		public CutAction() {
			super(" Cut ", new ImageIcon("resources/Cut24.gif"));

			setEnabled(false);

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_X, ActionEvent.ALT_MASK));

		}

		public void actionPerformed(ActionEvent e) {
			focusedTextComponent.cut();

			MainFrame.this.pasteAction.setEnabled(true);
		}
	}

	class CopyAction extends AbstractAction {
		public CopyAction() {
			super(" Copy ", new ImageIcon("resources/Copy24.gif"));

			setEnabled(false);

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_C, ActionEvent.ALT_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			focusedTextComponent.copy();

			MainFrame.this.pasteAction.setEnabled(true);
		}
	}

	class PasteAction extends AbstractAction {
		public PasteAction() {
			super(" Paste ", new ImageIcon("resources/Paste24.gif"));

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V,
					ActionEvent.CTRL_MASK));
			// else
			// /putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_V, ActionEvent.ALT_MASK));
		}

		public void actionPerformed(ActionEvent e) {

			if (focusedTextComponent != null)
				focusedTextComponent.paste();
		}
	}

	class SetupInformationAction extends AbstractAction {
		public SetupInformationAction() {
			super("Setup Information...", new ImageIcon(
					"resources/properties24.gif"));
		}

		public void actionPerformed(ActionEvent e) {
			SetupInformationDialog2 dialog = new SetupInformationDialog2(
					MainFrame.this, "Setup Information", MainFrame.this);
		}
	}

	public class ExportAction extends AbstractAction {
		String exportPath = null;
		String groovyFile = "";
		Object[] options = { "Save, then export", "Cancel" };

		public void transformToEnergy(String outputPath, String stylesheetPath) {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);

			try {
				File stylesheet = new File(stylesheetPath);
				File infocardFile = new File(activeWindowFilePath);

				DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(infocardFile);

				// Use a Transformer for output
				TransformerFactory tFactory = TransformerFactory.newInstance();
				StreamSource stylesource = new StreamSource(stylesheet);
				Transformer transformer = tFactory.newTransformer(stylesource);

				DOMSource source = new DOMSource(document);

				// converts output path name to form needed by transform method
				normalizedName = new File(outputPath).toURI().toString();

				StreamResult result = new StreamResult(normalizedName);
				transformer.transform(source, result);

			} catch (TransformerConfigurationException tce) {
				// Error generated by the parser
				System.out.println("\n** Transformer Factory error");
				System.out.println("   " + tce.getMessage());

				// Use the contained exception, if any
				Throwable x = tce;
				if (tce.getException() != null)
					x = tce.getException();
				x.printStackTrace();

			} catch (TransformerException te) {
				// Error generated by the parser
				System.out.println("\n** Transformation error");
				System.out.println("   " + te.getMessage());

				// Use the contained exception, if any
				Throwable x = te;
				if (te.getException() != null)
					x = te.getException();
				x.printStackTrace();

			} catch (SAXException sxe) {
				// Error generated by this application
				// (or a parser-initialization error)
				Exception x = sxe;
				if (sxe.getException() != null)
					x = sxe.getException();
				x.printStackTrace();

			} catch (ParserConfigurationException pce) {
				// Parser with specified options can't be built
				pce.printStackTrace();

			} catch (IOException ioe) {
				// I/O error
				ioe.printStackTrace();
			}
		}

		public ExportAction() {
			super("Export", new ImageIcon("resources/export.gif"));

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_E, ActionEvent.ALT_MASK));

			setEnabled(false);

			putValue(SHORT_DESCRIPTION, "Export");
		}

		public void actionPerformed(ActionEvent e) {

			// If infocard window has unsaved changes, save them
			if (MainFrame.this.infomlFile.hasChanged) {
				System.out.println("GW: file needs to be saved");
				doSave();
			}

			// set up for open-file dialog to choose an export folder
			File dialogStartPath = new File(System.getProperty("user.dir")
					+ File.separator + "scripts" + File.separator + "exports");
			System.out.println(dialogStartPath.toString());
			
			JFileChooser myChooser = new JFileChooser(dialogStartPath);
			final AFileFilter defaultFilter = new AFileFilter("groovy");
			myChooser.setFileFilter(defaultFilter);
			myChooser.setDialogTitle("Choose Export Action");

			
			int selected = myChooser.showOpenDialog(null);
			File toBeExecuted = null;
			if ( selected == JFileChooser.APPROVE_OPTION ) {
				// this is the Groovy file to be executed
				toBeExecuted = myChooser.getSelectedFile();	
				System.out.println("(MainFrame) File chosen is " + toBeExecuted.toString());
			} else {
				// if error or if user chooses Cancel button
				return;
			}

			// transfer execution to Groovy class given by toBeExecuted
			ClassLoader parent = getClass().getClassLoader();
			GroovyClassLoader loader = new GroovyClassLoader(parent);
			Class groovyClass = null;
			try {
				groovyClass = loader.parseClass(toBeExecuted);
			} catch (CompilationFailedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// instantiate the class as a GroovyObject...
			GroovyObject groovyObject = null;
			try {
				groovyObject = (GroovyObject) groovyClass.newInstance();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// ... convert the reference to the infocard file to an object...
			Object object1 = activeWindowFilePath;
			// ... then call the "main" method of the class, passing it the object
			groovyObject.invokeMethod("main", object1);

		}  // END actionPerformed
	}

	class EditSelectedCardAction extends AbstractAction {
		public EditSelectedCardAction() {
			super(" Edit Selected Card   ", new ImageIcon("resources"
					+ File.separator + "Edit24.gif"));

			setEnabled(false);

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_7,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_7, ActionEvent.ALT_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			if (editableContentModel != null)
				editUniqueContent(editableContentModel);
			else if (editableNotecardModel != null)
				editNotecard(editableNotecardModel);
		}
	}

	class EditSharedContentAction extends AbstractAction {
		public EditSharedContentAction() {
			super(" Edit Shared Content  ", new ImageIcon("resources"
					+ File.separator + "EditBook.gif"));

			setEnabled(false);

			// if (osName.startsWith("Win"))
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_6,
					ActionEvent.CTRL_MASK));
			// else
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			// KeyEvent.VK_6, ActionEvent.ALT_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			if (editableContentModel != null)
				editSharedContent(editableContentModel);
		}
	}

	class PreviousState {
		DefaultMutableTreeNode added, deleted, deletedParent;
		int delInd;

		NotecardModel model;
		String option;

		Vector models, infomlTypes;

		int undoLevel;

		public PreviousState() {
			// models = new Vector();
		}

		public void saveState(DefaultMutableTreeNode deleted,
				NotecardModel model, String option,
				DefaultMutableTreeNode added, DefaultMutableTreeNode delParent,
				int delInd) {
			this.added = added;
			this.deleted = deleted;
			this.deletedParent = delParent;
			this.delInd = delInd;
			this.option = option;
			this.model = model;

			undoLevel = 1;

			undoInf.setEnabled(true);
			lastAction = 1;

			undoIsEnabled = undoAction.isEnabled();

			undoAction.setEnabled(true);
		}

		public void undoState() {
			if (option.compareTo("d&d") == 0) {
				if (deleted == null) {
					((DefaultTreeModel) MainFrame.this.infoWindow.tree
							.getModel()).removeNodeFromParent(added);

					notecards.remove(added);
					uniqueContent.remove(added);

					Enumeration enumer = added.preorderEnumeration();
					int ind = 0;

					while (enumer.hasMoreElements()) {
						DefaultMutableTreeNode current = (DefaultMutableTreeNode) enumer
								.nextElement();

						if (models.elementAt(ind) instanceof UniqueContentModel) {
							uniqueContent.remove(current);
							infocards.remove(models.elementAt(ind));
						} else {
							notecards.remove(current);
							infocards.remove(models.elementAt(ind));
							// System.out.println("model : " + model);
						}
						ind++;
					}
				} else {
					((DefaultTreeModel) MainFrame.this.infoWindow.tree
							.getModel()).removeNodeFromParent(added);

					DefaultMutableTreeNode currentN = added;
					Enumeration removables = added.preorderEnumeration();
					while (removables.hasMoreElements()) {
						notecards.remove(currentN);
						uniqueContent.remove(currentN);

						currentN = (DefaultMutableTreeNode) removables
								.nextElement();
					}

					((DefaultTreeModel) MainFrame.this.infoWindow.tree
							.getModel()).insertNodeInto(deleted, deletedParent,
							delInd);

					Enumeration enumer = deleted.preorderEnumeration();
					int ind = 0;

					while (enumer.hasMoreElements()) {
						DefaultMutableTreeNode current = (DefaultMutableTreeNode) enumer
								.nextElement();

						// if (model instanceof UniqueContentModel)
						// uniqueContent.put(current, model)

						if (models.elementAt(ind) instanceof UniqueContentModel) {
							uniqueContent.put(current, models.elementAt(ind));
						} else {
							notecards.put(current, models.elementAt(ind));
						}
						ind++;
					}
				}
			} else if (option.compareTo("r") == 0) {
				((DefaultTreeModel) MainFrame.this.infoWindow.tree.getModel())
						.insertNodeInto(deleted, deletedParent, delInd);

				infoWindow.tree.scrollPathToVisible(new TreePath(deleted
						.getPath()));
				Enumeration enumer = deleted.preorderEnumeration();
				int ind = 0;

				while (enumer.hasMoreElements()) {
					DefaultMutableTreeNode current = (DefaultMutableTreeNode) enumer
							.nextElement();

					if (models.elementAt(ind) instanceof UniqueContentModel) {
						uniqueContent.put(current, models.elementAt(ind));
					} else {
						notecards.put(current, models.elementAt(ind));
					}
					infocards.put(models.elementAt(ind), infomlTypes
							.elementAt(ind));

					/*
					 * String pId; if (ind > 0) { NotecardModel pM =
					 * (NotecardModel) notecards.get(current.getParent()); if
					 * (pM == null) pM = (NotecardModel)
					 * uniqueContent.get(current.getParent()); pId =
					 * pM.getCardId(); } else { NotecardModel pM =
					 * (NotecardModel) notecards.get(current.getParent()); if
					 * (pM == null) pM = (NotecardModel)
					 * uniqueContent.get(current.getParent()); pId =
					 * pM.getCardId(); }
					 * 
					 * System.out.println("Burn : " + current.getParent());
					 * equalizer.keepStructure(current,
					 * current.getParent().getIndex(current), ((NotecardModel)
					 * models.elementAt(ind)).getCardId(), pId, "add", -1, null,
					 * (NotecardModel) models.elementAt(ind), (InfomlType)
					 * infomlTypes.elementAt(ind), 1);
					 */
					ind++;
				}

				infomlFile.rearangeElements();

				moveLeft.setEnabled(false);
				moveRight.setEnabled(false);
				remove.setEnabled(false);
				clear.setEnabled(true);
			} else if ((option.compareTo("mr") == 0)
					|| (option.compareTo("ml") == 0)) {
				((DefaultTreeModel) MainFrame.this.infoWindow.tree.getModel())
						.removeNodeFromParent(deleted);

				((DefaultTreeModel) MainFrame.this.infoWindow.tree.getModel())
						.insertNodeInto(deleted, deletedParent, delInd);

				MainFrame.this.infoWindow.tree.setSelectionPath(new TreePath(
						deleted.getPath()));
				MainFrame.this.infoWindow.tree
						.scrollPathToVisible(new TreePath(deleted.getPath()));

				checkMoveRightEnabled(deleted);

				checkMoveLeftEnabled(deleted);
			}

			undoLevel = 0;
			undoInf.setEnabled(false);
			undoAction.updateUndoState();

			lastAction = 0;
		}
	}
}
