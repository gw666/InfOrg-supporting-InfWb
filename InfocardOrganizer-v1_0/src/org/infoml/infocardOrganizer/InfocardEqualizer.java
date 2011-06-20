package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/


import java.io.*;
import java.util.*;
//import javax.swing.*;
import javax.swing.tree.*;

import org.infoml.jaxb.*;
import org.infoml.jaxb.impl.*;


public class InfocardEqualizer extends Thread
{
	NotecardModel modifiedNotecardModel;
	UniqueContentModel modifiedContentcardModel;

	InfomlType modifiedInfomlType;

	int flag;
	MainFrame mainFr;
	ObjectOutputStream oos;
	ObjectInputStream ois;

	/* infocardDataBase is used only in this class */
	private  Map infocardDataBase;

	public InfocardEqualizer()
	{
		try
		{
			if ((new File(System.getProperty("user.dir") + File.separator + "infocardDataBase"))
										.exists())
			{
				FileInputStream fis = new FileInputStream(
						System.getProperty("user.dir") + File.separator + "infocardDataBase");
				ois = new ObjectInputStream(fis);

				infocardDataBase = (Map) ois.readObject();

				ois.close();
			}
			else
			{
				infocardDataBase = new HashMap();
			}

		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public MainFrame getMainFr()
	{
		return mainFr;
	}

	public void setMainFr(MainFrame mainFr)
	{
		this.mainFr = mainFr;
	}

	public InfomlType searchForNewValue(NotecardModel model)
	{
		Vector windows = MainFrame.windows;

		for (int i = 0; i < windows.size(); i++)
		{
			MainFrame mainFr = (MainFrame) windows.elementAt(i);
			if (mainFr.infocards.get(model) != null)
				return (InfomlType) mainFr.infocards.get(model);
		}

		return null;
	}

	public void equalizeNotecards()
	{
		Vector windows = MainFrame.windows;

		for (int i = 0; i < windows.size(); i++)
		{
			MainFrame currentWindow = (MainFrame) windows.elementAt(i);

			Set entries = Collections.synchronizedSet(
					new HashSet(currentWindow.notecards.entrySet()));

			for (Iterator j = entries.iterator(); j.hasNext();)
			{
				Map.Entry entry = (Map.Entry) j.next();

				NotecardModel model = (NotecardModel) entry.getValue();
				DefaultMutableTreeNode node =
								(DefaultMutableTreeNode) entry.getKey();

				//InfomlType newValue = modifiedInfomlType;

				if (model.getCardId().compareTo(modifiedNotecardModel.getCardId()) == 0)
				{
					currentWindow.notecards.remove(node);
					node.setUserObject(modifiedNotecardModel.getTitle());

					/*DefaultMutableTreeNode dummy =
						new DefaultMutableTreeNode();
					node.add(dummy);
					currentWindow.infoWindow.tree.
						scrollPathToVisible(new TreePath(dummy.getPath()));
					node.remove(dummy);		*/

					currentWindow.notecards.put(node, modifiedNotecardModel);

					InfocardBuilder infocardBuilder =
						// atentie - aici era mainFr in loc de currentWindow
						new InfocardBuilder(currentWindow.infomlFile, modifiedNotecardModel);

					infocardBuilder.setStandardInfocard();
					infocardBuilder.infocard.setCardId(modifiedNotecardModel.getCardId());

					infocardBuilder.setContent();
					infocardBuilder.setTitle();
					infocardBuilder.setSelectors();
					infocardBuilder.setNotes1();
					infocardBuilder.setNotes2();
					infocardBuilder.setCreator(mainFr.setupModel);
					infocardBuilder.setDateCreated();
					currentWindow.infocards.remove(model);
					currentWindow.infocards.put(modifiedNotecardModel,
							infocardBuilder.infocard);

					//currentWindow.infomlFile.if1.getInfoml().remove(index);
					//currentWindow.infomlFile.if1.getInfoml().add(index, newValue);

					currentWindow.infomlFile.rearangeElements();

					currentWindow.infoWindow.tree.setSelectionPath
								(new TreePath(node.getPath()));
					currentWindow.editableNotecardModel = modifiedNotecardModel;
				}
			}
		}
	}

	public void equalizeContentcards()
	{
		Vector windows = MainFrame.windows;

		for (int i = 0; i < windows.size(); i++)
		{
			MainFrame currentWindow = (MainFrame) windows.elementAt(i);

			Set entries = Collections.synchronizedSet(
					new HashSet(currentWindow.uniqueContent.entrySet()));

			for (Iterator j = entries.iterator(); j.hasNext();)
			{
				Map.Entry entry = (Map.Entry) j.next();

				UniqueContentModel model = (UniqueContentModel) entry.getValue();
				DefaultMutableTreeNode node =
								(DefaultMutableTreeNode) entry.getKey();
				//InfomlType newValue = modifiedInfomlType;

				if (modifiedContentcardModel.getCardId() != null)
				if (model.getCardId().compareTo(modifiedContentcardModel.getCardId()) == 0)
				{
					currentWindow.uniqueContent.remove(node);

					node.setUserObject(modifiedContentcardModel.getTitle());
					/*DefaultMutableTreeNode dummy =
									new DefaultMutableTreeNode();
					node.add(dummy);
					currentWindow.infoWindow.tree.
						scrollPathToVisible(new TreePath(dummy.getPath()));
					node.remove(dummy);*/

					currentWindow.uniqueContent.put(node, modifiedContentcardModel);

				//	System.out.println("MainFr : " + mainFr);
//					 atentie - aici era mainFr in loc de currentWindow
					InfocardBuilder infocardBuilder =
						new InfocardBuilder(currentWindow.infomlFile,
								modifiedContentcardModel);

					infocardBuilder.setStandardInfocard();
					infocardBuilder.infocard.setCardId(modifiedContentcardModel.getCardId());

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
					infocardBuilder.setAgentX(modifiedContentcardModel.shared.getAgent1());
					infocardBuilder.agentType = null;
					infocardBuilder.setAgentX(modifiedContentcardModel.shared.getAgent2());
					infocardBuilder.agentType = null;
					infocardBuilder.setAgentX(modifiedContentcardModel.shared.getAgent3());
					infocardBuilder.agentType = null;
					infocardBuilder.setAgentX(modifiedContentcardModel.shared.getAgent4());
					infocardBuilder.agentType = null;
					infocardBuilder.setEnclosingAgentX(modifiedContentcardModel.shared.getAgent5());
					infocardBuilder.agentType = null;
					infocardBuilder.setEnclosingAgentX(modifiedContentcardModel.shared.getAgent6());
					infocardBuilder.agentType = null;
					infocardBuilder.setEnclosingContainerVolumeIssue();
					infocardBuilder.setEnclosingContainerTitle();
					infocardBuilder.setCorporateAgent();

					//int index = currentWindow.infomlFile.if1.getInfoml().indexOf(value);

					currentWindow.infocards.remove(model);
					currentWindow.infocards.put(modifiedContentcardModel,
							infocardBuilder.infocard);

					currentWindow.infomlFile.rearangeElements();

					//currentWindow.infomlFile.if1.getInfoml().remove(index);
					//currentWindow.infomlFile.if1.getInfoml().add(index, newValue);

					currentWindow.infoWindow.tree.setSelectionPath
								(new TreePath(node.getPath()));
					currentWindow.editableContentModel = modifiedContentcardModel;
				}

				if (model.shared == modifiedContentcardModel.shared)
				{
					InfocardBuilder infocardBuilder =
						new InfocardBuilder(currentWindow.infomlFile, model);

					infocardBuilder.setStandardInfocard();
					infocardBuilder.infocard.setCardId(model.getCardId());

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

					currentWindow.infomlFile.rearangeElements();
				}
			}
		}
	}

	public void fileOpened(MainFrame mainFr)
	{
	  if (infocardDataBase.size() != 0)
	  {
		Set entries = Collections.synchronizedSet(
				new HashSet(mainFr.uniqueContent.entrySet()));

		for (Iterator j = entries.iterator(); j.hasNext();)
		{
			Map.Entry entry = (Map.Entry) j.next();

			UniqueContentModel model = (UniqueContentModel) entry.getValue();
			DefaultMutableTreeNode node =
							(DefaultMutableTreeNode) entry.getKey();

			UniqueContentModel updatedModel = (UniqueContentModel)
							infocardDataBase.get(model.getCardId());

			if (updatedModel != null)
			{
				mainFr.uniqueContent.remove(node);

				node.setUserObject(updatedModel.getTitle());

				/*DefaultMutableTreeNode dummy =
								new DefaultMutableTreeNode();
				node.add(dummy);
				mainFr.infoWindow.tree.
					scrollPathToVisible(new TreePath(dummy.getPath()));
				node.remove(dummy);*/

				mainFr.uniqueContent.put(node, updatedModel);

				InfomlType value =
					(InfomlType) mainFr.infocards.get(model);

				mainFr.infocards.remove(model);

				InfocardBuilder infocardBuilder = new
			    		InfocardBuilder(mainFr.infomlFile, updatedModel);

				infocardBuilder.setStandardInfocard();
				infocardBuilder.infocard.setCardId(value.getCardId());

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
				infocardBuilder.setAgentX(updatedModel.shared.getAgent1());
				infocardBuilder.agentType = null;
				infocardBuilder.setAgentX(updatedModel.shared.getAgent2());
				infocardBuilder.agentType = null;
				infocardBuilder.setAgentX(updatedModel.shared.getAgent3());
				infocardBuilder.agentType = null;
				infocardBuilder.setAgentX(updatedModel.shared.getAgent4());
				infocardBuilder.agentType = null;
				infocardBuilder.setEnclosingAgentX(updatedModel.shared.getAgent5());
				infocardBuilder.agentType = null;
				infocardBuilder.setEnclosingAgentX(updatedModel.shared.getAgent6());
				infocardBuilder.agentType = null;
				infocardBuilder.setEnclosingContainerVolumeIssue();
				infocardBuilder.setEnclosingContainerTitle();
				infocardBuilder.setCorporateAgent();

				mainFr.infocards.put(updatedModel, infocardBuilder.infocard);
			}
		}

		Set entries2 = Collections.synchronizedSet(
				new HashSet(mainFr.notecards.entrySet()));

		for (Iterator j = entries2.iterator(); j.hasNext();)
		{
			Map.Entry entry = (Map.Entry) j.next();

			NotecardModel model = (NotecardModel) entry.getValue();
			DefaultMutableTreeNode node =
							(DefaultMutableTreeNode) entry.getKey();

			NotecardModel updatedModel = (NotecardModel)
							infocardDataBase.get(model.getCardId());

			if (updatedModel != null)
			{
				mainFr.notecards.remove(node);

				node.setUserObject(updatedModel.getTitle());

				/*DefaultMutableTreeNode dummy =
								new DefaultMutableTreeNode();
				node.add(dummy);
				mainFr.infoWindow.tree.
					scrollPathToVisible(new TreePath(dummy.getPath()));
				node.remove(dummy);*/

				mainFr.notecards.put(node, updatedModel);

				InfomlType value =
					(InfomlType) mainFr.infocards.get(model);

				mainFr.infocards.remove(model);

				InfocardBuilder infocardBuilder =
					new InfocardBuilder(mainFr.infomlFile, updatedModel);

				infocardBuilder.setStandardInfocard();
				infocardBuilder.infocard.setCardId(value.getCardId());

				infocardBuilder.setContent();
				infocardBuilder.setTitle();
				infocardBuilder.setSelectors();
				infocardBuilder.setNotes1();
				infocardBuilder.setNotes2();
				infocardBuilder.setCreator(mainFr.setupModel);
				infocardBuilder.setDateCreated();

				mainFr.infocards.put(updatedModel, infocardBuilder.infocard);
			}
		}

		 mainFr.infomlFile.rearangeElements();

	  }
	}

	/*
	 * Writes map infocardDataBase to a file of the same name (?)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(
					System.getProperty("user.dir") + File.separator + "infocardDataBase", false);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(infocardDataBase);

			oos.flush();
			oos.close();
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public NotecardModel getModifiedNotecardModel()
	{
		return modifiedNotecardModel;
	}

	public void setModifiedNotecardModel(NotecardModel currentModel)
	{
		MainFrame.modifiedCardIds.add(currentModel.getCardId());
		infocardDataBase.remove(currentModel.getCardId());
		infocardDataBase.put(currentModel.getCardId(), currentModel);
		this.modifiedNotecardModel = currentModel;
	}

	public NotecardModel getModifiedContentcardModel()
	{
		return modifiedContentcardModel;
	}

	public void setModifiedContentcardModel(UniqueContentModel modifiedContentcardModel)
	{
		MainFrame.modifiedCardIds.add(modifiedContentcardModel.getCardId());
		infocardDataBase.remove(modifiedContentcardModel.getCardId());
		infocardDataBase.put(modifiedContentcardModel.getCardId(),
				modifiedContentcardModel);
		this.modifiedContentcardModel = modifiedContentcardModel;
	}

	public InfomlType getModifiedInfomlType()
	{
		return modifiedInfomlType;
	}

	public void setModifiedInfomlType(InfomlType modifiedInfomlType)
	{
		this.modifiedInfomlType = modifiedInfomlType;
	}

	/*public void keepStructure(DefaultMutableTreeNode Unode,
			int index, String Id, String pId, String op,
			int count, DefaultMutableTreeNode insParent,
			NotecardModel addModel, InfomlType infoml, int option
			)
	{
		Vector windows = (Vector) MainFrame.windows.clone();

		for (int i = 0; i < windows.size(); i++)
		{
			System.out.println("---------------------------------------------");
			int cardsFound = 0, cardsVisited = 0;
			boolean found = true;
			MainFrame currentWindow = (MainFrame) windows.elementAt(i);

			DefaultMutableTreeNode first = currentWindow.infoWindow.tree.root;

			while (found)
			{
			found = false;
			cardsFound = 0;

			Enumeration en = first.preorderEnumeration();

			//	avoid invisible root element
			en.nextElement();
			while (en.hasMoreElements())
			{
				DefaultMutableTreeNode parent =
						(DefaultMutableTreeNode) en.nextElement();

				NotecardModel model = (NotecardModel)
									currentWindow.notecards.get(parent);
				if (model == null)
					model = (NotecardModel)	currentWindow.uniqueContent.get(parent);

				if (pId.equals(model.getCardId()))
				{
					if (op.compareTo("del") == 0)
					{
					  if (parent.getChildCount() > index)
					  {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)
														parent.getChildAt(index);
						NotecardModel nModel = (NotecardModel)
												currentWindow.notecards.get(node);
						if (nModel == null)
							nModel = (NotecardModel) currentWindow.
											uniqueContent.get(node);

						if ((parent.getChildCount() == count) &&
								(Id.equals(nModel.getCardId())))
						{
						currentWindow.infoWindow.tree.makeVisible(new TreePath(
								((DefaultMutableTreeNode)
										parent.getChildAt(index)).getPath()));

						currentWindow.infoWindow.tree.setSelectionPath
							(new TreePath(((DefaultMutableTreeNode)
									parent.getChildAt(index)).getPath()));

						currentWindow.infoWindow.removeNode();

						found = true;
						break;
						}
					  }
					}
				  if (op.compareTo("add") == 0)
				  {
					  System.out.println(parent + "bronz" + index + "   " +
							  			parent.getChildCount());

					  if (index < parent.getChildCount()+1)
					  {
						  System.out.println("argint");
						cardsFound++;

						if (cardsFound > cardsVisited)
						{
							System.out.println("aur");
							System.out.println(parent.getChildCount() + "    " +
									childCountBeforeAdd);
						if (childCountBeforeAdd != parent.getChildCount())
						{
							System.out.println("platina platinata");
						currentWindow.infomlFile.addInfocard(infoml, addModel);
						currentWindow.infoWindow.insertNodeInto(addModel, null,
								parent, index, option);
						currentWindow.infomlFile.rearangeElements();

						}
						cardsVisited++;
						found = true;
						break;
						}
					  }
				  }
				  if (op.compareTo("addU") == 0)
				  {
					  if (index < parent.getChildCount()+1)
					  {
						cardsFound++;

						if (cardsFound > cardsVisited)
						{
							currentWindow.infomlFile.addInfocard(infoml, addModel);
							currentWindow.infoWindow.insertNodeInto(addModel, Unode,
									parent, index, option);
							currentWindow.infomlFile.rearangeElements();

							cardsVisited++;
							found = true;
							break;
						}
					  }
				  }
				}
			}
			}
		}
	}*/

}

