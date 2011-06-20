package org.infoml.infocardOrganizer;

/*

Copyright 2006 by Gregg Williams. Programming by Andrei Stoiculescu and Gregg Williams.

This file is part of Infocard Organizer.

Infocard Organizer is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or(at your option) any later version.

Infocard Organizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Infocard Organizer; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA.

*/

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class AFileFilter extends FileFilter
{
	String extension;

	public AFileFilter(String extension)
	{
		this.extension = extension;
	}

	 /* Return true if this file should be shown in the directory pane,
     * false if it shouldn't.
     */
	  public boolean accept(File f)
	  {
		if (f != null)
		{
		    if (f.isDirectory())
		    	return true;

		    String ext = getExtension(f);

		    if (ext != null)
		    if (ext.compareTo(extension) == 0)
		    	return true;
		}
		return false;
	  }

	  // Return the extension portion of the file's name
	  public String getExtension(File f)
	  {
		if (f != null)
		{
		    String filename = f.getName();

		    int i = filename.lastIndexOf('.');
		    if ((i > 0) && (i < filename.length()-1))
		    {
				return filename.substring(i+1).toLowerCase();
			}
		}
		return null;
   }

	public String getDescription()
	{
		return "*." + extension;
	}

	public String checkFormat(String source)
	{
		int index;
		if ((index = source.lastIndexOf(".")) != -1)
	    	if (source.substring(index+1, source.length()).
	    						compareTo(extension) == 0)
	    		return source;

		return source + "." + extension;
	}
}
