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
import java.util.*;

import javax.swing.tree.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

import org.infoml.jaxb.*;
import org.infoml.jaxb.impl.*;


/**
 * Sets up UI and all data structures associated with
 * Infocard Organizer.
 */
public class Main
{
MainFrame mainFr;

JAXBContext jc;
private Marshaller m;
static ObjectFactory objFactory;
Validator v;

public InfomlFileType if1;
private List infomlList;
public InfomlType infocard;

// will be replaced by a Map
private Vector infocards;

public boolean hasChanged = false;

int outlineInfocardsNr = 0;

	public Main(MainFrame mainFr)
	{
		this.mainFr = mainFr;

		createContext();
		infocards = new Vector();
	}

	/**
	 * Sets out JAXB conversion and marshaling, InfocardBuilder, and an empty
	 * infocard file
	 */
	public void createContext()
	{
		try
		{
			jc = JAXBContext.newInstance("org.infoml.jaxb");
			m = jc.createMarshaller();
			m.setProperty(m.JAXB_SCHEMA_LOCATION,
			"http://infoml.org/infomlFile     http://infoml.org/s/infomlFile.xsd");
			m.setProperty(m.JAXB_FORMATTED_OUTPUT, new Boolean(true));

			objFactory = new ObjectFactory();
			InfocardBuilder.objFactory = objFactory;

			//	create empty data structure corresponding to infomlFile element
			if1 = objFactory.createInfomlFile();
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}

	/** Adds the infocard both to the InfocardWindow
	 *  and to MainFrame's infocards map.
	 */
	public void addInfocard(InfomlType infocard, NotecardModel model)
	{
		try
		{
			if (model != null)
			{
				if1.getInfoml().add(infocard);
				mainFr.infocards.put(model, infocard);
			}
			else
			{
				if1.getInfoml().add(outlineInfocardsNr++, infocard);
     		}

			infocards.add(infocard);

			mainFr.setClearEnabled(true);
		}
		catch (Exception e)
		{ e.printStackTrace(); }

		//System.out.println("main - addInfocard");
		//hasChanged = true;
	}

	public void removeOutlineInfocards()
	{
		rearangeElements();

		outlineInfocardsNr = 0;
	}

	//public void addInfocardAtIndex(InfomlType infocard)

	/* In case of edit don't need to create a new infocard;
	 * an already existing infocard is modified according to the edit.
	 */
	public InfomlType findInfocard(NotecardModel model)
	{
		InfomlType infocard = null;

		infocard = (InfomlType) mainFr.infocards.get(model);

		return infocard;
	}

	public void rearangeElements()
	{
		if1.getInfoml().clear();

		DragAndDropTree tree = (DragAndDropTree) mainFr.infoWindow.tree;

		Enumeration e = ((DefaultMutableTreeNode) tree.root).preorderEnumeration();
		// the root is not mapped to an InfomlType element so we skip the root
		e.nextElement();

		while (e.hasMoreElements())
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();

			NotecardModel model = null;

			model = (NotecardModel) mainFr.notecards.get(node);
			if (model == null)
				model = (NotecardModel) mainFr.uniqueContent.get(node);

			InfomlType infocard = (InfomlType) mainFr.infocards.get(model);

			if1.getInfoml().add(infocard);
		}
	}

	void checkInfomlList()
	{
		boolean newInf;

		Vector infocards = new Vector();
		Vector indexesToBeRemoved = new Vector();

		boolean duplicate = true;

		while (duplicate)
		{
			duplicate = false;
			infocards = new Vector();

		for (int i = outlineInfocardsNr; i < if1.getInfoml().size(); i++)
		{
			InfomlType infocard = (InfomlType) if1.getInfoml().get(i);
			newInf = true;

			for (int j = 0; j < infocards.size(); j++)
			 if (infocard.getCardId().compareTo((String) infocards.elementAt(j)) == 0)
				 newInf = false;

			if (newInf)
			{
				infocards.addElement(infocard.getCardId());
			}
			else
			{
				//indexesToBeRemoved.addElement(new Integer(i));

				if1.getInfoml().remove(infocard);
				duplicate = true;
				break;
			}
		}
		}

		/*for (int i = 0; i < indexesToBeRemoved.size(); i++)
			if1.getInfoml().remove(((Integer)
					indexesToBeRemoved.elementAt(i)).intValue());*/
	}

	public int writeInfomlToDiskWithValidating(String path)
	{
		checkInfomlList();

		try
		{
			if (v == null)  v = jc.createValidator();
			boolean valid = v.validateRoot(if1);

			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			m.marshal(if1, new FileOutputStream(path));
			return 1;
		}
		catch (Exception e)
		{ e.printStackTrace(); return 0;}
	}

}





