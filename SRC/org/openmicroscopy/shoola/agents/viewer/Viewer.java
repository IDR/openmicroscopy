/*
 * org.openmicroscopy.shoola.agents.viewer.Viewer
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
import java.awt.image.BufferedImage;
import javax.swing.JCheckBoxMenuItem;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.rnd.events.DisplayRendering;
import org.openmicroscopy.shoola.env.Agent;
import org.openmicroscopy.shoola.env.config.Registry;
import org.openmicroscopy.shoola.env.event.AgentEvent;
import org.openmicroscopy.shoola.env.event.AgentEventListener;
import org.openmicroscopy.shoola.env.event.EventBus;
import org.openmicroscopy.shoola.env.rnd.RenderingControl;
import org.openmicroscopy.shoola.env.rnd.defs.PlaneDef;
import org.openmicroscopy.shoola.env.rnd.events.ImageLoaded;
import org.openmicroscopy.shoola.env.rnd.events.ImageRendered;
import org.openmicroscopy.shoola.env.rnd.events.LoadImage;
import org.openmicroscopy.shoola.env.rnd.events.RenderImage;
import org.openmicroscopy.shoola.env.rnd.metadata.PixelsDimensions;
import org.openmicroscopy.shoola.env.ui.TopFrame;

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
public class Viewer
	implements Agent, AgentEventListener
{
		
	/** Reference to the {@link Registry}. */
	private Registry			registry;
	
	private ViewerUIF			presentation;
	private ViewerCtrl			control;
	private TopFrame			topFrame;
	private RenderingControl	renderingControl;
	
	private int					curImageID, curPixelsID;
	private BufferedImage		curImage;
	
	private JCheckBoxMenuItem	viewItem;
	
	/** Implemented as specified by {@link Agent}. */
	public void activate() {}
	
	/** Implemented as specified by {@link Agent}. */
	public void terminate() {}

	/** Implemented as specified by {@link Agent}. */
	public void setContext(Registry ctx) 
	{
		registry = ctx;
		EventBus bus = registry.getEventBus();
		bus.register(this, ImageLoaded.class);
		bus.register(this, ImageRendered.class);
		topFrame = registry.getTopFrame();
		viewItem = getViewMenuItem();
		topFrame.addToMenu(TopFrame.VIEW, viewItem);
	}

	/** Implemented as specified by {@link Agent}. */
	public boolean canTerminate() { return true; }

	ViewerUIF getPresentation() { return presentation; }
	
	Registry getRegistry() { return registry; }
	
	PixelsDimensions getPixelsDims()
	{ 
		return renderingControl.getPixelsDims();
	}
	
	/** Default timepoint. */
	int getDefaultT() { return renderingControl.getDefaultT(); }
	
	/** Default z-section in the stack. */
	int getDefaultZ() { return renderingControl.getDefaultZ(); }
	
	/** Return the current buffered image. */
	BufferedImage getCurImage() { return curImage; }
	
	/** 2D-plane selected. */
	void onPlaneSelected(int z, int t)
	{
		PlaneDef def = new PlaneDef(PlaneDef.XY, t);
		def.setZ(z);
		RenderImage event = new RenderImage(curPixelsID, def);
		registry.getEventBus().post(event);	
	}
	
	/** Post an event to bring up the rendering agt. */
	void showRendering()
	{
		registry.getEventBus().post(new DisplayRendering());
	}
	
	/** Implement as specified by {@link AgentEventListener}. */
	public void eventFired(AgentEvent e) 
	{
		if (e instanceof ImageLoaded)
			handleImageLoaded((ImageLoaded) e);
		else if (e instanceof ImageRendered)
			handleImageRendered((ImageRendered) e);
	}
	
	/** Handle event @see ImageLoaded. */
	private void handleImageLoaded(ImageLoaded response)
	{
		LoadImage request = (LoadImage) response.getACT();
		renderingControl = response.getProxy();
		if (curImageID != request.getImageID()) {
			if (presentation == null) buildPresentation(request.getImageName());
			if (presentation.isClosed()) showPresentation();
			else if (presentation.isIcon()) deiconifyPresentation();
			curImageID = request.getImageID();
			curPixelsID = request.getPixelsID();
			RenderImage event = new RenderImage(curPixelsID);
			registry.getEventBus().post(event);
		} else {
			if (presentation.isClosed()) showPresentation();
			else if (presentation.isIcon()) deiconifyPresentation();
		}
	}
	
	/** Handle event @see ImageRendered. */
	private void handleImageRendered(ImageRendered response)
	{
		curImage = null;
		curImage = response.getRenderedImage();
		presentation.setImage(curImage);
		setMenuSelection(true);
	}
	
	/** Select the menuItem. */
	void setMenuSelection(boolean b) { viewItem.setSelected(b); }
	
	/** Display the presentation. */
	void showPresentation()
	{
		topFrame.removeFromDesktop(presentation);
		topFrame.addToDesktop(presentation, TopFrame.PALETTE_LAYER);
		try {
			presentation.setClosed(false);
		} catch (Exception e) {}
		presentation.setVisible(true);	
	}

	/** Pop up the presentation. */
	void deiconifyPresentation()
	{
		topFrame.deiconifyFrame(presentation);
		try {
			presentation.setIcon(false);
		} catch (Exception e) {}
	}
	
	/** Build the GUI. */
	private void buildPresentation(String imageName)
	{
		control = new ViewerCtrl(this);
		presentation = new ViewerUIF(control, registry, imageName);
		control.setPresentation(presentation);
		control.attachListener();
		control.setMenuItemListener(viewItem, ViewerCtrl.V_VISIBLE);
		viewItem.setEnabled(true);
		topFrame.addToDesktop(presentation, TopFrame.PALETTE_LAYER);
		presentation.setVisible(true);	
	}
	
	/** 
	 * Menu item to add to the 
	 * {@link org.openmicroscopy.shoola.env.ui.TopFrame} menu bar.
	 */
	private JCheckBoxMenuItem getViewMenuItem()
	{
		JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem("Viewer");
		menuItem.setEnabled(false);
		return menuItem;
	}
	
}
