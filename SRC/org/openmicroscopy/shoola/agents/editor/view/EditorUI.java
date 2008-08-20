/*
 * org.openmicroscopy.shoola.agents.editor.view.EditorUI 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
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
package org.openmicroscopy.shoola.agents.editor.view;

//Java imports
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.editor.EditorAgent;
import org.openmicroscopy.shoola.agents.editor.actions.EditorAction;
import org.openmicroscopy.shoola.env.ui.TaskBar;
import org.openmicroscopy.shoola.env.ui.TopWindow;

/** 
 * The {@link Editor}'s View.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta3
 */
class EditorUI
	extends TopWindow
{

	/** Reference to the Control. */
	private EditorControl 			controller;

	/** Reference to the Model. */
	private EditorModel   			model;
	
	/** Reference to the tool bar. */
	private EditorToolBar			toolBar;
	
	/** Reference to the status. */
	private EditorStatusBar			statusBar;
	
	private JMenu createMenu()
	{
		JMenu menu = new JMenu("Menu 1");
		EditorAction a = controller.getAction(EditorControl.CREATE);
		JMenuItem item = new JMenuItem(a);
		item.setText(a.getActionName());
		menu.add(item);
		return menu;
	}
	
	/** 
	 * Creates the menu bar.
	 * 
	 * @param pref The user preferences.
	 * @return The menu bar. 
	 */
	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar(); 
		menuBar.add(createMenu());
		TaskBar tb = EditorAgent.getRegistry().getTaskBar();
		menuBar.add(tb.getWindowsMenu());
		return menuBar;
	}
	
	
	/** Builds and lays out the UI. */
	private void buildGUI()
	{
		Container c = getContentPane();
		c.setLayout(new BorderLayout(0, 0));
		c.add(toolBar, BorderLayout.NORTH);
		c.add(new JPanel(), BorderLayout.CENTER);
		c.add(statusBar, BorderLayout.SOUTH);
	}
	
	/**
	 * Creates a new instance.
	 * The {@link #initialize(EditorControl, EditorModel) initialize} 
	 * method should be called straight after to link this View 
	 * to the Controller.
	 * 
	 * @param title The window title.
	 */
	EditorUI(String title)
	{
		super(title);
	}

	/**
	 * Links this View to its Controller and Model.
	 * 
	 * @param controller    Reference to the Control.
	 *                      Mustn't be <code>null</code>.
	 * @param model         Reference to the Model.
	 *                      Mustn't be <code>null</code>.
	 */
	void initialize(EditorControl controller, EditorModel model)
	{
		if (controller == null) throw new NullPointerException("No control.");
		if (model == null) throw new NullPointerException("No model.");
		this.controller = controller;
		this.model = model;
		toolBar = new EditorToolBar(controller);
		statusBar = new EditorStatusBar();
		setJMenuBar(createMenuBar());
		buildGUI();
	}
    
	/** 
	 * Sets the status message.
	 * 
	 * @param text  The message to display.
	 * @param hide  Pass <code>true</code> to hide the progress bar, 
	 *              <code>false</otherwise>.
	 */
    void setStatus(String text, boolean hide)
    {
        statusBar.setStatus(text);
        statusBar.setProgress(hide);
    }
    
    void editFile()
    {
    	System.err.println("Build UI");
    	
    }
    
}
