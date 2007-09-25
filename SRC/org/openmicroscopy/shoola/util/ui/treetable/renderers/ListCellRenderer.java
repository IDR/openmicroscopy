/*
 * org.openmicroscopy.shoola.util.ui.treetable.renderers.ListCellRenderer 
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
package org.openmicroscopy.shoola.util.ui.treetable.renderers;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import org.openmicroscopy.shoola.agents.measurement.util.ValueType;
import org.openmicroscopy.shoola.util.ui.treetable.util.OMETreeTableRenderUtils;

//Java imports

//Third-party libraries

//Application-internal dependencies

/** 
 * 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author	Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class ListCellRenderer
	extends JComboBox 
	implements TableCellRenderer
{
		/** Default font size. */
		private static final int 	FONTSIZE = 10;
	
		/**
		 * Creates a new instance. Sets the opacity of the label to
		 * <code>true</code>.
		 */
		public ListCellRenderer(String[] items)
		{
			super(items);
			setOpaque(true);
		}
		
		/** Set the Items in the combobox to new Items. 
		 * 
		 * @param items see above.
		 */
		public void setItems(String[] items)
		{
			this.removeAllItems();
			for(int i = 0 ; i < items.length ; i++)
				addItem(items[i]);
		}
		
		/**
		 * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object,
		 *      boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column)
		{
			setSelectedItem(value);
			OMETreeTableRenderUtils
				.setRowColor(this, table.getSelectedRow(), row);
			return this;
		}
		
}


