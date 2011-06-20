package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import java.awt.dnd.*;
import javax.swing.*;
import java.awt.event.*;

import javax.swing.tree.*;
import javax.swing.event.*;

import java.awt.datatransfer.*;
import java.awt.*;
import java.awt.dnd.peer.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;

import org.infoml.jaxb.*;
import org.infoml.jaxb.impl.*;

public class DragAndDropTree extends JTree implements DragGestureListener,
		DragSourceListener,	DropTargetListener, Serializable, KeyListener,
		MouseListener
{
	DragSource dragSource;
	static DragSourceContext context;
	DefaultMutableTreeNode root;

	DefaultMutableTreeNode celery;

	final MainFrame mainFr;

	NotecardModel content;
	UniqueContentModel model2;
	SharedContentModel shared;
	LargeCellEditor thisEditor;

	//TransferableNode transferable;

	Vector way;
	TreePath editPath, currentPath;

	public DragAndDropTree(DefaultMutableTreeNode root, DragSource dragSource,
									MainFrame mainFr)
    {
        super(root);

        this.root = root;
        this.dragSource = dragSource;
        this.mainFr = mainFr;

        this.setEditable(true);
        this.setRootVisible(false);
        this.addKeyListener(this);
        this.setShowsRootHandles(true);

        thisEditor = new LargeCellEditor(
        		this, (DefaultTreeCellRenderer)this.getCellRenderer(), mainFr);

        this.setCellEditor(thisEditor);
        this.setInvokesStopCellEditing(true);
        this.addMouseListener(this);
    }

	// MouseListener
	public void mouseClicked(MouseEvent e) {}

	public void mousePressed(MouseEvent e)
	{
		if (editPath == null)
		{
			editPath = this.getPathForLocation(e.getPoint().x,
															e.getPoint().y);
		}

		currentPath = this.getPathForLocation(e.getPoint().x, e.getPoint().y);

		if (thisEditor.justEdited)
		{
			updateAfterEdit((DefaultMutableTreeNode) editPath.getLastPathComponent());

			DragAndDropTree.this.mainFr.doSave();

			if (currentPath != null)
				setSelectionPath(currentPath);

			thisEditor.justEdited = false;
		}
		editPath = currentPath;
	}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}


	void updateAfterEdit(DefaultMutableTreeNode selected)
	{
		NotecardModel m = null;

		if (mainFr.notecards.get(selected) != null)
		{
			m = (NotecardModel) mainFr.notecards.get(selected);
			m.setTitle((String) selected.getUserObject());

			mainFr.notecards.remove(selected);
			mainFr.notecards.put(selected, m);

			mainFr.equalizer.setMainFr(mainFr);
			mainFr.equalizer.setModifiedNotecardModel(m);
			mainFr.equalizer.setModifiedInfomlType(
					(InfomlType) mainFr.infocards.get(m));
			mainFr.equalizer.equalizeNotecards();

			mainFr.equalizer.run();
		}
		else
		if (mainFr.uniqueContent.get(selected) != null)
		{
			m = (UniqueContentModel) mainFr.uniqueContent.get(selected);
			m.setTitle((String) selected.getUserObject());

			mainFr.uniqueContent.remove(selected);
			mainFr.uniqueContent.put(selected, m);

			mainFr.equalizer.setMainFr(mainFr);
			mainFr.equalizer.setModifiedInfomlType(
					(InfomlType) mainFr.infocards.get(m));
			mainFr.equalizer.setModifiedContentcardModel((UniqueContentModel) m);
			mainFr.equalizer.equalizeContentcards();

			mainFr.equalizer.run();
		}

		InfomlType infocard = (InfomlType) mainFr.infocards.get(m);
		try
		{
			SimpleRichTextType srt = InfocardBuilder.
							objFactory.createSimpleRichTextType();

			srt.getContent().add(m.getTitle());

			infocard.getData().setTitle(srt);
		}
		catch (Exception excep)
		{ excep.printStackTrace(); }

		mainFr.infocards.remove(m);
		mainFr.infocards.put(m, infocard);

		int index = mainFr.infomlFile.if1.getInfoml().indexOf(infocard);

		mainFr.infomlFile.if1.getInfoml().remove(index);
		mainFr.infomlFile.if1.getInfoml().add(index, infocard);

		/*DefaultMutableTreeNode pNode =
			(DefaultMutableTreeNode) selected.getParent();
		NotecardModel pM = (NotecardModel) mainFr.notecards.get(pNode);
		if (pM == null)
			pM = (NotecardModel) mainFr.uniqueContent.get(pNode);*/
	}

	// KeyListener
	public void keyTyped(KeyEvent e)
	{
		//System.out.println("key typed");
	}

	public void keyPressed(KeyEvent e)
	{
		//System.out.println("key pressed");
	}

	public void keyReleased(KeyEvent e)
	{
	  if (getSelectionPath() != null)
	  {
		if (e.getKeyCode() == KeyEvent.VK_DELETE)
		{
		   if (mainFr.infoWindow.tree.getSelectionPath() != null)
		   {
			   DefaultMutableTreeNode node = (DefaultMutableTreeNode)
								getSelectionPath().getLastPathComponent();

			   /*mainFr.equalizer.addInd = node.getParent().getIndex(node);
			   NotecardModel momo = (NotecardModel) mainFr.notecards.get(node);
			   if (momo == null)
				   momo = (NotecardModel) mainFr.uniqueContent.get(node);*/

			   //if (node.getParent().equals(mainFr.infoWindow.top))
				   mainFr.infoWindow.removeNode();
			  // else
				  // mainFr.infoWindow.callKeepStructure();

			  // mainFr.equalizer.addId = momo.getCardId();
		   }
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			DefaultMutableTreeNode selected =
				(DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();

			if (((String) selected.getUserObject()).compareTo("") == 0)
			{
				((DefaultTreeModel) getModel()).removeNodeFromParent(selected);

				return;
			}

			updateAfterEdit(selected);

			mainFr.doSave();

			/*mainFr.equalizer.childCountBeforeAdd =
				selected.getParent().getChildCount();
			mainFr.equalizer.addInd = ((DefaultMutableTreeNode)
					selected.getParent()).getIndex(selected);

			if (mainFr.equalizer.addId != null);
			if (! selected.getParent().equals(root))
			{
				mainFr.equalizer.keepStructure(null, mainFr.equalizer.addInd,
				mainFr.equalizer.addId, pM.getCardId(),
				"add", -1, (DefaultMutableTreeNode) selected.getParent(), m,
				infocard, 1);
			}

			mainFr.equalizer.addId  = null;
			mainFr.equalizer.addInd = -1;*/
		}

		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			mainFr.doSave();
			// the perpetrator
			DefaultMutableTreeNode selected =
				(DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();

			int index = selected.getParent().getIndex(selected);

			// the resulted node
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("");

			((DefaultTreeModel) getModel()).insertNodeInto
				(newNode, (DefaultMutableTreeNode) selected.getParent(), index+1);
			scrollPathToVisible(new TreePath(newNode.getPath()));

			NotecardModel pModel = (NotecardModel)
					mainFr.notecards.get(
					(DefaultMutableTreeNode) selected.getParent());
			if (pModel == null)
				pModel = (NotecardModel) mainFr.uniqueContent.get(
					(DefaultMutableTreeNode) selected.getParent());

			NotecardModel model = new NotecardModel();
			model.setTitle("");

			InfocardBuilder infocardBuilder =
				new InfocardBuilder(mainFr.infomlFile, model);

			infocardBuilder.generateCardId();
			MainFrame.setupModel.increaseSequenceNumber("Space released");

			// serialize the new number sequence
			MainFrame.serializator.run();

			infocardBuilder.setStandardInfocard();

			infocardBuilder.setCardId();

			infocardBuilder.setCreator(mainFr.setupModel);
			infocardBuilder.setDateCreated();

			// performed by keepStructure now
			mainFr.infomlFile.if1.getInfoml().add(infocardBuilder.infocard);
			mainFr.infomlFile.hasChanged = true;

			// performed by keepStructure now
			mainFr.notecards.put(newNode, model);
			mainFr.infocards.put(model, infocardBuilder.infocard);

			startEditingAtPath(new TreePath(newNode.getPath()));
			scrollPathToVisible(new TreePath(newNode.getPreviousSibling().getPath()));

			editPath = this.getEditingPath();
			//mainFr.equalizer.addId  = infocardBuilder.cardId;
		}

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			Enumeration nodes = root.preorderEnumeration();

			while (nodes.hasMoreElements())
			{
			  DefaultMutableTreeNode currentNode =
					(DefaultMutableTreeNode) nodes.nextElement();

			  if (((String)currentNode.getUserObject()).equals(""))
			  {
				((DefaultTreeModel) getModel()).removeNodeFromParent(currentNode);

				NotecardModel model = (NotecardModel) mainFr.notecards.get(currentNode);
				InfomlType infocard = (InfomlType) mainFr.infocards.get(model);

				mainFr.notecards.remove(currentNode);
				mainFr.infocards.remove(model);
				mainFr.infomlFile.if1.getInfoml().remove(infocard);

				mainFr.infomlFile.hasChanged = true;
			  }
			}

			//mainFr.equalizer.addId  = null;
			//mainFr.equalizer.addInd = -1;
		}
	  }
	}

	public void dragGestureRecognized(DragGestureEvent e)
    {
      // get the selected node
	if (getPathForLocation(e.getDragOrigin().x, e.getDragOrigin().y) != null)
	  if (getSelectionPath() != null)
	  {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                              getSelectionPath().getLastPathComponent();

        celery = node;

        	TransferableNode transferable = new TransferableNode();

        	transferable.node = node;

        	int number = 0;
        	Enumeration enu = ((DefaultMutableTreeNode) node.getRoot()).
        								preorderEnumeration();
        	while (enu.hasMoreElements())
        	{
        		enu.nextElement();
        		number++;
        	}
        	transferable.identifier3 = number;
        	transferable.identifier4 = mainFr.getBounds();

        	// complete the models map
        	/*NotecardModel m = (NotecardModel) mainFr.notecards.get(node);
        	if (m != null)
        		transferable.models.put("notecard", m);
        	else
        	{
        		m = (NotecardModel) mainFr.uniqueContent.get(node);
        		transferable.models.put("contentcard", m);
        	}*/
        	Enumeration enumer = node.preorderEnumeration();
        	while (enumer.hasMoreElements())
        	{
        		DefaultMutableTreeNode elem =
        			(DefaultMutableTreeNode) enumer.nextElement();

        		NotecardModel m2 = (NotecardModel) mainFr.notecards.get(elem);
            	if (m2 != null)
            	{
            		transferable.models.put(elem, m2);
            		transferable.modelTypes.put(m2, "notecard");
            	}
            	else
            	{
            		m2 = (NotecardModel) mainFr.uniqueContent.get(elem);
            		transferable.models.put(elem, m2);
            		transferable.modelTypes.put(m2, "contentcard");
            	}

            	transferable.cardIds.addElement(m2.getCardId());
        	}

        	DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        	TreeNode[] nodeArray = model.getPathToRoot(node);

        	transferable.nodeIndex = new int[nodeArray.length];
        	transferable.nodeIndex[0] = 0;
        	for (int i = 1; i < nodeArray.length; i++)
        	{
        		DefaultMutableTreeNode currentNode =
        								(DefaultMutableTreeNode) nodeArray[i];

        		DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
        													currentNode.getParent();
        		transferable.nodeIndex[i] = model.getIndexOfChild(parent, currentNode);
        	}

        	transferable.identifier1 = root.getFirstChild().toString();
        	transferable.identifier2 = root.getLastChild().toString();

        	NotecardModel transferModel = null;
        		transferModel = (NotecardModel) mainFr.notecards.get(node);
        		transferable.transferModelType = "notecard";
        	if (transferModel == null)
        	{
        		transferModel = (NotecardModel) mainFr.uniqueContent.get(node);
        		transferable.transferModelType = "contentcard";
        	}
        	transferable.transferModel = transferModel;

        	//transferable.infocard = (InfomlType) mainFr.infocards.get(transferModel);

        	transferable.cardId = transferModel.cardId;

        	Cursor cursor = selectCursor(e.getDragAction());
        	dragSource.startDrag(e, cursor, transferable, this);

        	repaint();
	  }
    }

	private Cursor selectCursor(int action)
    {
        return (action == DnDConstants.ACTION_MOVE) ?
          DragSource.DefaultMoveDrop : DragSource.DefaultCopyDrop;
    }

	 // DragSourceListener
    public void dragDropEnd(DragSourceDropEvent e) {}

    public void dragEnter(DragSourceDragEvent e)
    {
    	DragSourceContext backUpContext = e.getDragSourceContext();

        if (backUpContext != null)
          context = e.getDragSourceContext();
    }

    public void dragExit(DragSourceEvent e)
    {
    	context = e.getDragSourceContext();

    	context.setCursor(dragSource.DefaultMoveNoDrop);
    }

    public void dropActionChanged(DragSourceDragEvent e) {}

    public void dragOver(DragSourceDragEvent e)
    {
        DragSourceContext backUpContext = e.getDragSourceContext();

        if (backUpContext != null)
          context = e.getDragSourceContext();
    }

