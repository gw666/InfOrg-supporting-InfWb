package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import java.util.Vector;
import java.util.Calendar;
import java.io.*;
import java.text.*;


public class SharedContentModel implements Serializable
{
	private String nickname;
	private String containerNamePart1, preSepar, containerNamePart2;
	private String dateRole;
	private int date1 = 0, date2 = 0, date3 = 0;
	private String sublocationPart1, sublocationPart2;
	private String enclCntNamePart1, enclPreSepar, enclCntNamePart2;
	private String enclRole, enclCorporateName;
	private Vector agents;
	private Agent agent1, agent2, agent3, agent4, agent5, agent6;
	private Calendar date;
	private SimpleDateFormat dateFormat;

	public SharedContentModel()
	{
		agent1 = new Agent();
		agent2 = new Agent();
		agent3 = new Agent();
		agent4 = new Agent();
		agent5 = new Agent();
		agent6 = new Agent();

		agents = new Vector();

		nickname = new String("");
		containerNamePart1 = new String("");
		preSepar = new String("");
		containerNamePart2 = new String("");
		dateRole = new String("");
		sublocationPart1 = new String("");
		sublocationPart2 = new String("");
		enclCntNamePart1 = new String("");
		enclPreSepar = new String("");
		enclCntNamePart2 = new String("");
		enclRole = new String();
		enclCorporateName = new String("");

		date = Calendar.getInstance();
		date.clear();
	}

	public void formatDate()
	{
		date.set(Calendar.MONTH, date1);
		date.set(Calendar.DATE, date2);
		date.set(Calendar.YEAR, date3);
	}

	public void addAgent(Agent agent)
	{
		agents.add(agent);
	}

	public Vector getAgents()
	{
		return agents;
	}

	public boolean isValid()
	{
		boolean valid = false;

		if ((containerNamePart1.compareTo("") != 0) || (preSepar.compareTo("") != 0)
		 || (containerNamePart2.compareTo("") != 0) || (date1 != 0) || (date2 != 0)
		 || (date3 != 0)
		 || (sublocationPart1.compareTo("") != 0) || (sublocationPart2.compareTo("") != 0)
		 || (enclCntNamePart1.compareTo("") != 0) || (enclPreSepar.compareTo("") != 0)
		 || (enclCntNamePart2.compareTo("") != 0) || (enclCorporateName.compareTo("") != 0)
		 || (!agent1.isEmpty()) || (!agent2.isEmpty()) || (!agent3.isEmpty())
		 || (!agent4.isEmpty()) || (!agent5.isEmpty()) || (!agent6.isEmpty()))
			valid = true;

		return valid;
	}

	public boolean dateValid()
	{
		boolean valid = false;

		//	month - date1; year - date3; day - date2

		if (dateRole.compareTo("") != 0)
		{
			if (((date3 != 0) && (date2 == 0) && (date1 == 0)) ||
				((date3 != 0) && (date1 != 0) && (date2 == 0)) ||
				((date3 != 0) && (date1 != 0) && (date2 != 0)))
				valid = true;
		}
		else
			valid = true;

		return valid;
	}

	public boolean titleValid()
	{
		boolean valid = false;

		if ((containerNamePart1.compareTo("") != 0) ||
					(containerNamePart2.compareTo("") != 0))
			valid = true;

		return valid;
	}

	public boolean enclosingSourceValid()
	{
		boolean valid = true;

		if (((! agent5.isEmpty()) || (! agent6.isEmpty())
				|| (enclCorporateName.compareTo("") != 0)
				|| (enclCntNamePart1.compareTo("") != 0)
				|| (enclCntNamePart2.compareTo("") != 0)
				|| (enclPreSepar.compareTo("") != 0))
						&&
			((sublocationPart1.compareTo("") == 0) &&
			 (sublocationPart2.compareTo("") == 0)))
		  valid = false;

		return valid;
	}

	public String getContainerNamePart1()
	{
		return containerNamePart1;
	}

	public void setContainerNamePart1(String containerNamePart1)
	{
		this.containerNamePart1 = containerNamePart1;
	}

	public String getContainerNamePart2()
	{
		return containerNamePart2;
	}

	public void setContainerNamePart2(String containerNamePart2)
	{
		this.containerNamePart2 = containerNamePart2;
	}

	public String getDateRole()
	{
		return dateRole;
	}

	public void setDateRole(String dateRole)
	{
		this.dateRole = dateRole;
	}

	public String getEnclCntNamePart1()
	{
		return enclCntNamePart1;
	}

	public void setEnclCntNamePart1(String enclCntNamePart1)
	{
		this.enclCntNamePart1 = enclCntNamePart1;
	}

	public String getEnclCntNamePart2()
	{
		return enclCntNamePart2;
	}

	public void setEnclCntNamePart2(String enclCntNamePart2)
	{
		this.enclCntNamePart2 = enclCntNamePart2;
	}

	public String getEnclCorporateName()
	{
		return enclCorporateName;
	}

	public void setEnclCorporateName(String enclCorporateName)
	{
		this.enclCorporateName = enclCorporateName;
	}

