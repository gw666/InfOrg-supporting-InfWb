package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.ListIterator;
import java.util.Vector;

import java.io.*;
/**
 *  Represents a notecard infocard. Includes getter and setter methods for all of the object's
 *  instance variables.
*/
public class NotecardModel implements Serializable
{
protected String title;
/** Text used to create the content element. Successive paragraphs must be separated by an empty line.
 */
protected String contentString;
protected List contentList, notes1List, notes2List;
protected String notes1String, notes2String;
protected String selectorsString;
protected List selectorsList;

protected String first;
protected String surname;
protected String honorific;
protected String middle1, middle2;
protected String suffix;
protected String punctuation;
protected String number;
protected String sessionNumber;
protected String email;
protected String sessionEmail;
protected String domain;
protected String sessionDomain;
protected String yahooId;
protected String sessionYahooId;
protected String time;
protected Calendar date;
protected String cardId;
protected GregorianCalendar current;

String substitute;

static long oldTime;

protected String type = "notecard";
final static long baseM;
static
{
	Calendar base = Calendar.getInstance();
	base.set(2008, Calendar.JULY, 1, 0, 0, 0);
	baseM = base.getTimeInMillis();
	oldTime = baseM;
}

	public NotecardModel()
	{
     	date = Calendar.getInstance();

		title = new String("No title");
		substitute = new String("No title");
		contentString = new String("");
		notes1String = new String("");
		notes2String = new String("");
		selectorsList = new ArrayList();
		selectorsString = new String();
		contentList = new ArrayList();
		notes1List = new ArrayList();
		notes2List = new ArrayList();

		honorific = new String("");
		first = new String("");
		middle1 = new String("");
		middle2 = new String("");
		surname = new String("");
		punctuation = new String("");
		suffix = new String("");
		sessionNumber = new String("baseM");
		email = new String("");
		sessionEmail = new String("");
		domain = new String("");
		sessionDomain = new String("");
		yahooId = new String("");
		sessionYahooId = new String("");
	}

	public void setTime()
	{
		date = Calendar.getInstance();

		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH) + 1;
		int day = date.get(Calendar.DATE);