//  DropTargetListener
    public void dragEnter(DropTargetDragEvent dtde) {}

    public void dragExit(DropTargetEvent e)
    {
    	mainFr.setDropStatusText("   ...");
    }

    public void dragOver(DropTargetDragEvent dtde)
    {
    	JComponent dropTargetComp = (JComponent)
    	                 ((DropTarget) dtde.getSource()).getComponent();

    	// retrieving the node
    	Transferable transferable = dtde.getTransferable();
    	TransferableNode transfer = null;
        try
        {
           transfer = (TransferableNode) transferable.getTransferData
           			(java.awt.datatransfer.DataFlavor.stringFlavor);
        }
        catch (Exception e)
        { e.printStackTrace(); }
    	DefaultMutableTreeNode draggedNode = null;
    	if (transferable != null)
    	{
    		draggedNode = (DefaultMutableTreeNode) transfer.node;
    	}


        // obtaining the location the node has been dragged over
        Point loc = dtde.getLocation();
        TreePath path = getPathForLocation(loc.x, loc.y);
        Rectangle rect = getPathBounds(path);

        if (path == null)
        {
            context.setCursor(dragSource.DefaultMoveDrop);
            mainFr.setDropStatusText("Drop as last node");
        }
        else
        {
          DefaultMutableTreeNode nodeAtLoc = (DefaultMutableTreeNode)
          										path.getLastPathComponent();

          	  if (destinationNodeInDropNodeFamily(nodeAtLoc, draggedNode,
          			  transfer.cardIds))
        	  {
        		  mainFr.setDropStatusText("Destination node in drop node branch or " +
        		  		"Drop node in destination node branch" +
        		  		" - Not allowed - ");
        		  context.setCursor(dragSource.DefaultMoveNoDrop);
        	  }
        	  else
        	  {
        		  if ((nodeAtLoc.equals(root.getFirstChild())) &&
      				  	(loc.y < rect.height / 2))
        			  mainFr.setDropStatusText("drop as first node in the window");
        		  else
        		  if (loc.x < rect.x + 23)
        			  //loc.x - rect.x < rect.x + rect.width - loc.x
        		  {
        			  mainFr.setDropStatusText("Drop as sibling of : " +
        				  			(String) nodeAtLoc.getUserObject());
        			  context.setCursor(dragSource.DefaultLinkDrop);
        		  }
        		  else
        		  //if (loc.x >= rect.x + 23)
        			  //loc.x - rect.x >= rect.x + rect.width - loc.x
        		  {
        			  mainFr.setDropStatusText("Drop as child of : " +
        				  			(String) nodeAtLoc.getUserObject());
        			  context.setCursor(dragSource.DefaultLinkDrop);
        		  }
        	  }
        }
    }

    public void dropActionChanged(DropTargetDragEvent e) {}

    public boolean destinationNodeInDropNodeFamily(DefaultMutableTreeNode
    		     destination, DefaultMutableTreeNode droped, Vector dropedIds)
    {
    	Vector destIds = new Vector();
    	NotecardModel m = null;

    	DefaultMutableTreeNode link = destination;

    	while (link != null)
    	{
    		m = (NotecardModel) mainFr.uniqueContent.get(link);
    		if (m != null)
    		{
    			destIds.addElement(m.getCardId());
    		}
    		else
    		{
    		    m = (NotecardModel) mainFr.notecards.get(link);
    		    if (m!= null)
    		    {
    		    	destIds.addElement(m.getCardId());
    		    }
    		}

    		link = (DefaultMutableTreeNode) link.getParent();
    	}

    	Enumeration e = destination.preorderEnumeration();

    	// skip destination - I already checked it
    	e.nextElement();
    	while (e.hasMoreElements())
    	{
    		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();

    		m = (NotecardModel) mainFr.uniqueContent.get(node);
    		if (m != null)
    			destIds.addElement(m.getCardId());
    		else
    		{
    		    m = (NotecardModel) mainFr.notecards.get(node);
    		    destIds.addElement(m.getCardId());
    		}
    	}

    	for (int i = 0; i < dropedIds.size(); i++)
    	  for (int j = 0; j < destIds.size(); j++)
    		  if (((String) dropedIds.elementAt(i)).compareTo(
    				(String) destIds.elementAt(j)) == 0)
    			  return true;

    	return false;
    }

    // removes the nodes from the data structures used
    public void modifySubTreeInStructures(DefaultMutableTreeNode node,
    		DefaultMutableTreeNode transferNode)
    {
    	/*int i = 0;

    	if (node.getChildCount() != 0)
    	while (i < node.getChildCount())
    	{
    		//i++;
    		//modifySubTreeInStructures((DefaultMutableTreeNode) node.getFirstChild(),
    			//	(DefaultMutableTreeNode) transferNode.getFirstChild());

    		modifySubTreeInStructures((DefaultMutableTreeNode) node.getChildAt(i),
        			(DefaultMutableTreeNode) transferNode.getChildAt(i));
    		i++;
    	}*/

    	Enumeration transfer = transferNode.preorderEnumeration();

    	Enumeration enumer = node.preorderEnumeration();
    	while (enumer.hasMoreElements())
    	{
    	node = (DefaultMutableTreeNode) enumer.nextElement();

    	Object notecardTrue = mainFr.notecards.remove(node);
        Object contentcardTrue = mainFr.uniqueContent.remove(node);

        if (notecardTrue != null)
  	  	{
  			mainFr.notecards.put(transfer.nextElement(), notecardTrue);

  			mainFr.stateSaver.models.add(notecardTrue);
   	  	}
  	  	else
  	  	if (contentcardTrue != null)
  	  	{
  			mainFr.uniqueContent.put(transfer.nextElement(), contentcardTrue);

  			mainFr.stateSaver.models.add(contentcardTrue);
  	  	}
    	}
        // The mainFr.infocards does not necesitate a correction because
        // the mapping model - infocard is still valid (remains the same).
    }

    public void addSubTreeToStructures(TransferableNode transfer)
    {
    	Enumeration enumer = transfer.node.preorderEnumeration();

    	while (enumer.hasMoreElements())
    	{
    		String type = null;

    		DefaultMutableTreeNode currentNode =
    			(DefaultMutableTreeNode) enumer.nextElement();

    		NotecardModel currentModel =
    			(NotecardModel) transfer.models.get(currentNode);
    		type = (String) transfer.modelTypes.get(currentModel);

    		if (type.compareTo("notecard") == 0)
    		{
    			mainFr.notecards.put(currentNode, currentModel);
    			type = "notecard";

    			mainFr.stateSaver.models.add(currentModel);
    		}
        	if (type.compareTo("contentcard") == 0)
        	{
        		currentModel =
        			(UniqueContentModel) transfer.models.get(currentNode);

        		UniqueContentModel uniqueModel = (UniqueContentModel) currentModel;

        		mainFr.uniqueContent.put(currentNode, currentModel);

        		type = "contentcard";

        		MainFrame.sharedContent.put(uniqueModel.shared.getNickname(),
        				uniqueModel.shared);
        		//MainFrame.nicknames.add(uniqueModel.shared.getNickname());
        		mainFr.quickContentCardAction.setEnabled(true);

        		mainFr.stateSaver.models.add(currentModel);
        	}

    		InfocardBuilder infocardBuilder = new InfocardBuilder(mainFr.infomlFile,
						currentModel);

    		infocardBuilder.setStandardInfocard();

    		infocardBuilder.infocard.setCardId(currentModel.cardId);

    		  infocardBuilder.setContent();
    		  infocardBuilder.setTitle();

    		  infocardBuilder.setSelectors();
    		  infocardBuilder.setNotes1();
    		  infocardBuilder.setNotes2();
    		  infocardBuilder.setCreator(mainFr.setupModel);
    		  infocardBuilder.setDateCreated();

    		  if (type.compareTo("notecard") == 0)
    		  {
    		  	mainFr.infocards.put(currentModel, infocardBuilder.infocard);
    		  }

    		  if (type.compareTo("contentcard") == 0)
    		  {
    			model2 = new UniqueContentModel();

    			shared = new SharedContentModel();
    			model2.setShared(shared);

    			model2.setTitle(currentModel.getTitle());

    			model2.setContentString(currentModel.getContentString());
    			model2.setContentList(model2.getContentString());

    			model2.setSelectorsString(currentModel.getSelectorsString());
    			model2.setSelectorsList(model2.getSelectorsString());

    			model2.setNotes1String(currentModel.getNotes1String());
    			model2.setNotes1List(model2.getNotes1String());

    			model2.setNotes2String(currentModel.getNotes2String());
    			model2.setNotes2List(model2.getNotes2String());

    			infocardBuilder.setExactQuotation();
    			infocardBuilder.setPages();
    			infocardBuilder.setContainerNamePart1();
    			infocardBuilder.setContainerNamePart2();
    			infocardBuilder.setContainerDate();

    			infocardBuilder.setAgentX(model2.shared.getAgent1());
    			infocardBuilder.agentType = null;
    			infocardBuilder.setAgentX(model2.shared.getAgent2());
    			infocardBuilder.agentType = null;
    			infocardBuilder.setAgentX(model2.shared.getAgent3());
    			infocardBuilder.agentType = null;
    			infocardBuilder.setAgentX(model2.shared.getAgent4());
    			infocardBuilder.agentType = null;
    			infocardBuilder.setEnclosingAgentX(model2.shared.getAgent5());
    			infocardBuilder.agentType = null;
    			infocardBuilder.setEnclosingAgentX(model2.shared.getAgent6());
    			infocardBuilder.agentType = null;
    			infocardBuilder.setEnclosingContainerVolumeIssue();
    			infocardBuilder.setEnclosingContainerTitle();
    			infocardBuilder.setCorporateAgent();

    			mainFr.infocards.put(model2, infocardBuilder.infocard);
    		 }
    	}
    }

    public int getNr()
    {
    	int number = 0;
    	Enumeration enu = root.preorderEnumeration();
    	while (enu.hasMoreElements())
    	{
    		enu.nextElement();
    		number++;
    	}

    	return number;
    }

    public void drop(DropTargetDropEvent dtde)
    {
      mainFr.setDropStatusText("   ...");
      mainFr.infomlFile.hasChanged = true;

      Transferable transferable = dtde.getTransferable();

      DropTarget dropTarget = (DropTarget) dtde.getSource();
      DragAndDropTree dropTree = (DragAndDropTree) dropTarget.getComponent();
      DefaultTreeModel model = (DefaultTreeModel) dropTree.getModel();

      Point loc = dtde.getLocation();
      TreePath destinationPath = getPathForLocation(loc.x, loc.y);
      Rectangle rect = getPathBounds(destinationPath);

      DefaultMutableTreeNode destinationNode = null;
      if (destinationPath != null)
         destinationNode = (DefaultMutableTreeNode)
           						destinationPath.getLastPathComponent();

      TransferableNode transfer = null;
      try
      {
    	  transfer = (TransferableNode) transferable.getTransferData
         			(java.awt.datatransfer.DataFlavor.stringFlavor);
      }
      catch (Exception e)
      { e.printStackTrace(); }


      if (destinationNode != null)
      if (! destinationNodeInDropNodeFamily(destinationNode, transfer.node,
    		  transfer.cardIds))
      {
    	  if ((root.getFirstChild().toString().compareTo(transfer.identifier1) == 0) &&
        	(root.getLastChild().toString().compareTo(transfer.identifier2) == 0) &&
        	(getNr() == transfer.identifier3) &&
        	(mainFr.getBounds().equals(transfer.identifier4)))
    	  {
    		  DefaultMutableTreeNode currentNode = root;
    		  for (int i = 1; i < transfer.nodeIndex.length; i++)
    		  {
    			  int k = 0;
    			  currentNode = (DefaultMutableTreeNode) currentNode.getFirstChild();
    			  while (k < transfer.nodeIndex[i])
    			  {
    				  currentNode = currentNode.getNextSibling();
    				  k++;
    			  }
    		  }

//    		 check if the node is dropped over itself
    		  if (destinationNode.equals(celery))
    		  {
    			  return;
    		  }


    		  Object notecardTrue = mainFr.notecards.get(currentNode);
    	      Object contentcardTrue = mainFr.uniqueContent.get(currentNode);

    	      if (notecardTrue != null)
    		  mainFr.stateSaver.saveState(currentNode, (NotecardModel) notecardTrue, "d&d",
    			transfer.node, (DefaultMutableTreeNode) currentNode.getParent(),
    	      ((DefaultMutableTreeNode) currentNode.getParent()).getIndex(currentNode));
    	      else
    	      mainFr.stateSaver.saveState(currentNode, (NotecardModel) contentcardTrue,
    	    		  "d&d", transfer.node, (DefaultMutableTreeNode) currentNode.getParent(),
        	  ((DefaultMutableTreeNode) currentNode.getParent()).getIndex(currentNode));
    	      mainFr.stateSaver.models = new Vector();

    		  model.removeNodeFromParent(currentNode);
    		  modifySubTreeInStructures(currentNode, transfer.node);


    		  // check if the node is to be dropped as the first in the window
    		  if ((destinationNode.equals(root.getFirstChild())) &&
    				  	(loc.y < rect.height / 2))
    		  {
    		  	 model.insertNodeInto(transfer.node, root, 0);

    		  	 scrollPathToVisible(new TreePath(transfer.node.getPath()));
    		  }
    		  else
              if (loc.x < rect.x + 23)
              {
            	  DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
            	  							destinationNode.getParent();
            	  int index = parent.getIndex(destinationNode);
            	  model.insertNodeInto(transfer.node, parent, index+1);

            	  scrollPathToVisible(new TreePath(transfer.node.getPath()));
              }
              else
              {
            	  model.insertNodeInto(transfer.node, destinationNode, 0);

            	  scrollPathToVisible(new TreePath(transfer.node.getPath()));
              }

              mainFr.infomlFile.rearangeElements();
    	  }
    	  else
    	  {
    		  mainFr.stateSaver.saveState(null, null, "d&d", transfer.node, null, -1);

    		  if ((destinationNode.equals(root.getFirstChild()))
    			                && (loc.y < rect.height / 2))
    		  {
    				 model.insertNodeInto(transfer.node, root, 0);

    				 scrollPathToVisible(new TreePath(transfer.node.getPath()));
    			 // System.out.println("new first node");
    		  }
    		  else
    		  if (loc.x < rect.x + 23)
              {
            	  DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
            	  							destinationNode.getParent();
            	  int index = parent.getIndex(destinationNode);
            	  model.insertNodeInto(transfer.node, parent, index+1);

            	  scrollPathToVisible(new TreePath(transfer.node.getPath()));
              }
              else
              {
            	  model.insertNodeInto(transfer.node, destinationNode, 0);

            	  scrollPathToVisible(new TreePath(transfer.node.getPath()));
              }

    		  mainFr.stateSaver.models = new Vector();
       		  addSubTreeToStructures(transfer);

    		  mainFr.infomlFile.rearangeElements();
    	  }
      }
      else
      {
    	  // do nothing
      }

      if (destinationNode == null)
      {
    	if (root.getChildCount() != 0)
    	{
    	  if ((root.getFirstChild().toString().compareTo(transfer.identifier1) == 0) &&
    		  (root.getLastChild().toString().compareTo(transfer.identifier2) == 0) &&
          		(getNr() == transfer.identifier3)&&
            	(mainFr.getBounds().equals(transfer.identifier4)))
    	  {
    		  DefaultMutableTreeNode currentNode = root;
    		  for (int i = 1; i < transfer.nodeIndex.length; i++)
    		  {
    			  int k = 0;
    			  currentNode = (DefaultMutableTreeNode) currentNode.getFirstChild();
    			  while (k < transfer.nodeIndex[i])
    			  {
    				  currentNode = currentNode.getNextSibling();
    				  k++;
    			  }
    		  }

    		  Object notecardTrue = mainFr.notecards.get(currentNode);
    	      Object contentcardTrue = mainFr.uniqueContent.get(currentNode);

    	      if (notecardTrue != null)
    		  mainFr.stateSaver.saveState(currentNode, (NotecardModel) notecardTrue,
    				  "d&d", transfer.node, (DefaultMutableTreeNode) currentNode.getParent(),
    	      ((DefaultMutableTreeNode) currentNode.getParent()).getIndex(currentNode));
    	      else
    	      mainFr.stateSaver.saveState(currentNode, (NotecardModel) contentcardTrue,
    	    		  "d&d", transfer.node, (DefaultMutableTreeNode) currentNode.getParent(),
        	  ((DefaultMutableTreeNode) currentNode.getParent()).getIndex(currentNode));
    	      mainFr.stateSaver.models = new Vector();

    		  model.removeNodeFromParent(currentNode);
    		  modifySubTreeInStructures(currentNode, transfer.node);

    		  model.insertNodeInto(transfer.node,
    				  root, root.getIndex(root.getLastChild())+1);

    		  scrollPathToVisible(new TreePath(transfer.node.getPath()));
    	  }
    	  else
    	  {
    		  mainFr.stateSaver.saveState(null, null, "d&d", transfer.node, null, -1);

    		  model.insertNodeInto(transfer.node,
    				  root, root.getIndex(root.getLastChild())+1);

    		  //System.out.println("addSubTreeToStructures called");
    		  mainFr.stateSaver.models = new Vector();
    		  addSubTreeToStructures(transfer);

    		  this.scrollPathToVisible(new TreePath(transfer.node.getPath()));
    	  }
    	}
    	else
    	{
    		  mainFr.stateSaver.saveState(null, null, "d&d", transfer.node, null, -1);

    		  model.insertNodeInto(transfer.node, root, 0);

    		  this.scrollPathToVisible(new TreePath(transfer.node.getPath()));

    		  //System.out.println("addSubTreeToStructures called");
    		  mainFr.stateSaver.models = new Vector();
    		  addSubTreeToStructures(transfer);
    		  mainFr.enableSave();
    	}

    	mainFr.infomlFile.rearangeElements();
      }

      this.repaint();
    }


}
