package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/


import java.awt.*;

import javax.swing.tree.*;
import javax.swing.*;
import javax.swing.text.*;

public class LargeCellEditor extends DefaultTreeCellEditor
{
	JTree tree;
	MainFrame mainFr;
	Rectangle rect;
	int myOffset;

	boolean justEdited = false;

	public LargeCellEditor(JTree tree, DefaultTreeCellRenderer renderer,
							MainFrame mainFr)
	{
		super(tree, renderer);

		this.tree = tree;
		this.mainFr = mainFr;

		//DefaultMutableTreeNode selected = (DefaultMutableTreeNode)
		 //tree.getSelectionPath().getLastPathComponent();
	}

	public boolean stopCellEditing()
	{
		justEdited = true;

		return super.stopCellEditing();
	}

	 /**
     * Creates the container to manage placement of
     * <code>editingComponent</code>.
     */
    protected Container createContainer()
    {
    	return new EditorContainer();
    }




	public class EditorContainer extends Container
	{
	     /**
         * Constructs an <code>EditorContainer</code> object.
         */
	public EditorContainer()
	{
	    setLayout(null);
	}

	// This should not be used. It will be removed when new API is
	// allowed.
	public void EditorContainer()
	{
	    setLayout(null);
	}

        /**
         * Overrides <code>Container.paint</code> to paint the node's
         * icon and use the selection color for the background.
         */
	public void paint(Graphics g)
	{
	    Dimension        size = getSize();

	    // Then the icon.
	    if(editingIcon != null)
	    {
		int       yLoc = Math.max(0, (getSize().height -
					  editingIcon.getIconHeight()) / 2);

		editingIcon.paintIcon(this, g, 0, yLoc);
	    }

	    // Border selection color
	    Color       background = getBorderSelectionColor();
	    if(background != null)
	    {
		g.setColor(background);
		g.drawRect(0, 0, size.width - 1, size.height - 1);
	    }
	    super.paint(g);

	    ((JTextComponent) editingComponent).setSelectionEnd(0);
	    ((JTextComponent) editingComponent).setCaretPosition(
	    		((JTextComponent) editingComponent).getText().length());
	}
	/**
	 * Lays out this <code>Container</code>.  If editing,
         * the editor will be placed at
	 * <code>offset</code> in the x direction and 0 for y.
	 */
	public void doLayout()
	{
	    if(editingComponent != null)
	    {
		Dimension             cSize = getSize();

		editingComponent.getPreferredSize();
		editingComponent.setLocation(offset, 0);
		editingComponent.setBounds(offset, 0,
					   cSize.width - offset,
					   cSize.height);
	    }

	    ((JTextComponent) editingComponent).setSelectionEnd(0);
	    ((JTextComponent) editingComponent).setCaretPosition(
	    		((JTextComponent) editingComponent).getText().length());
	}

	/**
	 * Returns the preferred size for the <code>Container</code>.
         * This will be at least preferred size of the editor plus
         * <code>offset</code>.
         * @return a <code>Dimension</code> containing the preferred
         *   size for the <code>Container</code>; if
         *   <code>editingComponent</code> is <code>null</code> the
         *   <code>Dimension</code> returned is 0, 0
	 */
	public Dimension getPreferredSize() {

	    if(editingComponent != null)
	    {
		Dimension         pSize = editingComponent.getPreferredSize();

		pSize.width += offset + 5;

		Dimension         rSize = (renderer != null) ?
		                          renderer.getPreferredSize() : null;

		if(rSize != null)
		    pSize.height = Math.max(pSize.height, rSize.height);
		if(editingIcon != null)
		    pSize.height = Math.max(pSize.height,
					    editingIcon.getIconHeight());

		// Make sure width is at least 100.
		pSize.width = mainFr.getWidth() - myOffset - 20;
		return pSize;
	    }

	    ((JTextComponent) editingComponent).setSelectionEnd(0);
	    ((JTextComponent) editingComponent).setCaretPosition(
	    		((JTextComponent) editingComponent).getText().length());

	    return new Dimension(0, 0);
	}

	}
}