		if ((month <= 9) && (day <= 9))
			time = new String(new Integer(year).toString() + "-0" +
				new Integer(month).toString() + "-0" + new Integer(day).toString());
		if ((month <= 9) && (day > 9))
			time = new String(new Integer(year).toString() + "-0" +
					new Integer(month).toString() + "-" + new Integer(day).toString());
		if ((month > 9) && (day <= 9))
			time = new String(new Integer(year).toString() + "-" +
					new Integer(month).toString() + "-0" + new Integer(day).toString());
		if ((month > 9) && (day > 9))
			time = new String(new Integer(year).toString() + "-" +
					new Integer(month).toString() + "-" + new Integer(day).toString());
	}

	/**
	 * Creates unique string used to create current infocard's cardId;
	 * result is placed in the field named "number"
	 * 
	 * @param apelant
	 */
	public void increaseSequenceNumber(String apelant)
	{
		/*
		current = Calendar.getInstance();
		long currentM = current.getTimeInMillis();
		oldTime = currentM;

		while (oldTime == currentM)
		{
			current = Calendar.getInstance();
			currentM = current.getTimeInMillis();
		}

		setNumber((new Long(currentM-baseM)).toString());
		*/
		current = new GregorianCalendar();
		
		String timeID;
		timeID = String.format("%1$ty%1$tm%1$td%1$tH%1$tM%1$tS%1$tL", current);
		setNumber(timeID);
		
		
		/*
		 * wait until millisecond value changes before exiting; this prevents
		 * two infocards from being given the same cardID
		 */
		long currentM = current.getTimeInMillis();
		oldTime = currentM;
		while (oldTime == currentM)
		{
			current = new GregorianCalendar();
			currentM = current.getTimeInMillis();
		}
		
		current = null;
	}

	public boolean setupInf()
	{
		boolean inf = false;

		if (((honorific.compareTo("") != 0) || (first.compareTo("") != 0) ||
			 (middle1.compareTo("") != 0) || (middle2.compareTo("") != 0) ||
			 (surname.compareTo("") != 0) || (punctuation.compareTo("") != 0) ||
			 (suffix.compareTo("") != 0)) &&
		    ((email.compareTo("") != 0) || (domain.compareTo("") != 0) ||
		     (yahooId.compareTo("") != 0)))
		  inf = true;

		return inf;
	}

	/*	This method creates the notes1String from the notes1List;
	 *  it is needed when editing an infocard after the infocard
	 *  has been unmarshalled (the notes1String is "" but the notes1List
	 *  has been completed from the xml)
	 */
	public void setNotes1StringFromList(List notes1List)
	{
		StringBuffer notes1 = new StringBuffer();

		for (ListIterator i = notes1List.listIterator(); i.hasNext();)
		{
			notes1.append(i.next());
			notes1.append("\n\n");
		}

		notes1String = new String(notes1);
	}

	public void setNotes2StringFromList(List notes2List)
	{
		StringBuffer notes2 = new StringBuffer();

		for (ListIterator i = notes2List.listIterator(); i.hasNext();)
		{
			notes2.append(i.next());
			notes2.append("\n\n");
		}

		notes2String = new String(notes2);
	}

	public void setNotes1List(String notes1String)
	{
		if ((notes1String.compareTo("") == 0) ||
					(notes1String.compareTo("\n\n") == 0))
		{
			notes1List.clear();
			return;
		}

		Vector tokens = new Vector();

		StringTokenizer tokenizer = new StringTokenizer(notes1String, "\n", true);

		while (tokenizer.hasMoreTokens())
		{
			String elem = tokenizer.nextToken();
			tokens.add(elem);
		}

		while (((String) tokens.elementAt(0)).compareTo("\n") == 0)
			tokens.remove(0);

		while (((String) tokens.elementAt(tokens.size()-1)).compareTo("\n") == 0)
			tokens.remove(tokens.size()-1);

		int ind = 0;
		while (ind < tokens.size()-1)
		{
			if ((((String) tokens.elementAt(ind)).compareTo("\n") == 0) &&
			   (((String) tokens.elementAt(ind+1)).compareTo("\n") == 0))
			{
				ind = ind + 2;
				while (((String) tokens.elementAt(ind)).compareTo("\n") == 0)
					tokens.remove(ind);
			}
			ind++;
		}

		StringBuffer buffer = new StringBuffer();
		String previous = null;
		for (int i = 0; i < tokens.size(); i++)
		{
			String current = (String) tokens.elementAt(i);

			if ((previous != null) && (previous.compareTo("\n") == 0)
							       && (current.compareTo("\n") == 0))
			{
				String elem = new String(buffer);

				if (elem.compareTo("\n") != 0)
					elem = removeWhiteSpace(elem);
				elem = removeWhiteSpace(elem);
				if (elem.compareTo("") != 0)
					notes1List.add(elem);

				buffer.delete(0, buffer.length());
			}

			if (previous != null)
			if (!((previous.compareTo("\n") == 0) && (current.compareTo("\n") == 0)))
				buffer.append(current);
			if (previous == null)
				buffer.append(current);

			previous = new String(current);
		}

		String elem = new String(buffer);

		if (elem.compareTo("\n") != 0)
			elem = removeWhiteSpace(elem);
		if (elem.compareTo("") != 0)
			notes1List.add(elem);
	}

	public void setNotes2List(String notes2String)
	{
		if ((notes2String.compareTo("") == 0) ||
					(notes2String.compareTo("\n\n") == 0))
		{
			notes2List.clear();
			return;
		}

		Vector tokens = new Vector();

		StringTokenizer tokenizer = new StringTokenizer(notes2String, "\n", true);

		while (tokenizer.hasMoreTokens())
		{
			String elem = tokenizer.nextToken();
			tokens.add(elem);
		}

		while (((String) tokens.elementAt(0)).compareTo("\n") == 0)
			tokens.remove(0);

		while (((String) tokens.elementAt(tokens.size()-1)).compareTo("\n") == 0)
			tokens.remove(tokens.size()-1);

		int ind = 0;
		while (ind < tokens.size()-1)
		{
			if ((((String) tokens.elementAt(ind)).compareTo("\n") == 0) &&
			   (((String) tokens.elementAt(ind+1)).compareTo("\n") == 0))
			{
				ind = ind + 2;
				while (((String) tokens.elementAt(ind)).compareTo("\n") == 0)
					tokens.remove(ind);
			}
			ind++;
		}

		StringBuffer buffer = new StringBuffer();
		String previous = null;
		for (int i = 0; i < tokens.size(); i++)
		{
			String current = (String) tokens.elementAt(i);

			if ((previous != null) && (previous.compareTo("\n") == 0)
							       && (current.compareTo("\n") == 0))
			{
				String elem = new String(buffer);

				if (elem.compareTo("\n") != 0)
					elem = removeWhiteSpace(elem);
				elem = removeWhiteSpace(elem);
				if (elem.compareTo("") != 0)
					notes2List.add(elem);

				buffer.delete(0, buffer.length());
			}

			if (previous != null)
			if (!((previous.compareTo("\n") == 0) && (current.compareTo("\n") == 0)))
				buffer.append(current);
			if (previous == null)
				buffer.append(current);

			previous = new String(current);
		}

		String elem = new String(buffer);

		if (elem.compareTo("\n") != 0)
			elem = removeWhiteSpace(elem);
		if (elem.compareTo("") != 0)
			notes2List.add(elem);
	}

	public String getContentString()
	{
		return contentString;
	}

	public void setContentString(String contentString)
	{
		this.contentString = contentString;
	}

	public String removeWhiteSpace(String source)
	{
		//	removes leading and trailing white space
		while ((source.startsWith(" ")) || (source.startsWith("\n"))
			|| (source.startsWith("\t")))
			source = source.substring(1, source.length());

		while ((source.endsWith(" ")) || (source.endsWith("\n"))
		    || (source.endsWith("\t")))
			source = source.substring(0, source.length()-1);

		while (source.contains("  "))
			source = source.replaceAll("  ", " ");

		return source;
	}

	/*	This method creates the contentString from the contentList;
	 *  it is needed when editing an infocard after the infocard
	 *  has been unmarshalled (the contentString is "" but the contentList
	 *  has been completed from the xml)
	 */
	public void setContentStringFromList(List contentList)
	{
		StringBuffer content = new StringBuffer();

		for (ListIterator i = contentList.listIterator(); i.hasNext();)
		{
			String primer = (String) i.next();
			content.append(removeWhiteSpace(primer));
			content.append("\n\n");
		}

		contentString = new String(content);
	}

	public void setContentList(String contentString)
	{
		if (contentString.compareTo("") == 0)
		{
			contentList.clear();
			return;
		}

		Vector tokens = new Vector();

		StringTokenizer tokenizer = new StringTokenizer(contentString, "\n", true);

		while (tokenizer.hasMoreTokens())
		{
			String elem = tokenizer.nextToken();
			tokens.add(elem);
		}

		while (((String) tokens.elementAt(0)).compareTo("\n") == 0)
			tokens.remove(0);

		while (((String) tokens.elementAt(tokens.size()-1)).compareTo("\n") == 0)
			tokens.remove(tokens.size()-1);

		int ind = 0;
		while (ind < tokens.size()-1)
		{
			if ((((String) tokens.elementAt(ind)).compareTo("\n") == 0) &&
			   (((String) tokens.elementAt(ind+1)).compareTo("\n") == 0))
			{
				ind = ind + 2;
				while (((String) tokens.elementAt(ind)).compareTo("\n") == 0)
					tokens.remove(ind);
			}
			ind++;
		}

		StringBuffer buffer = new StringBuffer();
		String previous = null;
		for (int i = 0; i < tokens.size(); i++)
		{
			String current = (String) tokens.elementAt(i);

			if ((previous != null) && (previous.compareTo("\n") == 0)
							       && (current.compareTo("\n") == 0))
			{
				String elem = new String(buffer);

				if (elem.compareTo("\n") != 0)
					elem = removeWhiteSpace(elem);
				elem = removeWhiteSpace(elem);
				if (elem.compareTo("") != 0)
					contentList.add(elem);

				buffer.delete(0, buffer.length());
			}

			if (previous != null)
			if (!((previous.compareTo("\n") == 0) && (current.compareTo("\n") == 0)))
				buffer.append(current);
			if (previous == null)
				buffer.append(current);

			previous = new String(current);
		}

		String elem = new String(buffer);

		if (elem.compareTo("\n") != 0)
			elem = removeWhiteSpace(elem);
		if (elem.compareTo("") != 0)
			contentList.add(elem);
	}

	public List getContentList()
	{
		return contentList;
	}

	public String getNotes1String()
	{
		return notes1String;
	}

	public List getNotes1List()
	{
		return notes1List;
	}

	public void setNotes1String(String notes1String)
	{
		notes1String = notes1String.trim();

		this.notes1String = notes1String;
	}

	public List getNotes2List()
	{
		return notes2List;
	}

	public String getNotes2String()
	{
		return notes2String;
	}

	public void setNotes2String(String notes2)
	{
		//notes2 = removeWhiteSpace(notes2);
		notes2 = notes2.trim();

		this.notes2String = notes2;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		title = removeWhiteSpace(title);

		this.title = title;
	}

	public List getSelectorsList()
	{
		return selectorsList;
	}

	/*	This method creates the selectorsString from the selectorsList;
	 *  it is needed when editing an infocard after the infocard
	 *  has been unmarshalled (the selectorsString is "" but the selectorsList
	 *  has been completed from the xml)
	 */
	public void setSelectorsStringFromList(List selectorsList)
	{
		StringBuffer selectors = new StringBuffer();

		for (ListIterator i = selectorsList.listIterator(); i.hasNext();)
		{
			selectors.append(i.next());
			selectors.append(";");
		}

		selectorsString = new String(selectors);
	}

	public void setSelectorsList(String selectorsString)
	{
		if (selectorsString.compareTo("") == 0)
		{
			selectorsList.clear();
			return;
		}

		Vector sel = new Vector();
		StringTokenizer tokenizer = new StringTokenizer(selectorsString, ";\n");

		while (tokenizer.hasMoreTokens())
		{
			String elem = tokenizer.nextToken();
			sel.add(elem);
		}

		for (int ind = 0; ind < sel.size(); ind++)
		{
			boolean newToken = true;
			String elem = (String) sel.elementAt(ind);

			for (ListIterator i = selectorsList.listIterator(); i.hasNext();)
			{
				String current = (String) i.next();
				if (current.compareTo(elem) == 0)
					newToken = false;
			}

			if (newToken)
			{
				elem = removeWhiteSpace(elem);

				if (elem.compareTo("") != 0)
					selectorsList.add(elem);
			}
		}
	}

	public void setSelectorsString(String selectorsString)
	{
		this.selectorsString = selectorsString;
	}

	public String getSelectorsString()
	{
		return selectorsString;
	}

	public String getFirst()
	{
		return first;
	}

	public void setFirst(String first)
	{
		this.first = first;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public Calendar getDate()
	{
		return date;
	}

	public void setDate(Calendar date)
	{
		this.date = date;
	}

	public String getMiddle1() {
		return middle1;
	}

	public void setMiddle1(String middle1) {
		this.middle1 = middle1;
	}

	public String getMiddle2() {
		return middle2;
	}

	public void setMiddle2(String middle2) {
		this.middle2 = middle2;
	}

	public String getHonorific() {
		return honorific;
	}

	public void setHonorific(String honorific) {
		this.honorific = honorific;
	}

	public String getPunctuation() {
		return punctuation;
	}

	public void setPunctuation(String punctuation) {
		this.punctuation = punctuation;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email)
	{
		if (email.contains("@"))
		{
			email = email.replace("@", ".");
		}
		this.email = email;
	}

	public String getYahooId() {
		return yahooId;
	}

	public void setYahooId(String yahooId) {
		this.yahooId = yahooId;
	}

	public String getSessionDomain() {
		return sessionDomain;
	}

	public void setSessionDomain(String sessionDomain) {
		this.sessionDomain = sessionDomain;
	}

	public String getSessionEmail() {
		return sessionEmail;
	}

	public void setSessionEmail(String sessionEmail) {
		this.sessionEmail = sessionEmail;
	}

	public String getSessionNumber() {
		return sessionNumber;
	}

	public void setSessionNumber(String sessionNumber) {
		this.sessionNumber = sessionNumber;
	}

	public String getSessionYahooId() {
		return sessionYahooId;
	}

	public void setSessionYahooId(String sessionYahooId) {
		this.sessionYahooId = sessionYahooId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

}