	public String getEnclPreSepar()
	{
		return enclPreSepar;
	}

	public void setEnclPreSepar(String enclPreSepar)
	{
		this.enclPreSepar = enclPreSepar;
	}

	public String getEnclRole()
	{
		return enclRole;
	}

	public void setEnclRole(String enclRole)
	{
		this.enclRole = enclRole;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public String getPreSepar()
	{
		return preSepar;
	}

	public void setPreSepar(String preSepar)
	{
		this.preSepar = preSepar;
	}

	public String getSublocationPart1()
	{
		return sublocationPart1;
	}

	public void setSublocationPart1(String sublocationPart1)
	{
		this.sublocationPart1 = sublocationPart1;
	}

	public String getSublocationPart2()
	{
		return sublocationPart2;
	}

	public void setSublocationPart2(String sublocationPart2)
	{
		this.sublocationPart2 = sublocationPart2;
	}

	public Agent getAgent1()
	{
		return agent1;
	}

	public void setAgent1(Agent agent1)
	{
		this.agent1.setRole(agent1.getRole());
		this.agent1.setFirst(agent1.getFirst());
		this.agent1.setSurname(agent1.getSurname());
		this.agent1.setMiddle1(agent1.getMiddle1());
		this.agent1.setMiddle2(agent1.getMiddle2());
		this.agent1.setSuffix(agent1.getSuffix());
		this.agent1.setPrefix(agent1.getPrefix());
		this.agent1.setPreSeparator(agent1.getPreSeparator());
	}

	public Agent getAgent2() {
		return agent2;
	}

	public void setAgent2(Agent agent2)
	{
		this.agent2.setRole(agent2.getRole());
		this.agent2.setFirst(agent2.getFirst());
		this.agent2.setSurname(agent2.getSurname());
		this.agent2.setMiddle1(agent2.getMiddle1());
		this.agent2.setMiddle2(agent2.getMiddle2());
		this.agent2.setSuffix(agent2.getSuffix());
		this.agent2.setPrefix(agent2.getPrefix());
		this.agent2.setPreSeparator(agent2.getPreSeparator());
	}

	public Agent getAgent3() {
		return agent3;
	}

	public void setAgent3(Agent agent3)
	{
		this.agent3.setRole(agent3.getRole());
		this.agent3.setFirst(agent3.getFirst());
		this.agent3.setSurname(agent3.getSurname());
		this.agent3.setMiddle1(agent3.getMiddle1());
		this.agent3.setMiddle2(agent3.getMiddle2());
		this.agent3.setSuffix(agent3.getSuffix());
		this.agent3.setPrefix(agent3.getPrefix());
		this.agent3.setPreSeparator(agent3.getPreSeparator());
	}

	public Agent getAgent4() {
		return agent4;
	}

	public void setAgent4(Agent agent4)
	{
		this.agent4.setRole(agent4.getRole());
		this.agent4.setFirst(agent4.getFirst());
		this.agent4.setSurname(agent4.getSurname());
		this.agent4.setMiddle1(agent4.getMiddle1());
		this.agent4.setMiddle2(agent4.getMiddle2());
		this.agent4.setSuffix(agent4.getSuffix());
		this.agent4.setPrefix(agent4.getPrefix());
		this.agent4.setPreSeparator(agent4.getPreSeparator());
	}

	public Agent getAgent5() {
		return agent5;
	}

	public void setAgent5(Agent agent5)
	{
		this.agent5.setRole(agent5.getRole());
		this.agent5.setFirst(agent5.getFirst());
		this.agent5.setSurname(agent5.getSurname());
		this.agent5.setMiddle1(agent5.getMiddle1());
		this.agent5.setMiddle2(agent5.getMiddle2());
		this.agent5.setSuffix(agent5.getSuffix());
		this.agent5.setPrefix(agent5.getPrefix());
		this.agent5.setPreSeparator(agent5.getPreSeparator());
	}

	public Agent getAgent6() {
		return agent6;
	}

	public void setAgent6(Agent agent6)
	{
		this.agent6.setRole(agent6.getRole());
		this.agent6.setFirst(agent6.getFirst());
		this.agent6.setSurname(agent6.getSurname());
		this.agent6.setMiddle1(agent6.getMiddle1());
		this.agent6.setMiddle2(agent6.getMiddle2());
		this.agent6.setSuffix(agent6.getSuffix());
		this.agent6.setPrefix(agent6.getPrefix());
		this.agent6.setPreSeparator(agent6.getPreSeparator());
	}

	public void printModel()
	{
	}

	public Calendar getDate()
	{
		setDate();
		return date;
	}

	public void setDate()
	{
		formatDate();
		this.date = date;
	}

	public int getDate1()
	{
		return date1;
	}

	public void setDate1(int date1)
	{
		this.date1 = date1;
	}

	public int getDate2()
	{
		return date2;
	}

	public void setDate2(int date2)
	{
		this.date2 = date2;
	}

	public int getDate3()
	{
		return date3;
	}

	public void setDate3(int date3)
	{
		this.date3 = date3;
	}

}
