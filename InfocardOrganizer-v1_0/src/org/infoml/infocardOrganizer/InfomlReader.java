package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/


import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

import org.infoml.jaxb.*;
import org.infoml.jaxb.impl.*;


import javax.swing.*;
import javax.swing.tree.*;

public class InfomlReader
{
	JAXBContext jc;
	private Unmarshaller u;

	List content, notes1, notes2;
	String title;
	String filePath;
	private List infocardList;
	Main infomlFile;
	MainFrame mainFr;

	int option = 1;
	NotecardModel model;
	UniqueContentModel model2;
	SharedContentModel shared;

	static Vector unrepetableNicknames = new Vector();;

	Vector ids = null, levels = null;

	String aux;

	HashMap auxMap;
	InfocardWindow window;
	int realInfocards = 0;

	public InfomlReader(MainFrame mainFr, Main infomlFile, String filePath)
	{
		try
		{
			this.mainFr = mainFr;
			this.infomlFile = infomlFile;

			jc = infomlFile.jc;

			u = jc.createUnmarshaller();
			u.setValidating(true);

			this.filePath = filePath;
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public List getInfocardList()
	{
		return infocardList;
	}

	public int readInfomlFile()
	{
		try
		{
			InfomlFileType uf = (InfomlFile) u.unmarshal(
					new FileInputStream(filePath));

			infocardList = uf.getInfoml();
		}
		catch (Exception e)
		{ e.printStackTrace(); return 0;}

		return 1;
	}

	public boolean realInfocard(int ind)
	{
		InfomlType infocard = (InfomlType) infocardList.get(ind);

		if (infocard.getData() != null)
			if (infocard.getData().getPointers() != null)
				return false;

		return true;
	}

	public void setOutlineVector(int ind)
	{
		int insertIndex = -1;
		InfomlType infocard = (InfomlType) infocardList.get(ind);

		String current = infocard.getCardId();

		for (int i = 0; i < ids.size(); i++)
		  if (((String) ids.elementAt(i)).compareTo(current) == 0)
          {
			  insertIndex = ids.indexOf(ids.elementAt(i));
			  break;
	      }

		if (insertIndex != -1)
		{
			RichPointerType rpt = (RichPointerType)
		       infocard.getData().getPointers().getParentPtr();

			String newId = rpt.getTargetId();
			int level = ((Integer) levels.elementAt(insertIndex)).intValue();

			int aux = insertIndex;
			ids.remove(insertIndex);
			ids.add(insertIndex, newId);
//			levels.add(aux, new Integer(insertIndex));
			aux++;

			for (int i = 0; i < infocard.getData().getPointers().getPtr().size(); i++)
			{
				rpt = (RichPointerType)
				       infocard.getData().getPointers().getPtr().get(i);

				newId = rpt.getTargetId();

				ids.add(aux, newId);
				levels.add(aux, new Integer(level+1));

				aux++;
			}
		}
		else
		{
			ids.add(current);
			levels.add(new Integer(0));

			for (int i = 0; i < infocard.getData().getPointers().getPtr().size(); i++)
			{
				RichPointerType rpt = (RichPointerType)
					infocard.getData().getPointers().getPtr().get(i);

				ids.add(rpt.getTargetId());
				levels.add(new Integer(1));
			}
		}
	}

	//	 this is called from invokeAndWait
	public void displayInfoml(InfocardWindow win)
	{
	  window = win;
	  try
	  {
		int ind = 0;
		int outlineInd = 0;

		//unrepetableNicknames = new Vector();
		ids = new Vector();
		levels = new Vector();
		auxMap = new HashMap();

		for (int i = 0; i < infocardList.size(); i++)
		{
			if (! realInfocard(i))
			{
				setOutlineVector(i);
			}
		}

		//int levelIndex = 0;
		for (ListIterator i = infocardList.listIterator(); i.hasNext();)
		{
			// the model associated to the infocard
			model = new NotecardModel();

			InfomlType infocard = getInfocard(ind);
			if (infocard != null)
			{
			  if (option == 1)
				auxMap.put(infocard, model);
			  else
			  if (option == 2)
				auxMap.put(infocard, model2);

			  realInfocards++;
			}
			ind++;

			// reinitialized for each infocard reading operation
			option = 1;
			model2 = null;
			title = null;
			content = null;

			i.next();


		}
		System.gc();

		completeInfomlList();

		completeStructuresForDuplicateElements();
	  }
	  catch (Exception e)
	  { e.printStackTrace(); }
	}

	public UniqueContentModel cloneContentcardModel(UniqueContentModel cloned)
	{
		UniqueContentModel newCModel = new UniqueContentModel();

		newCModel.setCardId(cloned.getCardId());

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

		newCModel.shared.setContainerNamePart1(
				cloned.shared.getContainerNamePart1());
		newCModel.shared.setContainerNamePart2(
				cloned.shared.getContainerNamePart2());
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

		newCModel.shared.setEnclCntNamePart1(cloned.shared.getEnclCntNamePart1());
		newCModel.shared.setEnclCntNamePart2(cloned.shared.getEnclCntNamePart2());
		newCModel.shared.setEnclPreSepar(cloned.shared.getEnclPreSepar());
		newCModel.shared.setEnclRole(cloned.shared.getEnclRole());
		newCModel.shared.setEnclCorporateName(cloned.shared.getEnclCorporateName());

		newCModel.shared.setNickname(cloned.shared.getNickname());

		newCModel.shared.setSublocationPart1(cloned.shared.getSublocationPart1());
		newCModel.shared.setSublocationPart2(cloned.shared.getSublocationPart2());

		return newCModel;
	}

	public NotecardModel cloneNotecardModel(NotecardModel cloned)
	{
		NotecardModel newNModel = new NotecardModel();

		newNModel.setCardId(cloned.getCardId());

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

		return newNModel;
	}

	public void completeStructuresForDuplicateElements()
	{
	  NotecardModel oldModel = null;
	  int duplicater = 0;

	  for (int i = 0; i < ids.size(); i++)
	  {
		for (int k = i+1; k < ids.size(); k++)
		{
		  String duplicateId = null;

		  if (((String) ids.elementAt(i)).compareTo((String)ids.elementAt(k)) == 0)
		  {
			duplicateId = (String) ids.elementAt(k);

			InfomlType value = null;
			NotecardModel key = null;
			for (Iterator j = mainFr.infocards.entrySet().iterator(); j.hasNext();)
			{
				Map.Entry entry = (Map.Entry) j.next();

				value = (InfomlType) entry.getValue();
				if (value.getCardId().compareTo(duplicateId) == 0)
				{
					key = (NotecardModel) entry.getKey();
					oldModel = (NotecardModel) entry.getKey();

					if (key instanceof UniqueContentModel)
					{
						key = cloneContentcardModel((UniqueContentModel) key);
					}
					else
					if (key instanceof NotecardModel)
					{
						key = cloneNotecardModel(key);
					}

					duplicater = k;

					break;
				}
			}

			mainFr.infocards.put(key, value);

			DefaultMutableTreeNode node = null;
			for (Iterator iter = mainFr.notecards.entrySet().iterator(); iter.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iter.next();

				node = (DefaultMutableTreeNode) entry.getKey();
				break;
			}
			if (node == null)
			for (Iterator iter = mainFr.uniqueContent.entrySet().iterator(); iter.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iter.next();

				node = (DefaultMutableTreeNode) entry.getKey();
				break;
			}

			Enumeration e = ((DefaultMutableTreeNode)
					node.getRoot()).preorderEnumeration();

			int ind = -1;
			DefaultMutableTreeNode current = null;
			while ((ind < duplicater) && (e.hasMoreElements()))
			{
				current = (DefaultMutableTreeNode) e.nextElement();
				ind++;
			}

			if (key instanceof UniqueContentModel)
			{
				mainFr.uniqueContent.put(current, key);
			}
			else
			if (key instanceof NotecardModel)
			{
				mainFr.notecards.put(current, key);
			}
		  }
		}
	  }
	}

	public void completeInfomlList()
	{
		for (int i = 0; i < ids.size(); i++)
		{
			String neededId = (String) ids.get(i);

			for (Iterator j = auxMap.entrySet().iterator(); j.hasNext();)
			{
				Map.Entry entry = (Map.Entry) j.next();

				InfomlType current = (InfomlType) entry.getKey();
				if (current.getCardId().compareTo(neededId) == 0)
				{
					NotecardModel model = (NotecardModel) entry.getValue();

				    if (model instanceof UniqueContentModel)
				    {
				    	option = 2;

				    	window.addNodeFromSaved(model.getTitle(), option, model,
								 ((Integer) levels.elementAt(i)).intValue());
				    }
				    else
				    if (model instanceof NotecardModel)
					{
					   	option = 1;

					   	window.addNodeFromSaved(model.getTitle(), option, model,
							 ((Integer) levels.elementAt(i)).intValue());
					}

				    break;
				}
			}
		}
	}

	public UniqueContentModel turnToUniqueContentModel(NotecardModel model)
	{
		model2 = new UniqueContentModel();

		shared = new SharedContentModel();
		model2.setShared(shared);

		model2.setTitle(model.getTitle());
		model2.setContentString(model.getContentString());
		model2.setContentList(model2.getContentString());

		model2.setSelectorsString(model.getSelectorsString());
		model2.setSelectorsList(model2.getSelectorsString());

		model2.setNotes1String(model.getNotes1String());
		model2.setNotes1List(model2.getNotes1String());

		model2.setNotes2String(model.getNotes2String());
		model2.setNotes2List(model.getNotes2String());

		return model2;
	}

	/*	This method completes the infocard object and a corresponding
	 *  NotecardModel object.
	 */
	public InfomlType getInfocard(int ind)
	{
		try
		{
		  InfomlType infocard = (InfomlType) infocardList.get(ind);

		  if (! realInfocard(ind))
			  return null;

			//title = "No title";
			//noTitles.add(title);
			if (infocard.getData().getTitle() != null)
			{
			   title = (String) infocard.getData().getTitle().getContent().get(0);
			   model.setTitle(title);
			}

			if (infocard.getSelectors() != null)
			{
				model.selectorsList = infocard.getSelectors().getTag();

				model.setSelectorsStringFromList(model.selectorsList);
			}

			if (infocard.getData() != null)
			if (infocard.getData().getNotes1() != null)
			{
				//RichTextType.P par = (RichTextType.P)
				//	infocard.getData().getNotes1().getPOrQuotationOrPoem().get(0);


				if (infocard.getData() != null)
			    if (infocard.getData().getNotes1() != null)
			    {
				RichTextType rtt =
					(RichTextType) infocard.getData().getNotes1();

				if (rtt != null)
				{
				  notes1 = new ArrayList();

				  List not1 = rtt.getPOrQuotationOrPoem();

			      for (ListIterator k = not1.listIterator();  k.hasNext();)
				  {
			    	Object obj = k.next();
			    	{
			    		RichTextType.P parList = (RichTextType.P) obj;
			    		String par2 = (String) parList.getContent().get(0);
			    		notes1.add(par2);
			    	}
				  }

			      model.setNotes1StringFromList(notes1);
			      model.setNotes1List(model.getNotes1String());
				}
				}

				if (infocard.getData() != null)
				if (infocard.getData().getNotes2() != null)
				{
				  RichTextType rtt =
					(RichTextType) infocard.getData().getNotes1();

				  if (rtt != null)
				  {
					  notes2= new ArrayList();

					  List not2 = rtt.getPOrQuotationOrPoem();

				      for (ListIterator k = not2.listIterator();  k.hasNext();)
					  {
				    	Object obj = k.next();
				    	{
				    		RichTextType.P parList = (RichTextType.P) obj;
				    		String par2 = (String) parList.getContent().get(0);
				    		notes2.add(par2);
				    	}
					  }

				      model.setNotes2StringFromList(notes2);
				      model.setNotes2List(model.getNotes2String());
					}
				}
				/*if (infocard.getData().getNotes2() != null)
				model.setNotes2((String) ((RichTextType.P) infocard.getData().
					getNotes2().getPOrQuotationOrPoem().get(0)).getContent().get(0));*/
			}

			if (infocard.getData() != null)
			{
			RichTextWithExactType rtwet =
				(RichTextWithExactType) infocard.getData().getContent();

			if (rtwet != null)
			{
			  content = new ArrayList();

			  List cont = rtwet.getPOrQuotationOrPoem();


		      for (ListIterator k = cont.listIterator();  k.hasNext();)
			  {
		    	Object obj = k.next();
		    	{
		    		RichTextType.P parList = (RichTextType.P) obj;
		    		String par = (String) parList.getContent().get(0);
		    		content.add(par);
		    	}
			  }

		      model.setContentStringFromList(content);
		      model.setContentList(model.getContentString());

		      // if the exact parameter is set to false
			  // we are dealing with a contentcard
			  if (!rtwet.isExact())
			  {
				  model2 = turnToUniqueContentModel(model);
				  model2.setExact(rtwet.isExact());

				  option = 2;
			  }
			}
			}

			if (infocard.getData() != null)
			if (infocard.getData().getSource() != null)
			if (infocard.getData().getSource().getLocation() != null)
			{
				if (model2 == null)
				{
					model2 = turnToUniqueContentModel(model);
				}

				if (infocard.getData().getSource().getLocation().
					getPoint() != null)
				{
					model2.setPage1(((PointType) infocard.getData().
					getSource().getLocation().getPoint()).getValue());
				}
				if (infocard.getData().getSource().getLocation().getRange() != null)
				{
					model2.setPage1(((RangeType) infocard.getData().
					getSource().getLocation().getRange()).getBegin());

					model2.setPage2(((RangeType) infocard.getData().
					getSource().getLocation().getRange()).getEnd());
				}

				option = 2;
			}

			if (infocard.getData() != null)
			if (infocard.getData().getSource() != null)
			if (infocard.getData().getSource().getContainer() != null)
			if (infocard.getData().getSource().
					getContainer().getContainerNamePart() != null)
			{
				if (model2 == null)
				{
					model2 = turnToUniqueContentModel(model);
				}

				SimpleRichTextWithSeparatorType srtwst =
				((SimpleRichTextWithSeparatorType) infocard.getData().getSource().
							getContainer().getContainerNamePart().get(0));

				String contNamePart1 = (String) srtwst.getContent().get(0);

				String preSepar = ((SimpleRichTextWithSeparatorType) infocard.getData().
							getSource().getContainer().getContainerNamePart().
							get(0)).getPreSeparator();
				if (preSepar != null)
					model2.shared.setPreSepar(preSepar);

				if (contNamePart1 != null)
					model2.shared.setContainerNamePart1(contNamePart1);

				if (infocard.getData().getSource().
							getContainer().getContainerNamePart().size() > 1)
				{
				SimpleRichTextWithSeparatorType srtwst2 =
				((SimpleRichTextWithSeparatorType) infocard.getData().getSource().
								getContainer().getContainerNamePart().get(1));

				preSepar = ((SimpleRichTextWithSeparatorType) infocard.getData().
						getSource().getContainer().getContainerNamePart().
						get(1)).getPreSeparator();

				if (preSepar != null)
					model2.shared.setPreSepar(preSepar);

				String contNamePart2 = (String) srtwst2.getContent().get(0);

				if (contNamePart2 != null)
					model2.shared.setContainerNamePart2(contNamePart2);
				}
				option = 2;
			}

			if (infocard.getData() != null)
			if (infocard.getData().getSource() != null)
			if (infocard.getData().getSource().getContainer() != null)
			if (infocard.getData().getSource().getContainer().getDate() != null)
			{
				if (model2 == null)
				{
					model2 = turnToUniqueContentModel(model);
				option = 2;
				}

				if (infocard.getData().getSource().getContainer().getDate().
						size() != 0)
				{
					DateWithRoleType dwrt = (DateWithRoleType) infocard.getData().
							getSource().getContainer().getDate().get(0);

					if ((dwrt.getValue()) instanceof String)
					{
						String date = (String) dwrt.getValue();

						StringTokenizer tokenizer = new StringTokenizer(date, "-");

						model2.shared.setDate3((new Integer
								(tokenizer.nextToken())).intValue());

						if (tokenizer.hasMoreTokens())
					    model2.shared.setDate1((new Integer(tokenizer.nextToken())).
																	intValue());

						if (tokenizer.hasMoreTokens())
					    model2.shared.setDate2((new Integer(tokenizer.nextToken())).
																	intValue());
					}
					else
					if ((dwrt.getValue()) instanceof GregorianCalendar)
					{
						GregorianCalendar date = (GregorianCalendar) dwrt.getValue();

						model2.shared.setDate3(date.get(GregorianCalendar.YEAR));

						if (date.get(GregorianCalendar.MONTH) != 0)
						model2.shared.setDate1(date.get(GregorianCalendar.MONTH));

						if (date.get(GregorianCalendar.DATE) != 0)
						model2.shared.setDate2(date.get(GregorianCalendar.DATE));
					}

					model2.shared.setDateRole(dwrt.getRole());
				}
			}

			if (infocard.getData() != null)
			if (infocard.getData().getSource() != null)
			if (infocard.getData().getSource().getAgent() != null)
			{
				int k = 0;
				for (int i = 0; i < infocard.getData().getSource().getAgent().size(); i++)
				{
					AgentType agentType = (AgentType) infocard.getData().
												getSource().getAgent().get(i);

					if (agentType.getRole().compareTo("card creator") != 0)
					{
						option = 2;
						if (model2 == null)
						{
							model2 = turnToUniqueContentModel(model);
						}

					if (k == 0)
					{
						model2.shared.getAgent1().setRole(agentType.getRole());
						model2.shared.getAgent1().setSurname(((String)
						(((NonnullTokenWithSeparatorType) agentType.getSurname()).getValue())));


						if (agentType.getFirst() != null)
						{
						NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
								agentType.getFirst();

						model2.shared.getAgent1().setFirst(ntwst.getValue());
						}

						if (!agentType.getPrefix().isEmpty())
						{
							model2.shared.getAgent1().setPrefix((String)
									agentType.getPrefix().get(0));
						}

						if (!agentType.getMiddle().isEmpty())
						{
							NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
									agentType.getMiddle().get(0);

							model2.shared.getAgent1().setMiddle1(ntwst.getValue());

							if (agentType.getMiddle().size() > 1)
							{
							NonnullTokenWithSeparatorType ntwst2 = (NonnullTokenWithSeparatorType)
								agentType.getMiddle().get(1);
							model2.shared.getAgent1().setMiddle2(ntwst2.getValue());
							}
						}

						if (!agentType.getSuffix().isEmpty())
						{
							model2.shared.getAgent1().setSuffix
								(((NonnullTokenWithSeparatorType) agentType.getSuffix().
										get(0)).getValue());

							String preSepar = ((NonnullTokenWithSeparatorType)
										agentType.getSuffix().get(0)).getPreSeparator();

							if (preSepar != null)
								model2.shared.getAgent1().setPreSeparator(preSepar);
						}
					}

					if (k == 1)
					{
						model2.shared.getAgent2().setRole(agentType.getRole());
						model2.shared.getAgent2().setSurname(((String)
								(((NonnullTokenWithSeparatorType) agentType.getSurname()).getValue())));

						if (agentType.getFirst() != null)
						{
						NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
								agentType.getFirst();

						model2.shared.getAgent2().setFirst(ntwst.getValue());
						}

						if (!agentType.getPrefix().isEmpty())
						{
							model2.shared.getAgent2().setPrefix((String)
									agentType.getPrefix().get(0));
						}

						if (!agentType.getMiddle().isEmpty())
						{
							NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
									agentType.getMiddle().get(0);

							model2.shared.getAgent2().setMiddle1(ntwst.getValue());

							if (agentType.getMiddle().size() > 1)
							{
							NonnullTokenWithSeparatorType ntwst2 = (NonnullTokenWithSeparatorType)
								agentType.getMiddle().get(1);
							model2.shared.getAgent2().setMiddle2(ntwst2.getValue());
							}
						}

						if (!agentType.getSuffix().isEmpty())
						{
							model2.shared.getAgent2().setSuffix
								(((NonnullTokenWithSeparatorType) agentType.getSuffix().
										get(0)).getValue());

							String preSepar = ((NonnullTokenWithSeparatorType)
									agentType.getSuffix().get(0)).getPreSeparator();

							if (preSepar != null)
								model2.shared.getAgent2().setPreSeparator(preSepar);
						}
					}

					if (k == 2)
					{
						model2.shared.getAgent3().setRole(agentType.getRole());
						model2.shared.getAgent3().setSurname(((String)
								(((NonnullTokenWithSeparatorType) agentType.getSurname()).getValue())));

						if (agentType.getFirst() != null)
						{
						NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
								agentType.getFirst();

						model2.shared.getAgent3().setFirst(ntwst.getValue());
						}

						if (!agentType.getPrefix().isEmpty())
						{
							model2.shared.getAgent3().setPrefix((String)
									agentType.getPrefix().get(0));
						}

						if (!agentType.getMiddle().isEmpty())
						{
							NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
									agentType.getMiddle().get(0);

							model2.shared.getAgent3().setMiddle1(ntwst.getValue());

							if (agentType.getMiddle().size() > 1)
							{
							NonnullTokenWithSeparatorType ntwst2 = (NonnullTokenWithSeparatorType)
								agentType.getMiddle().get(1);
							model2.shared.getAgent3().setMiddle2(ntwst2.getValue());
							}
						}

						if (!agentType.getSuffix().isEmpty())
						{
							model2.shared.getAgent3().setSuffix
								(((NonnullTokenWithSeparatorType) agentType.getSuffix().
									get(0)).getValue());

							String preSepar = ((NonnullTokenWithSeparatorType)
									agentType.getSuffix().get(0)).getPreSeparator();

							if (preSepar != null)
								model2.shared.getAgent3().setPreSeparator(preSepar);
						}
					}

					if (k == 3)
					{
						model2.shared.getAgent4().setRole(agentType.getRole());
						model2.shared.getAgent4().setSurname(((String)
								(((NonnullTokenWithSeparatorType) agentType.getSurname()).getValue())));

						if (agentType.getFirst() != null)
						{
						NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
								agentType.getFirst();

						model2.shared.getAgent4().setFirst(ntwst.getValue());
						}

						if (!agentType.getPrefix().isEmpty())
						{
							model2.shared.getAgent4().setPrefix((String)
									agentType.getPrefix().get(0));
						}

						if (!agentType.getMiddle().isEmpty())
						{
							NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
									agentType.getMiddle().get(0);

							model2.shared.getAgent4().setMiddle1(ntwst.getValue());

							if (agentType.getMiddle().size() > 1)
							{
							NonnullTokenWithSeparatorType ntwst2 = (NonnullTokenWithSeparatorType)
								agentType.getMiddle().get(1);
							model2.shared.getAgent4().setMiddle2(ntwst2.getValue());
							}
						}

						if (!agentType.getSuffix().isEmpty())
						{
							model2.shared.getAgent4().setSuffix
								(((NonnullTokenWithSeparatorType) agentType.getSuffix().
									get(0)).getValue());

							String preSepar = ((NonnullTokenWithSeparatorType)
									agentType.getSuffix().get(0)).getPreSeparator();

							if (preSepar != null)
								model2.shared.getAgent4().setPreSeparator(preSepar);
						}
					}
					k++;
				}
			}
			}

			if (infocard.getData() != null)
			if (infocard.getData().getSource() != null)
			if (infocard.getData().getSource().getEnclosingSource() != null)
			if (infocard.getData().getSource().getEnclosingSource().getAgent() != null)
			{
				option = 2;
				if (model2 == null)
				{
					model2 = turnToUniqueContentModel(model);
				}

				int k = 0;
				for (int i = 0; i < infocard.getData().getSource().
							getEnclosingSource().getAgent().size(); i++)
				{
					AgentType agentType = (AgentType) infocard.getData().
								getSource().getEnclosingSource().getAgent().get(i);

					if (agentType.getCorporateName() != null)
					{
						model2.shared.setEnclCorporateName((String) agentType.
											getCorporateName().getContent().get(0));

						model2.shared.setEnclRole(agentType.getRole());
					}
					else
					if (agentType.getRole().compareTo("card creator") != 0)
					{
					if (k == 0)
					{
						model2.shared.getAgent5().setRole(agentType.getRole());
						model2.shared.getAgent5().setSurname(((String)
								(((NonnullTokenWithSeparatorType) agentType.getSurname()).getValue())));

						if (agentType.getFirst() != null)
						{
						NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
								agentType.getFirst();

						model2.shared.getAgent5().setFirst(ntwst.getValue());
						}

						if (!agentType.getPrefix().isEmpty())
						{
							model2.shared.getAgent5().setPrefix((String)
									agentType.getPrefix().get(0));
						}

						if (!agentType.getMiddle().isEmpty())
						{
							NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
									agentType.getMiddle().get(0);

							model2.shared.getAgent5().setMiddle1(ntwst.getValue());

							if (agentType.getMiddle().size() > 1)
							{
							NonnullTokenWithSeparatorType ntwst2 = (NonnullTokenWithSeparatorType)
								agentType.getMiddle().get(1);
							model2.shared.getAgent5().setMiddle2(ntwst2.getValue());
							}
						}

						if (!agentType.getSuffix().isEmpty())
						{
							model2.shared.getAgent5().setSuffix
								(((NonnullTokenWithSeparatorType) agentType.getSuffix().
									get(0)).getValue());

							String preSepar = ((NonnullTokenWithSeparatorType)
									agentType.getSuffix().get(0)).getPreSeparator();

							if (preSepar != null)
								model2.shared.getAgent5().setPreSeparator(preSepar);
						}
					}

					if (k == 1)
					{
						model2.shared.getAgent6().setRole(agentType.getRole());
						model2.shared.getAgent6().setSurname(((String)
								(((NonnullTokenWithSeparatorType) agentType.getSurname()).getValue())));

						if (agentType.getFirst() != null)
						{
						NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
								agentType.getFirst();

						model2.shared.getAgent6().setFirst(ntwst.getValue());
						}

						if (!agentType.getPrefix().isEmpty())
						{
							model2.shared.getAgent6().setPrefix((String)
									agentType.getPrefix().get(0));
						}

						if (!agentType.getMiddle().isEmpty())
						{
							NonnullTokenWithSeparatorType ntwst = (NonnullTokenWithSeparatorType)
									agentType.getMiddle().get(0);

							model2.shared.getAgent6().setMiddle1(ntwst.getValue());

							if (agentType.getMiddle().size() > 1)
							{
							NonnullTokenWithSeparatorType ntwst2 = (NonnullTokenWithSeparatorType)
								agentType.getMiddle().get(1);
							model2.shared.getAgent6().setMiddle2(ntwst2.getValue());
							}
						}

						if (!agentType.getSuffix().isEmpty())
						{
							model2.shared.getAgent6().setSuffix
								(((NonnullTokenWithSeparatorType) agentType.getSuffix().
									get(0)).getValue());

							String preSepar = ((NonnullTokenWithSeparatorType)
									agentType.getSuffix().get(0)).getPreSeparator();

							if (preSepar != null)
								model2.shared.getAgent6().setPreSeparator(preSepar);
						}
					}
					k++;

					}

				}
			}

			if (infocard.getData() != null)
			if (infocard.getData().getSource() != null)
			if (infocard.getData().getSource().getEnclosingSource() != null)
			if (infocard.getData().getSource().getEnclosingSource().
													getContainer() != null)
			if (infocard.getData().getSource().getEnclosingSource().
					getContainer().getContainerNamePart().size() != 0)
			{
				if (model2 == null)
				{
					model2 = turnToUniqueContentModel(model);
					option = 2;
				}

				List containerNamePart = infocard.getData().getSource().
							getEnclosingSource().getContainer().getContainerNamePart();

					SimpleRichTextWithSeparatorType srtwst =
						(SimpleRichTextWithSeparatorType) containerNamePart.get(0);

					String part1 = (String) srtwst.getContent().get(0);
					model2.shared.setEnclCntNamePart1(part1);

					String preSepar = ((SimpleRichTextWithSeparatorType)
							containerNamePart.get(0)).getPreSeparator();

					if (preSepar != null)
						model2.shared.setEnclPreSepar(preSepar);

					if (containerNamePart.size() > 1)
					{
						SimpleRichTextWithSeparatorType srtwst2 =
							(SimpleRichTextWithSeparatorType) containerNamePart.get(1);

						String part2 = (String) srtwst2.getContent().get(0);
						model2.shared.setEnclCntNamePart2(part2);

						if (preSepar == null)
							preSepar = ((SimpleRichTextWithSeparatorType)
								containerNamePart.get(1)).getPreSeparator();

						if (preSepar != null)
							model2.shared.setEnclPreSepar(preSepar);
					}
			}

			if (infocard.getData() != null)
			if (infocard.getData().getSource() != null)
			if (infocard.getData().getSource().getEnclosingSource() != null)
			if (infocard.getData().getSource().getEnclosingSource().
					getComplexLocation() != null)
			{
				if (model2 == null)
				{
					model2 = turnToUniqueContentModel(model);
					option = 2;
				}

				ComplexPointType complexLoc = (ComplexPointType) infocard.
					getData().getSource().getEnclosingSource().getComplexLocation();

				PointType point1 = (PointType) complexLoc.getAxis().get(0);
				model2.shared.setSublocationPart1(point1.getValue());

				PointType point2 = (PointType) complexLoc.getAxis().get(1);
				model2.shared.setSublocationPart2(point2.getValue());
			}

			if (infocard.getData() != null)
			if (infocard.getData().getSource() != null)
			if (infocard.getData().getSource().getEnclosingSource() != null)
			if (infocard.getData().getSource().getEnclosingSource().
					getLocation() != null)
			{
				if (model2 == null)
				{
					model2 = turnToUniqueContentModel(model);
					option = 2;
				}

				PointType point = (PointType) infocard.getData().getSource()
					.getEnclosingSource().getLocation();

				if (point.getUnit().compareTo("volume") == 0)
					model2.shared.setSublocationPart1(point.getValue());
				if (point.getUnit().compareTo("issue") == 0)
					model2.shared.setSublocationPart2(point.getValue());
			}

			if (option == 1)
			{
				infomlFile.addInfocard(infocard, model);
				infomlFile.hasChanged = false;

				String cardId = (String) infocard.getCardId();
				model.setCardId(cardId);
			}
			else
			if (option == 2)
			{
				String cardId = (String) infocard.getCardId();
				model2.setCardId(cardId);

				model2.shared.setNickname((String)
						MainFrame.nicknameKeeper.nickToId.get(cardId));

				if ((! unrepetableNicknames.contains(model2.shared.getNickname()))
						&&
		  			(model2.shared.getNickname() != null))
				{
					mainFr.sharedContent.put(model2.shared.getNickname(),
																model2.shared);
					mainFr.quickContentCardAction.setEnabled(true);

					mainFr.nicknames.add(model2.shared.getNickname());
					unrepetableNicknames.addElement(
							  model2.shared.getNickname());
				}

				infomlFile.addInfocard(infocard, model2);
				infomlFile.hasChanged = false;
			}

			return infocard;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}
}



