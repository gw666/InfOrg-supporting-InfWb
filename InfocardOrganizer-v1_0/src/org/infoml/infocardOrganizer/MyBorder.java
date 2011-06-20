package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import javax.swing.border.*;
import javax.swing.*;
import java.awt.*;

public class MyBorder
{
public EmptyBorder eb;

	public Border getBorder()
	{
		eb = (EmptyBorder) BorderFactory.createEmptyBorder(5, 5, 5, 5);
		Border border = new CompoundBorder(eb,
		        BorderFactory.createLoweredBevelBorder());

		return border;
	}
}
