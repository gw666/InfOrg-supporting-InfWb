package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

import javax.swing.tree.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

import org.infoml.jaxb.*;
import org.infoml.jaxb.impl.*;


/**
 * @author gw
 *
 */
public class InfocardBuilder
{
	NotecardModel model;
	UniqueContentModel model2;
	private PropertiesType properties;
	private ContentAgentContainerLocationType data;
	private RichTextWithExactType cont;
	private AgentContainerLocationType source;
	AgentType agentType;
	private ContainerType container;
	private EnclosingSourceType enclContainer;
	private SelectorsType selectors;
	InfomlType infocard;
	Main infomlFile;
	
	private static String infomlVersion = "1.00";

	static ObjectFactory objFactory;

	String cardId;
	Map ids;

	public InfocardBuilder(Main infomlFile, NotecardModel model)
	{
		try
		{
			this.model = model;

			this.infomlFile = infomlFile;

			objFactory = infomlFile.objFactory;
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setModel(NotecardModel model)
	{
		this.model = model;
	}

	// create an infocard and add it to the infoml list
	public void setStandardInfocard()
	{
		try
		{
			infocard = objFactory.createInfomlType();

			infomlFile.addInfocard(infocard, model);

			infocard.setVersion(new java.math.BigDecimal(infomlVersion));
			infocard.setEncoding(new String("UTF-8"));
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	// in case of edit
	public void modifyInfocard(InfomlType infocard)
	{
		try
		{
			this.infocard = infocard;

			infocard.setVersion(new java.math.BigDecimal(infomlVersion));
			infocard.setEncoding(new String("UTF-8"));
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}


	public static void buildOutlineInfocards(Vector nodes, Vector cardIds,
										Vector preOrdCardIds, Main infomlFile)
	{
	  Map ids = new HashMap();
	  try
	  {
		  //System.out.println("\n\n");

		for (int index = 0; index < nodes.size(); index++)
		{
			DefaultMutableTreeNode currentNode =
				(DefaultMutableTreeNode) nodes.elementAt(index);
			//System.out.println(currentNode.getUserObject());

			if (currentNode.getChildCount() != 0)
			{
				InfomlType infocard = objFactory.createInfomlType();
				infomlFile.addInfocard(infocard, null);

				infocard.setVersion(new java.math.BigDecimal(infomlVersion));
				infocard.setEncoding(new String("UTF-8"));

				if (ids.get(currentNode) != null)
				{
					infocard.setCardId((String) ids.get(currentNode));
				}
				else
				if (MainFrame.setupModel.setupInf())
				{
					String prefix = null;

					if (MainFrame.setupModel.getEmail().compareTo("") != 0)
						prefix = new String(MainFrame.setupModel.getEmail());
					else
						if (MainFrame.setupModel.getDomain().compareTo("") != 0)
							prefix = new String(MainFrame.setupModel.getDomain());
						else
						if (MainFrame.setupModel.getYahooId().compareTo("") != 0)
							prefix = new String(MainFrame.setupModel.getYahooId());

					String cardId = new String(prefix + "_" + MainFrame.setupModel.getNumber());

					infocard.setCardId(cardId);

					ids.put(currentNode, cardId);

					MainFrame.setupModel.increaseSequenceNumber("buildOutlineInfocards");
					// serialize the new sequence number
					MainFrame.serializator.run();
				}

				ContentAgentContainerLocationType data =
					objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);

				ExtendedPointersType pointer = objFactory.createExtendedPointersType();
				data.setPointers(pointer);

				// if not root set parent
				if (index != 0)
				{
					RichPointerType rpt = objFactory.createRichPointerType();
					rpt.setTargetId((String) cardIds.elementAt(index-1));

					pointer.setParentPtr(rpt);
				}

				for (int i = 0; i < currentNode.getChildCount(); i++)
				{
					DefaultMutableTreeNode child =
						(DefaultMutableTreeNode) currentNode.getChildAt(i);

					if (child.getChildCount() != 0)
					{
						String localCardId = null;

						if (ids.get(child) != null)
						{
							localCardId  = (String) ids.get(child);
						}
						if (MainFrame.setupModel.setupInf())
						{
							String prefix = null;

							if (MainFrame.setupModel.getEmail().compareTo("") != 0)
								prefix = new String(MainFrame.setupModel.getEmail());
							else
								if (MainFrame.setupModel.getDomain().compareTo("") != 0)
									prefix = new String(MainFrame.setupModel.getDomain());
								else
								if (MainFrame.setupModel.getYahooId().compareTo("") != 0)
									prefix = new String(MainFrame.setupModel.getYahooId());

							localCardId = new String(
									prefix + "_" + MainFrame.setupModel.getNumber());

							ids.put(child, localCardId);

							MainFrame.setupModel.increaseSequenceNumber("buildOutlineInfocards");
							// serialize the new sequence number
							MainFrame.serializator.run();
						}

						RichPointerType rpt2 = objFactory.createRichPointerType();
						rpt2.setTargetId(localCardId);
						//System.out.println(localCardId);

						pointer.getPtr().add(rpt2);
					}
					else
					{
						Enumeration preOrder = ((DefaultMutableTreeNode)
								child.getRoot()).preorderEnumeration();

						// infoml file root element
						preOrder.nextElement();

						int x = 0;
						while (preOrder.hasMoreElements())
						{
							if (! ((DefaultMutableTreeNode)
									preOrder.nextElement()).equals(child))
								x++;
							else
								break;
						}

						RichPointerType rpt2 = objFactory.createRichPointerType();
						rpt2.setTargetId((String) preOrdCardIds.elementAt(x));
						//System.out.println(x + "   " + preOrdCardIds.elementAt(x));

						pointer.getPtr().add(rpt2);
					}
				}
			}
		}
	  }
	  catch (Exception e)
	  { e.printStackTrace(); }
	}


	/**
	 * This may be old code no longer in use (unsure)
	 */
	public void generateCardId()
	{
		if (MainFrame.setupModel.setupInf())
		{
			String prefix = null;

			if (MainFrame.setupModel.getEmail().compareTo("") != 0)
				prefix = new String(MainFrame.setupModel.getEmail());
			else
			if (MainFrame.setupModel.getDomain().compareTo("") != 0)
				prefix = new String(MainFrame.setupModel.getDomain());
			else
			if (MainFrame.setupModel.getYahooId().compareTo("") != 0)
				prefix = new String(MainFrame.setupModel.getYahooId());

			MainFrame.setupModel.increaseSequenceNumber("generateCardId");
			cardId = new String(prefix + "_" + MainFrame.setupModel.getNumber());

			model.setCardId(cardId);
			if (model2 != null)
				model2.setCardId(cardId);
		}
	}

	/**
	 * This may be old code no longer in use (unsure)
	 */
	public void setCardId()
	{
		infocard.setCardId(cardId);
	}

	public void setCreator(NotecardModel setupModel)
	{
	  if (setupModel.setupInf())
		try
		{
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}

			if (source == null)
			{
				source = objFactory.createAgentContainerLocationType();
				data.setSource(source);
			}

			AgentType agent = objFactory.createAgentType();

			if (setupModel.getSurname().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType surname =
					objFactory.createNonnullTokenWithSeparatorType();
				surname.setValue(setupModel.getSurname());
				agent.setSurname(surname);
			}

			if (setupModel.getHonorific().compareTo("") != 0)
			{
				agent.getPrefix().add(setupModel.getHonorific());
			}

			if (setupModel.getMiddle1().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType middle1 =
					objFactory.createNonnullTokenWithSeparatorType();
				middle1.setValue(setupModel.getMiddle1());

				agent.getMiddle().add(middle1);
			}

			if (setupModel.getMiddle2().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType middle2 =
					objFactory.createNonnullTokenWithSeparatorType();
				middle2.setValue(setupModel.getMiddle2());

				agent.getMiddle().add(middle2);
			}

			if (setupModel.getFirst().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType first =
					objFactory.createNonnullTokenWithSeparatorType();
				first.setValue(setupModel.getFirst());

				agent.setFirst(first);
			}

			if (setupModel.getSuffix().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType suffix =
					objFactory.createNonnullTokenWithSeparatorType();
				suffix.setValue(setupModel.getSuffix());

				if (setupModel.getPunctuation().compareTo("") != 0)
					suffix.setPreSeparator(setupModel.getPunctuation());

				agent.getSuffix().add(suffix);
			}

			agent.setRole("card creator");

			source.getAgent().add(agent);
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setTitle()
	{
	  if ((model.getTitle() != null) && (model.getTitle().compareTo("No title") != 0))
		try
		{
			/* GW 080225
			 * if (properties == null)
			{
				properties = objFactory.createPropertiesType();
				infocard.setProperties(properties);
			}

			SimpleRichTextType srt = objFactory.createSimpleRichTextType();
			srt.getContent().add(model.getTitle());
			properties.setTitle(srt);
			*/
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}

			SimpleRichTextType srt = objFactory.createSimpleRichTextType();
			srt.getContent().add(model.getTitle());
			data.setTitle(srt);
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setDateCreated() 
	{
	  if (model.getDate() != null)
		try
	  	{
			if (properties == null)
			{
				properties = objFactory.createPropertiesType();
				infocard.setProperties(properties);
			}

			model.setTime();
			properties.setDateCreated(model.getTime());
	  	}
	  	catch (Exception e)
	  	{ e.printStackTrace(); }
	}

	public void setContent()
	{
	  if ((model.getContentList() != null) && !(model.getContentList().isEmpty()))
		try
		{
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}

			if (cont == null)
			{
				cont = objFactory.createRichTextWithExactType();
				data.setContent(cont);
			}

			List l = model.getContentList();
			for (ListIterator i = l.listIterator(); i.hasNext();)
			{
				RichTextType.P paragraph = objFactory.createRichTextTypeP();

				// add String object
				paragraph.getContent().add(i.next());
				cont.getPOrQuotationOrPoem().add(paragraph);
			}
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setNotes1()
	{
	  if ((model.getNotes1List() != null) && !(model.getNotes1List().isEmpty()))
		try
		{
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}

			RichTextType rtt = objFactory.createRichTextType();

			List l = model.getNotes1List();
			for (ListIterator i = l.listIterator(); i.hasNext();)
			{
				RichTextType.P paragraph = objFactory.createRichTextTypeP();

				// add String object
				paragraph.getContent().add(i.next());
				rtt.getPOrQuotationOrPoem().add(paragraph);
			}

			data.setNotes1(rtt);
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setNotes2()
	{
	  if ((model.getNotes2List() != null) && (!model.getNotes2List().isEmpty()))
		try
		{
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}
			RichTextType rtt = objFactory.createRichTextType();

			List l = model.getNotes2List();
			for (ListIterator i = l.listIterator(); i.hasNext();)
			{
				RichTextType.P paragraph = objFactory.createRichTextTypeP();

				// add String object
				paragraph.getContent().add(i.next());
				rtt.getPOrQuotationOrPoem().add(paragraph);
			}

			data.setNotes2(rtt);
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setSelectors()
	{
	  if (model.getSelectorsList() != null) //&& !(model.getSelectorsList().isEmpty()))
		try
		{
		    if (selectors == null)
			{
			    selectors = objFactory.createSelectorsType();
			    infocard.setSelectors(selectors);
			}

		    if (model.getSelectorsList().isEmpty())
		    {
		    	selectors.getTag().clear();
		    	infocard.setSelectors(null);
		    	return;
		    }

			List l = model.getSelectorsList();
		    for (ListIterator i = l.listIterator(); i.hasNext();)
		    {
		    	selectors.getTag().add((String) i.next());
		    }
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setExactQuotation()
	{
		if (model2 == null)
			model2 = (UniqueContentModel) model;

		try
		{
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}
			if (cont != null)
			{
				//cont = objFactory.createRichTextWithExactType();
				//data.setContent(cont);
				cont.setExact(model2.isExact());
			}
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setPages()
	{
		if (model2 == null)
			model2 = (UniqueContentModel) model;

		if ((model2.getPage1().compareTo("") != 0) ||
						(model2.getPage2().compareTo("") != 0))
		try
		{
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}
			if (source == null)
			{
				source = objFactory.createAgentContainerLocationType();
				data.setSource(source);
			}

			LocationType location = objFactory.createLocationType();

			if ((model2.getPage1().compareTo("") != 0) &&
					(model2.getPage2().compareTo("") == 0))
			{
				PointType point = objFactory.createPointType();
				point.setUnit("page");
				point.setValue(model2.getPage1());

				location.setPoint(point);
				source.setLocation(location);
			}
			if ((model2.getPage1().compareTo("") != 0) &&
					(model2.getPage2().compareTo("") != 0))
			{
				RangeType range = objFactory.createRangeType();
				range.setBegin(model2.getPage1());
				range.setEnd(model2.getPage2());
				range.setUnit("page");

				location.setRange(range);
				source.setLocation(location);
			}
			// at least 1 point type
			/*if ((model2.getPage1().compareTo("") == 0) &&
					(model2.getPage2().compareTo("") != 0))
			{
				RangeType range = objFactory.createRangeType();
				range.setEnd(model2.getPage2());
				range.setUnit("page");
			}*/

			//source.setLocation(location);
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setContainerNamePart1()
	{
	  try
	  {
		if (model2 == null)
			model2 = (UniqueContentModel) model;

		if (model2.shared.getContainerNamePart1() != null)
		if (model2.shared.getContainerNamePart1().compareTo("") != 0)
		{
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}
			if (source == null)
			{
				source = objFactory.createAgentContainerLocationType();
				data.setSource(source);
			}
			if (container == null)
			{
				container = objFactory.createContainerType();
				source.setContainer(container);
			}

			SimpleRichTextWithSeparatorType srtwst =
						objFactory.createSimpleRichTextWithSeparatorType();

			srtwst.getContent().add(model2.shared.getContainerNamePart1());

			container.getContainerNamePart().add(srtwst);
		}
	  }
	  catch (Exception e)
	  { e.printStackTrace(); }
	}

	public void setContainerNamePart2()
	{
	  try
	  {
		if (model2 == null)
			model2 = (UniqueContentModel) model;

		if (model2.shared.getContainerNamePart2() != null)
		if (model2.shared.getContainerNamePart2().compareTo("") != 0)
		{
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}
			if (source == null)
			{
				source = objFactory.createAgentContainerLocationType();
				data.setSource(source);
			}
			if (container == null)
			{
				container = objFactory.createContainerType();
				source.setContainer(container);
			}

			SimpleRichTextWithSeparatorType srtwst =
						objFactory.createSimpleRichTextWithSeparatorType();

			if (model2.shared.getPreSepar().compareTo("") != 0)
				srtwst.setPreSeparator(model2.shared.getPreSepar());

			srtwst.getContent().add(model2.shared.getContainerNamePart2());

			container.getContainerNamePart().add(srtwst);
		}
	  }
	  catch (Exception e)
	  { e.printStackTrace(); }
	}

	public void setContainerDate()
	{
		if (model2 == null)
			model2 = (UniqueContentModel) model;

		try
		{
			if ((model2.shared.getDate3() != 0)
					&& (model2.shared.getDateRole().compareTo("") != 0))
			{
				if (data == null)
				{
					data = objFactory.createContentAgentContainerLocationType();
					infocard.setData(data);
				}
				if (source == null)
				{
					source = objFactory.createAgentContainerLocationType();
					data.setSource(source);
				}
				if (container == null)
				{
					container = objFactory.createContainerType();
					source.setContainer(container);
				}

				DateWithRoleType dwrt = objFactory.createDateWithRoleType();

				//month - date1; year - date3; day - date2
				if ((model2.shared.getDate1() == 0) && (model2.shared.getDate2() == 0))
					dwrt.setValue((new Integer(model2.shared.getDate3())).toString());

				if (model2.shared.getDate1() <= 9)
				if ((model2.shared.getDate1() != 0) && (model2.shared.getDate2() == 0))
				  dwrt.setValue((new Integer(model2.shared.getDate3())).toString() + "-0" +
						  		(new Integer(model2.shared.getDate1())).toString()
						  		);

				if (model2.shared.getDate1() > 9)
					if ((model2.shared.getDate1() != 0) && (model2.shared.getDate2() == 0))
					  dwrt.setValue((new Integer(model2.shared.getDate3())).toString() + "-" +
							  		(new Integer(model2.shared.getDate1())).toString()
							  		);

				if ((model2.shared.getDate2() <= 9) && (model2.shared.getDate1() <= 9))
				if ((model2.shared.getDate1() != 0) && (model2.shared.getDate2() != 0))
					dwrt.setValue((new Integer(model2.shared.getDate3())).toString() + "-0" +
					  			  (new Integer(model2.shared.getDate1())).toString() + "-0" +
					  			  (new Integer(model2.shared.getDate2())).toString()
					  			  );

				if ((model2.shared.getDate2() <= 9) && (model2.shared.getDate1() > 9))
					if ((model2.shared.getDate1() != 0) && (model2.shared.getDate2() != 0))
						dwrt.setValue((new Integer(model2.shared.getDate3())).toString() + "-" +
						  			  (new Integer(model2.shared.getDate1())).toString() + "-0" +
						  			  (new Integer(model2.shared.getDate2())).toString()
						  			  );

				if ((model2.shared.getDate2() > 9) && (model2.shared.getDate1() <= 9))
					if ((model2.shared.getDate1() != 0) && (model2.shared.getDate2() != 0))
						dwrt.setValue((new Integer(model2.shared.getDate3())).toString() + "-0" +
						  			  (new Integer(model2.shared.getDate1())).toString() + "-" +
						  			  (new Integer(model2.shared.getDate2())).toString()
						  			  );

				if ((model2.shared.getDate2() > 9) && (model2.shared.getDate1() > 9))
					if ((model2.shared.getDate1() != 0) && (model2.shared.getDate2() != 0))
						dwrt.setValue((new Integer(model2.shared.getDate3())).toString() + "-" +
						  			  (new Integer(model2.shared.getDate1())).toString() + "-" +
						  			  (new Integer(model2.shared.getDate2())).toString()
						  			  );

				dwrt.setRole(model2.shared.getDateRole());

				container.getDate().add(dwrt);
			}
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setAgentX(Agent agentX)
	{
		if (model2 == null)
			model2 = (UniqueContentModel) model;

		// role can't be "" or null
		if (agentX.getSurname().compareTo("") != 0)
		try
		{
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}
			if (source == null)
			{
				source = objFactory.createAgentContainerLocationType();
				data.setSource(source);
			}
			if (agentType == null)
			{
				agentType = objFactory.createAgentType();
				source.getAgent().add(agentType);
			}

			agentType.setRole(agentX.getRole());

			if (agentX.getFirst().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType ntwst =
									objFactory.createNonnullTokenWithSeparatorType();
				ntwst.setValue(agentX.getFirst());

				agentType.setFirst(ntwst);
			}

			if (agentX.getSurname().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType ntwst =
									objFactory.createNonnullTokenWithSeparatorType();
				ntwst.setValue(agentX.getSurname());

				agentType.setSurname(ntwst);
			}

			if (agentX.getMiddle1().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType ntwst =
									objFactory.createNonnullTokenWithSeparatorType();
				ntwst.setValue(agentX.getMiddle1());

				agentType.getMiddle().add(ntwst);
			}

			if (agentX.getMiddle2().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType ntwst =
									objFactory.createNonnullTokenWithSeparatorType();
				ntwst.setValue(agentX.getMiddle2());

				agentType.getMiddle().add(ntwst);
			}

			if (agentX.getPrefix().compareTo("") != 0)
			{
				agentType.getPrefix().add(agentX.getPrefix());
			}

			if (agentX.getSuffix().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType ntwst =
									objFactory.createNonnullTokenWithSeparatorType();
				ntwst.setValue(agentX.getSuffix());

				if (agentX.getPreSeparator().compareTo("") != 0)
					ntwst.setPreSeparator(agentX.getPreSeparator());

				agentType.getSuffix().add(ntwst);
			}
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setEnclosingContainerVolumeIssue()
	{
	  if (model2 == null)
		  model2 = (UniqueContentModel) model;

	  if ((model2.shared.getSublocationPart1().compareTo("") != 0) ||
	  			(model2.shared.getSublocationPart2().compareTo("") != 0))
	  try
	  {
		if (data == null)
		{
			data = objFactory.createContentAgentContainerLocationType();
			infocard.setData(data);
		}
		if (source == null)
		{
			source = objFactory.createAgentContainerLocationType();
			data.setSource(source);
		}
		if (enclContainer == null)
		{
			enclContainer = objFactory.createEnclosingSourceType();
			source.setEnclosingSource(enclContainer);
		}


		if ((model2.shared.getSublocationPart1().compareTo("") != 0) &&
				(model2.shared.getSublocationPart2().compareTo("") != 0))
		{
			ComplexPointType cpt = objFactory.createComplexPointType();

			PointType point1 = objFactory.createPointType();
			point1.setValue(model2.shared.getSublocationPart1());
			point1.setUnit("volume");
			cpt.getAxis().add(point1);

			PointType point2 = objFactory.createPointType();
			point2.setValue(model2.shared.getSublocationPart2());
			point2.setUnit("issue");
			cpt.getAxis().add(point2);

			enclContainer.setComplexLocation(cpt);
		}
		else
		{
		if (model2.shared.getSublocationPart1().compareTo("") != 0)
		{
			PointType point = objFactory.createPointType();
			LocationType location = objFactory.createLocationType();
			
			point.setUnit("volume");
			point.setValue(model2.shared.getSublocationPart1());
			location.setPoint(point);
			
			enclContainer.setLocation(location);
		}

		if (model2.shared.getSublocationPart2().compareTo("") != 0)
		{
			PointType point = objFactory.createPointType();
			LocationType location = objFactory.createLocationType();

			point.setUnit("issue");
			point.setValue(model2.shared.getSublocationPart2());
			location.setPoint(point);

			enclContainer.setLocation(location);
		}
		}
	  }
	  catch (Exception e)
	  { e.printStackTrace(); }
	}

	public void setEnclosingContainerTitle()
	{
	  if (model2 == null)
		  model2 = (UniqueContentModel) model;

	  if ((model2.shared.getEnclCntNamePart1().compareTo("") != 0) ||
				(model2.shared.getEnclCntNamePart2().compareTo("") != 0))
	  try
	  {
		if (data == null)
		{
			data = objFactory.createContentAgentContainerLocationType();
			infocard.setData(data);
		}
		if (source == null)
		{
			source = objFactory.createAgentContainerLocationType();
			data.setSource(source);
		}
		if (enclContainer == null)
		{
			enclContainer = objFactory.createEnclosingSourceType();
			source.setEnclosingSource(enclContainer);
		}

		ContainerType ct = objFactory.createContainerType();

		if ((model2.shared.getEnclCntNamePart1().compareTo("") != 0) ||
				(model2.shared.getEnclCntNamePart2().compareTo("") != 0))
		{
			SimpleRichTextWithSeparatorType srtwst =
							objFactory.createSimpleRichTextWithSeparatorType();
			SimpleRichTextWithSeparatorType srtwst2 = null;

			if (model2.shared.getEnclCntNamePart1().compareTo("") != 0)
			{
				srtwst.getContent().add(model2.shared.getEnclCntNamePart1());
				ct.getContainerNamePart().add(srtwst);
			}

			if (model2.shared.getEnclCntNamePart2().compareTo("") != 0)
			{
				srtwst2 =
					objFactory.createSimpleRichTextWithSeparatorType();

				srtwst2.getContent().add(model2.shared.getEnclCntNamePart2());;
			}

			if (model2.shared.getEnclPreSepar().compareTo("") != 0)
				srtwst2.setPreSeparator(model2.shared.getEnclPreSepar());

			if (srtwst2 != null)
				ct.getContainerNamePart().add(srtwst2);
			enclContainer.setContainer(ct);
		}

	  }
	  catch (Exception e)
	  { e.printStackTrace(); }
	}

	public void setEnclosingAgentX(Agent agentX)
	{
		if (model2 == null)
			model2 = (UniqueContentModel) model;

		// role can't be "" or null
		if (agentX.getSurname().compareTo("") != 0)
		try
		{
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}
			if (source == null)
			{
				source = objFactory.createAgentContainerLocationType();
				data.setSource(source);
			}
			if (enclContainer == null)
			{
				enclContainer = objFactory.createEnclosingSourceType();
				source.setEnclosingSource(enclContainer);
			}
			if (agentType == null)
			{
				agentType = objFactory.createAgentType();
				enclContainer.getAgent().add(agentType);
			}

			agentType.setRole(agentX.getRole());

			if (agentX.getFirst().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType ntwst =
									objFactory.createNonnullTokenWithSeparatorType();
				ntwst.setValue(agentX.getFirst());

				agentType.setFirst(ntwst);
			}

			if (agentX.getSurname().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType ntwst =
									objFactory.createNonnullTokenWithSeparatorType();
				ntwst.setValue(agentX.getSurname());

				agentType.setSurname(ntwst);
			}

			if (agentX.getMiddle1().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType ntwst =
									objFactory.createNonnullTokenWithSeparatorType();
				ntwst.setValue(agentX.getMiddle1());

				agentType.getMiddle().add(ntwst);
			}

			if (agentX.getMiddle2().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType ntwst =
									objFactory.createNonnullTokenWithSeparatorType();
				ntwst.setValue(agentX.getMiddle2());

				agentType.getMiddle().add(ntwst);
			}

			if (agentX.getPrefix().compareTo("") != 0)
			{
				agentType.getPrefix().add(agentX.getPrefix());
			}

			if (agentX.getSuffix().compareTo("") != 0)
			{
				NonnullTokenWithSeparatorType ntwst =
									objFactory.createNonnullTokenWithSeparatorType();
				ntwst.setValue(agentX.getSuffix());

				if (agentX.getPreSeparator().compareTo("") != 0)
					ntwst.setPreSeparator(agentX.getPreSeparator());

				agentType.getSuffix().add(ntwst);
			}
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	public void setCorporateAgent()
	{
		if (model2 == null)
			model2 = (UniqueContentModel) model;

		if (model2.shared.getEnclCorporateName().compareTo("") != 0)
		try
		{
			if (data == null)
			{
				data = objFactory.createContentAgentContainerLocationType();
				infocard.setData(data);
			}
			if (source == null)
			{
				source = objFactory.createAgentContainerLocationType();
				data.setSource(source);
			}
			if (enclContainer == null)
			{
				enclContainer = objFactory.createEnclosingSourceType();
				source.setEnclosingSource(enclContainer);
			}
			if (agentType == null)
			{
				agentType = objFactory.createAgentType();
				enclContainer.getAgent().add(agentType);
			}

			agentType.setRole(model2.shared.getEnclRole());


			SimpleRichTextType srtt = objFactory.createSimpleRichTextType();
			srtt.getContent().add(model2.shared.getEnclCorporateName());

			agentType.setCorporateName(srtt);
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

}




