/*
 * org.openmicroscopy.shoola.agents.viewer.ViewerUIF
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.agents.viewer;


//Java imports
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.viewer.canvas.ImageCanvas;
import org.openmicroscopy.shoola.agents.viewer.controls.ToolBar;
import org.openmicroscopy.shoola.env.config.Registry;
import org.openmicroscopy.shoola.env.rnd.metadata.PixelsDimensions;

/** 
 * 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2 
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
public class ViewerUIF
	extends JInternalFrame
{
	
	private static final int		EXTRA = 20;
	
	/** Canvas to display the currently selected 2D image. */
	private ImageCanvas             canvas;
	
	/** z-slider and t-slider. */
	private JSlider					tSlider, zSlider;
	
	private ToolBar					toolBar;
	
	private ViewerCtrl 				control;
	
	private Registry				registry;
	
	private boolean					active;
	
	ViewerUIF(ViewerCtrl control, Registry registry, String imageName)
	{
		//name, resizable, closable, maximizable, iconifiable.
		super(imageName, false, true, true, true);
		this.control = control;
		this.registry = registry;
		setJMenuBar(createMenuBar());
		PixelsDimensions pxsDims = control.getPixelsDims();
		int sizeT = pxsDims.sizeT;
		int sizeZ = pxsDims.sizeZ;
		int t = control.getDefaultT();
		int z = control.getDefaultZ();
		initSliders(sizeT-1, t, sizeZ-1, z);
		initToolBar(sizeT-1, t, sizeZ-1, z);
		buildGUI();
	}
	
	public JSlider getTSlider() { return tSlider; }
	
	public JSlider getZSlider() { return zSlider; }
	
	public ToolBar getToolBar() { return toolBar; } 
	
	/** Initiliazes the z-slider and t-slider. */
	private void initSliders(int maxT, int t, int maxZ, int z)
	{
		tSlider = new JSlider(JSlider.HORIZONTAL, 0, maxT, t);
		tSlider.setToolTipText("Move the slider to navigate across time.");
		if (maxT == 0) tSlider.setEnabled(false);
		zSlider = new JSlider(JSlider.VERTICAL, 0, maxZ, z);
		zSlider.setToolTipText("Move the slider to navigate across Z stack.");
		if (maxZ == 0) zSlider.setEnabled(false);
	}
	
	/** Initiliazes the toolBar. */
	private void initToolBar(int maxT, int t, int maxZ, int z)
	{
		PixelsDimensions pxsDims = control.getPixelsDims();
		toolBar = new ToolBar(control, registry, maxT, maxZ, t, z);
	}
	
	/** 
	 * Sizes, centers and brings up the specified editor dialog.
	 *
	 * @param editor	The editor dialog.
	 */
	void showDialog(JDialog editor)
	{
		JFrame topFrame = (JFrame) registry.getTopFrame().getFrame();
		Rectangle tfB = topFrame.getBounds(), psB = editor.getBounds();
		int offsetX = (tfB.width-psB.width)/2, 
			offsetY = (tfB.height-psB.height)/2;
		if (offsetX < 0) offsetX = 0;
		if (offsetY < 0) offsetY = 0;
		editor.setLocation(tfB.x+offsetX, tfB.y+offsetY);
		editor.setVisible(true);
	}
	
	/**
	 * Display the image in the viewer.
	 * 
	 * @param img
	 */
	 void setImage(BufferedImage img)
	 {
		canvas.display(img);
		if (!active) {
			int w = canvas.getIconWidth();
			int h = canvas.getIconHeight();
			tSlider.setSize(w, EXTRA);
			zSlider.setSize(EXTRA, h);
			setSize(w, h);
			pack();
	 	}
	 	active = true;
		revalidate();
	 }
	   
	/** Create an internal menu. */
	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar(); 
		menuBar.add(createControlMenu());
		menuBar.add(createMovieMenu());
		return menuBar;
	}

	/** Create a menu. */
	private JMenu createMovieMenu()
	{
		IconManager im = IconManager.getInstance(registry);
		JMenu menu = new JMenu("Movie");
		JMenuItem menuItem = new JMenuItem("Play", 
									im.getIcon(IconManager.MOVIE));
		control.setMenuItemListener(menuItem, ViewerCtrl.MOVIE_PLAY);
		menu.add(menuItem);
		menuItem = new JMenuItem("Stop", 
									im.getIcon(IconManager.STOP));
		control.setMenuItemListener(menuItem, ViewerCtrl.MOVIE_STOP);
		menu.add(menuItem);
		menuItem = new JMenuItem("Rewind", 
									im.getIcon(IconManager.REWIND));
		control.setMenuItemListener(menuItem, ViewerCtrl.MOVIE_REWIND);
		menu.add(menuItem);
		return menu;
	}
	
	/** Create the <code>newMenu</code>. */
	private JMenu createControlMenu()
	{
		JMenu menu = new JMenu("Controls");
		JMenuItem menuItem = new JMenuItem("Rendering");
		control.setMenuItemListener(menuItem, ViewerCtrl.RENDERING);
		menu.add(menuItem);
		menuItem = new JMenuItem("SAVE AS...");
		control.setMenuItemListener(menuItem, ViewerCtrl.SAVE_AS);
		menu.add(menuItem);
		return menu;
	}	
	
	/** Build and lay out the GUI. */
	private void buildGUI()
	{
		Container container = getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(toolBar);
		canvas = new ImageCanvas(this, container);
		JPanel p = new JPanel(), pt = new JPanel(), pz = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		pt.setLayout(new BoxLayout(pt, BoxLayout.X_AXIS));
		JLabel label = new JLabel("T ");
		pt.add(label);
		pt.add(tSlider);
		pz.setLayout(new BoxLayout(pz, BoxLayout.Y_AXIS));
		label = new JLabel("Z ");
		pz.add(label);
		pz.add(zSlider);
		JScrollPane scrollPane = new JScrollPane(canvas);
		p.add(pz);
		p.add(scrollPane);
		container.add(p);
		container.add(pt);
		IconManager im = IconManager.getInstance(registry);
		Icon icon = im.getIcon(IconManager.OME);
		setFrameIcon(icon);
	}
	
}
