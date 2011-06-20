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
import java.util.Vector;

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;

public class AncestorDialog extends JDialog implements WindowListener,
										KeyListener, ContainerListener
{
	public UndoManager ownUndoManager;
	public OwnUndoableEditListener ownUndoListener;

	MainFrame mainFr;

	Vector focusableComponents;
	int index = 0;
	int size;

	static int viewableInstances = 0;

	public AncestorDialog(Frame owner, String title, MainFrame mainFr)
	{
		super(owner, title);

		viewableInstances++;

		focusableComponents = new Vector();

		this.mainFr = mainFr;
		addWindowListener(this);

		ownUndoListener = new OwnUndoableEditListener();
		ownUndoManager = new UndoManager();

		addKeyAndContainerListenerToAllChildren(this);
	}

	public void windowClosed(WindowEvent e)	{}

	public void windowOpened(WindowEvent e) {}

	public void windowIconified(WindowEvent e) {}

	public void windowDeiconified(WindowEvent e) {}

	public void windowActivated(WindowEvent e)
	{
		mainFr.focusedDialog = AncestorDialog.this;

		mainFr.undoAction.updateUndoState();
		mainFr.redoAction.updateRedoState();
	}

	public void windowDeactivated(WindowEvent e)
	{}

	public void windowClosing(WindowEvent e)
	{
		mainFr.focusedDialog = null;
		ownUndoManager.die();

		viewableInstances--;

		mainFr.undoAction.updateUndoState();
		mainFr.redoAction.updateRedoState();
	}

	protected class OwnUndoableEditListener implements UndoableEditListener
	{
		public void undoableEditHappened(UndoableEditEvent e)
		{
			ownUndoManager.addEdit(e.getEdit());

			mainFr.undoAction.updateUndoState();
			mainFr.redoAction.updateRedoState();
		}
	}

	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_Z)
		{
			if (mainFr.undoAction.isEnabled())
				mainFr.performUndo();
		}
		if (e.getKeyCode() == KeyEvent.VK_Y)
		{
			if (mainFr.redoAction.isEnabled())
				mainFr.performRedo();
		}
	}

	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			this.setVisible(false);
			// remember to take care about the undo redo behaviour
			mainFr.undoAction.setEnabled(false);
			mainFr.redoAction.setEnabled(false);

			viewableInstances--;
		}
	}

	public void keyTyped(KeyEvent e)
	{

	}

	public void addKeyAndContainerListenerToAllChildren(Component comp)
	{
		comp.addKeyListener(this);

		if (comp instanceof Container)
		{
			Container cont = (Container) comp;

			cont.addContainerListener(this);

			Component[] children = cont.getComponents();
			for (int i = 0; i < children.length; i++)
			{
				addKeyAndContainerListenerToAllChildren(children[i]);
			}
		}
	}

	public void componentAdded(ContainerEvent e)
	{
		addKeyAndContainerListenerToAllChildren(e.getChild());
	}

	public void removeKeyAndContainerListenerFromAllChildren(Component comp)
	{
		comp.removeKeyListener(this);

		if (comp instanceof Container)
		{
			Container cont = (Container) comp;

			cont.removeContainerListener(this);

			Component[] children = cont.getComponents();
			for (int i = 0; i < children.length; i++)
			{
				removeKeyAndContainerListenerFromAllChildren(children[i]);
			}
		}
	}

	public void componentRemoved(ContainerEvent e)
	{
		removeKeyAndContainerListenerFromAllChildren(e.getChild());
	}

	public boolean checkEmpty()
	{
		Component[] list = this.getContentPane().getComponents();

		for (int i = 0; i < list.length; i++)
		{
			if (list[i] instanceof JTextComponent)
			{
				JTextComponent current = (JTextComponent) list[i];

				if (current.getDocument().getLength() != 0)
					return false;
			}
		}

		return true;
	}
}
