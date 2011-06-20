package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.tree.*;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.dnd.peer.*;

import java.util.*;

import org.infoml.jaxb.*;
import org.infoml.jaxb.impl.*;


import java.io.*;

/**
 * Sets up the content of an infocard file's window in terms of a tree of nodes
 * that responds to drag-and-drop behaviors.
 *
 */
public class InfocardWindow implements Serializable
{
	public DragAndDropTree tree;
	private DefaultTreeModel treeModel;
	public JScrollPane treeView;
	DefaultMutableTreeNode top;

	MainFrame mainFr;

	DragSource dragSource;
	DropTarget dropTarget;

	Vector nodes;
	Vector cardIds;
	Vector preOrdCardIds;

	String extra;

	public InfocardWindow(MainFrame mainFr)
	{
		this.mainFr = mainFr;

		top = new DefaultMutableTreeNode("InfomlFile");

		dragSource = new DragSource(){
            protected DragSourceContext createDragSourceContext(
              DragSourceContextPeer dscp, DragGestureEvent dgl, Cursor dragCursor,
               Image dragImage, Point imageOffset, Transferable t,
                DragSourceListener dsl)
            {
                return new DragSourceContext(dscp, dgl, dragCursor, dragImage, imageOffset, t,  dsl)
                {
                   protected void updateCurrentCursor(int dropOp, int targetAct, int status) {}
                };
            }
        };

        tree = new DragAndDropTree(top, dragSource, mainFr);
        treeModel = new DefaultTreeModel(top);
		tree.setModel(treeModel);

        //tree.putClientProperty("JTree.linestyle", null);
	    tree.addTreeSelectionListener(mainFr);

		treeView = new JScrollPane(tree);
		treeView.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				System.out.println("mouse clicked");
			}

			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});
		treeView.getViewport().setOpaque(true);

		tree.setCellRenderer(new MyRenderer());

        dropTarget = new DropTarget();
        dropTarget.setComponent(tree);
        try
        {
          dropTarget.addDropTargetListener(tree);
        }
        catch (Exception e)
        { e.printStackTrace(); }

        dropTarget = new DropTarget();
        dropTarget.setComponent(treeView);
        try
        {
          dropTarget.addDropTargetListener(tree);
        }
        catch (Exception e)
        { e.printStackTrace(); }


        dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_MOVE, tree);
	}

	public void moveUp()
	{
	}

	public void moveDown()
	{
	}

	public void editNode(NotecardModel model)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		// must change to - the node to which the model points to in the map structure
						tree.getSelectionPath().getLastPathComponent();

			if (model.getTitle().compareTo("") != 0)
				node.setUserObject(model.getTitle());
			else
				node.setUserObject("No title");
	}

	public DefaultMutableTreeNode rememberSmartSelection(DefaultMutableTreeNode node)
	{
		if (node.getNextSibling() != null)
			return node.getNextSibling();
		else
		if (node.getPreviousSibling() != null)
			return node.getPreviousSibling();
		else return null;
	}

	/*public void callKeepStructure()
	{
		boolean keepStr = true;
		String pId = null;
		int index = -1;
		DefaultMutableTreeNode pNode = null;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					tree.getSelectionPath().getLastPathComponent();

		NotecardModel m = (NotecardModel) mainFr.notecards.get(node);
		if (m == null)
			m = (NotecardModel) mainFr.uniqueContent.get(node);
		String Id = m.getCardId();

		if (node.getParent().equals(mainFr.infoWindow.top))
			keepStr = false;
		else
		{
			pNode = (DefaultMutableTreeNode) node.getParent();
			NotecardModel pM = (NotecardModel) mainFr.notecards.get(pNode);
			if (pM == null)
				pM = (NotecardModel) mainFr.uniqueContent.get(pNode);

			pId = pM.getCardId();

			index = pNode.getIndex(node);
		}

		if (keepStr)
			mainFr.equalizer.keepStructure(null, index, Id, pId, "del",
					pNode.getChildCount(), null, null, null, -1);

	}*/

	public void insertNodeInto(NotecardModel model, DefaultMutableTreeNode node,
			DefaultMutableTreeNode parent, int index,  int option
			)
	{
		if (node == null)
		{
		DefaultMutableTreeNode infocardRep = null;

		if (((model.getTitle()).compareTo("")) != 0)
			infocardRep = new DefaultMutableTreeNode(model.getTitle());
		else
			infocardRep = new DefaultMutableTreeNode("No title");

		((DefaultTreeModel) tree.getModel()).insertNodeInto
										(infocardRep, parent, index);
		tree.scrollPathToVisible(new TreePath(infocardRep.getPath()));
		tree.setSelectionPath(new TreePath(infocardRep.getPath()));

		if (option == 1)
			mainFr.notecards.put(infocardRep, model);
		else
		if (option == 2)
			mainFr.uniqueContent.put(infocardRep, model);
		}
		else
		{
			((DefaultTreeModel) tree.getModel()).insertNodeInto
						(node, parent, index);
			tree.scrollPathToVisible(new TreePath(node.getPath()));
			tree.setSelectionPath(new TreePath(node.getPath()));

			if (option == 1)
				mainFr.notecards.put(node, model);
			else
			if (option == 2)
				mainFr.uniqueContent.put(node, model);
		}
	}

	/* The selected node must be removed from the gui but also from
	 * the data structures where it was kept and from the InfomlType
	 * list.
	 */
	public void removeNode()
	{
		DefaultMutableTreeNode selected = null;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						tree.getSelectionPath().getLastPathComponent();

		NotecardModel m = (NotecardModel) mainFr.notecards.get(node);
		if (m == null)
			m = (NotecardModel) mainFr.uniqueContent.get(node);

		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

		//NotecardModel pModel = (NotecardModel) mainFr.notecards.get(parent);
		//if (pModel == null)
        //    pModel = (NotecardModel) mainFr.uniqueContent.get(parent);


		//String pId = pModel.getCardId();

		mainFr.stateSaver.models = new Vector();
		mainFr.stateSaver.infomlTypes = new Vector();
		mainFr.stateSaver.saveState(node, m, "r", null, parent, parent.getIndex(node));
		selected = rememberSmartSelection(node);

		((DefaultTreeModel) tree.getModel()).removeNodeFromParent(node);
		Enumeration nodes = node.preorderEnumeration();

		while (nodes.hasMoreElements())
		{
			DefaultMutableTreeNode currentNode =
					(DefaultMutableTreeNode) nodes.nextElement();

			// the selected infocard is a notecard
			if (mainFr.notecards.containsKey(currentNode))
			{
				//InfomlType infocard  = (InfomlType) mainFr.infocards
				//			.get(mainFr.notecards.get(currentNode));

				//mainFr.infomlFile.if1.getInfoml().remove(infocard);

				mainFr.stateSaver.models.add(mainFr.notecards.get(currentNode));
				mainFr.stateSaver.infomlTypes.add(mainFr.infocards.get(
									mainFr.notecards.get(currentNode)));

				mainFr.infocards.remove(mainFr.notecards.get(currentNode));
				mainFr.notecards.remove(currentNode);

				//((DefaultTreeModel) tree.getModel()).removeNodeFromParent(currentNode);
			}

			if (mainFr.uniqueContent.containsKey(currentNode))
			{
				//InfomlType infocard  = (InfomlType) mainFr.infocards
				//			.get(mainFr.uniqueContent.get(currentNode));

				//mainFr.infomlFile.if1.getInfoml().remove(infocard);

				mainFr.stateSaver.models.add(mainFr.uniqueContent.get(currentNode));
				mainFr.stateSaver.infomlTypes.add(mainFr.infocards.get(
						mainFr.uniqueContent.get(currentNode)));

				mainFr.infocards.remove(mainFr.uniqueContent.get(currentNode));
				mainFr.uniqueContent.remove(currentNode);

				//((DefaultTreeModel) tree.getModel()).removeNodeFromParent(currentNode);
			}
		}

		//if (selected != null)
		//{	Object[] smartPath = { selected.getRoot(), selected };
		//	tree.setSelectionPath(new TreePath(smartPath));
		//}
		//else
		//	mainFr.remove.setEnabled(false);

		// remember that a change occured to the infoml file
		mainFr.infomlFile.hasChanged = true;

		if (top.getChildCount() == 0)
			mainFr.setClearEnabled(false);

		mainFr.moveLeft.setEnabled(false);
		mainFr.moveRight.setEnabled(false);
		mainFr.remove.setEnabled(false);

		mainFr.infomlFile.rearangeElements();

		mainFr.doSave();
		//
	}

	public void clearNodes()
	{
		int lastIndex = top.getIndex(top.getLastChild());

		for (int i = lastIndex; i >= 0; i--)
		{
			((DefaultTreeModel) tree.getModel()).removeNodeFromParent(
					(DefaultMutableTreeNode) top.getChildAt(i));
		}

		if (top.getChildCount() == 0)
			mainFr.setClearEnabled(false);
	}

	public void addNode(NotecardModel model, int option)
	{
		DefaultMutableTreeNode infocardRep = null;

		if (((model.getTitle()).compareTo("")) != 0)
			infocardRep = new DefaultMutableTreeNode(model.getTitle());
		else
			infocardRep = new DefaultMutableTreeNode("No title");

		//mainFr.stateSaver.saveState(null, model, "a", infocardRep, top,
			//												top.getChildCount());
		/* Add the model(bound to its representation - the DefaultMutableTreeNode)
		 * to one of the Map structures in MainFrame.
		 */
		if (option == 1)
			mainFr.notecards.put(infocardRep, model);
		else
		if (option == 2)
			mainFr.uniqueContent.put(infocardRep, model);

		treeModel.insertNodeInto(infocardRep, top, top.getChildCount());
		tree.scrollPathToVisible(new TreePath(infocardRep.getPath()));

		mainFr.enableSave();
	}

	public void getOutline()
	{
		cardIds = new Vector();
		preOrdCardIds = new Vector();
		nodes = new Vector();

		Enumeration enum1 = mainFr.infoWindow.top.breadthFirstEnumeration();
		Enumeration aux = mainFr.infoWindow.top.preorderEnumeration();

		// the top element NOT is skipped
		//enum1.nextElement();
		//aux.nextElement();

		while (enum1.hasMoreElements())
		{
			DefaultMutableTreeNode node =
				(DefaultMutableTreeNode) enum1.nextElement();
			nodes.add(node);

			NotecardModel ret1 = (NotecardModel) mainFr.notecards.
										get(node);
			NotecardModel ret2 = (NotecardModel) mainFr.uniqueContent.
										get(node);

			if (ret1 != null)
			{
				cardIds.add(ret1.getCardId());
			}
			else
			if (ret2 != null)
			{
				cardIds.add(ret2.getCardId());
			}

			ret1 = null;
			ret2 = null;
		}

		while (aux.hasMoreElements())
		{
			DefaultMutableTreeNode node =
				(DefaultMutableTreeNode) aux.nextElement();

			NotecardModel ret1 = (NotecardModel) mainFr.notecards.
										get(node);
			NotecardModel ret2 = (NotecardModel) mainFr.uniqueContent.
										get(node);

			if (ret1 != null)
			{
				preOrdCardIds.add(ret1.getCardId());
			}
			else
			if (ret2 != null)
			{
				preOrdCardIds.add(ret2.getCardId());
			}

			ret1 = null;
			ret2 = null;
		}
	}

	public void addNodeFromSaved(String title, int option,
										NotecardModel model, int level)
	{
		DefaultMutableTreeNode infocardRep =
							new DefaultMutableTreeNode(title);

		/* Add the model(bound to its representation - the DefaultMutableTreeNode)
		 * to one of the Map structures in MainFrame.
		 */
		if (option == 1)
			mainFr.notecards.put(infocardRep, model);
		else
		if (option == 2)
			mainFr.uniqueContent.put(infocardRep, model);

		Enumeration enum1 = top.preorderEnumeration();

		int lastNodeLevel = 0;
		DefaultMutableTreeNode lastNode = null;

		while (enum1.hasMoreElements())
		{
			lastNode = (DefaultMutableTreeNode) enum1.nextElement();
		}

		lastNodeLevel = lastNode.getLevel();

		if (lastNodeLevel > level)
		{
			int j = 0;
			int steps = lastNodeLevel - level;
			DefaultMutableTreeNode link = lastNode;
			while (j <= steps)
			{
				link = (DefaultMutableTreeNode) link.getParent();
				j++;
			}

			int index = link.getChildCount();
			treeModel.insertNodeInto(infocardRep, link, index);
		}
		else
		if (lastNodeLevel < level)
		{
			int index = lastNode.getChildCount();
			treeModel.insertNodeInto(infocardRep, lastNode, index);
		}
		else
		if ((lastNodeLevel == level) && (level != 0))
		{
			int index = lastNode.getParent().getChildCount();
			treeModel.insertNodeInto(infocardRep,
					(DefaultMutableTreeNode) lastNode.getParent(), index);
		}

		tree.scrollPathToVisible(new TreePath(infocardRep.getPath()));
	}

	public void moveNodeToRight()
	{
		boolean editing = false;

		Vector memExpanded = new Vector();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		                                  tree.getLastSelectedPathComponent();

		// test wether it leads to infinite recursivity or not
		DefaultMutableTreeNode parentToBe =
			(DefaultMutableTreeNode) node.getPreviousSibling();

		Enumeration children = node.preorderEnumeration();
		while (children.hasMoreElements())
		{
			DefaultMutableTreeNode current =
				(DefaultMutableTreeNode) children.nextElement();

			if (parentToBe.getUserObject().toString().equals
				(current.getUserObject().toString()))
			{
				mainFr.setDropStatusText("Destination node in drop node branch or " +
        		  		"Drop node in destination node branch" +
        		  		" - Not allowed");

				Thread dummy = new Thread(new Runnable(){
					public void run()
					{
						try
						{
							Thread.currentThread().sleep(7500);

							mainFr.setDropStatusText("   ...");
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				dummy.start();

				return;
			}
		}

		if (tree.isEditing())
			editing = true;

		boolean expanded = tree.isExpanded(new TreePath(node.getPath()));
		if (expanded)
		{
			Enumeration enu = node.preorderEnumeration();

			while (enu.hasMoreElements())
			{
				DefaultMutableTreeNode c =
					(DefaultMutableTreeNode) enu.nextElement();

				if (tree.isExpanded(new TreePath(c.getPath())))
					memExpanded.add(new Integer(1));
				else
					memExpanded.add(new Integer(0));
			}
		}

		DefaultMutableTreeNode sibling = node.getPreviousSibling();

		mainFr.stateSaver.saveState(node, null, "mr", node,
				(DefaultMutableTreeNode) node.getParent(),
				((DefaultMutableTreeNode) node.getParent()).getIndex(node));

			treeModel.removeNodeFromParent(node);
			treeModel.insertNodeInto(node, sibling, sibling.getChildCount());

			if (node.getChildCount() != 0)
				tree.scrollPathToVisible(new TreePath(
				((DefaultMutableTreeNode) node.getLastChild()).getPath()));
			else
				tree.scrollPathToVisible(new TreePath(node.getPath()));

			tree.setSelectionPath(new TreePath(node.getPath()));

			mainFr.infomlFile.hasChanged = true;

		if (expanded)
		{
			DefaultMutableTreeNode c = null;

			Enumeration enu = node.preorderEnumeration();
			int row = tree.getRowForPath(new TreePath(
					(c = (DefaultMutableTreeNode) enu.nextElement()).getPath()));

			int i = 0;
			int cRow = row;
			while (enu.hasMoreElements())
			{
				if (((Integer) memExpanded.elementAt(i)).intValue() == 1)
					tree.expandRow(cRow);
				else
					tree.collapseRow(cRow);
				cRow++;
				i++;

				c = (DefaultMutableTreeNode) enu.nextElement();
			}
		}
		else
			tree.collapsePath(new TreePath(node.getPath()));

		if (editing)
			tree.startEditingAtPath(new TreePath(node.getPath()));
	}

	public void moveNodeToLeft()
	{
		boolean editing = false;

		Vector memExpanded = new Vector();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        						tree.getLastSelectedPathComponent();

		if (tree.isEditing())
			editing = true;

		boolean expanded = tree.isExpanded(new TreePath(node.getPath()));
		if (expanded)
		{
			Enumeration enu = node.preorderEnumeration();

			while (enu.hasMoreElements())
			{
				DefaultMutableTreeNode c =
					(DefaultMutableTreeNode) enu.nextElement();

				if (tree.isExpanded(new TreePath(c.getPath())))
					memExpanded.add(new Integer(1));
				else
					memExpanded.add(new Integer(0));
			}
		}

		DefaultMutableTreeNode linkNode = (DefaultMutableTreeNode) node.getParent();

		mainFr.stateSaver.saveState(node, null, "ml", node,
								linkNode, linkNode.getIndex(node));

		if (linkNode != null)
		{
			treeModel.removeNodeFromParent(node);

			treeModel.insertNodeInto(node, (DefaultMutableTreeNode) linkNode.getParent(),
					linkNode.getParent().getIndex(linkNode) + 1);

			mainFr.infomlFile.hasChanged = true;
		}

		tree.setSelectionPath(new TreePath(node.getPath()));

		if (expanded)
		{
			DefaultMutableTreeNode c = null;

			Enumeration enu = node.preorderEnumeration();
			int row = tree.getRowForPath(new TreePath(
					(c = (DefaultMutableTreeNode) enu.nextElement()).getPath()));

			int i = 0;
			int cRow = row;
			while (enu.hasMoreElements())
			{
				if (((Integer) memExpanded.elementAt(i)).intValue() == 1)
					tree.expandRow(cRow);
				else
					tree.collapseRow(cRow);
				cRow++;

				c = (DefaultMutableTreeNode) enu.nextElement();

				i++;
			}
		}
		else
			tree.collapsePath(new TreePath(node.getPath()));

		if (editing)
			tree.startEditingAtPath(new TreePath(node.getPath()));
	}
}




