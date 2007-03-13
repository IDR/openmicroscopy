/*
 * org.openmicroscopy.shoola.agents.treeviewer.profile.ProfileEditorFactory 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2007 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.treeviewer.profile;

import pojos.ExperimenterData;

//Java imports

//Third-party libraries

//Application-internal dependencies

/** 
 * 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class ProfileEditorFactory 
{

	 /** The sole instance. */
    private static final ProfileEditorFactory singleton = 
    						new ProfileEditorFactory();
    
    /**
     * Returns an editor for the passed experimenter.
     * 
     * @param exp The experimenter to edit.
     * @return See above.
     */
    public static ProfileEditor getEditor(ExperimenterData exp)
    {
    	if (exp == null)
    		throw new IllegalArgumentException("No experimenter sepcified.");
    	return singleton.createEditor(exp);
    }
    
    
    /** The tracked component. */
    private ProfileEditor  editor;
    
    /** Creates a new instance. */
    private ProfileEditorFactory()
    {
    	editor = null;
    }

    /**
     * Creates or recycles the editor for the specified experimenter.
     * 
     * @param exp The experimenter to edit.
     * @return See above.
     */
    private ProfileEditor createEditor(ExperimenterData exp)
    {
    	if (editor != null) return editor;
    	ProfileEditorModel model = new ProfileEditorModel(exp);
    	ProfileEditorComponent c = new ProfileEditorComponent(model);
    	model.initialize(c);
    	c.initialize();
    	editor = c;
    	return c;
    }
    
}
