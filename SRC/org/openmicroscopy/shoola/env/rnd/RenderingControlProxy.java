/*
 * org.openmicroscopy.shoola.env.rnd.RenderingControlProxy
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

package org.openmicroscopy.shoola.env.rnd;

//Java imports
import java.util.Iterator;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.env.event.EventBus;
import org.openmicroscopy.shoola.env.rnd.codomain.CodomainMapContext;
import org.openmicroscopy.shoola.env.rnd.defs.ChannelBindings;
import org.openmicroscopy.shoola.env.rnd.defs.QuantumDef;
import org.openmicroscopy.shoola.env.rnd.defs.RenderingDef;
import org.openmicroscopy.shoola.env.rnd.events.RenderingPropChange;
import org.openmicroscopy.shoola.env.rnd.metadata.PixelsDimensions;
import org.openmicroscopy.shoola.env.rnd.metadata.PixelsStats;

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
class RenderingControlProxy
	implements RenderingControl
{

	private RenderingControlImpl	servant;
	private RenderingDef			rndDefCopy;
	private EventBus				eventBus;
	
	RenderingControlProxy(RenderingControlImpl servant, RenderingDef copy,
							EventBus eventBus) 
	{
		this.servant = servant;
		this.rndDefCopy = copy;
		this.eventBus = eventBus;
	}

	public PixelsDimensions getPixelsDims() 
	{
		return servant.getPixelsDims();  //Read-only, no concurrency problems.
	}

	public PixelsStats getPixelsStats()
	{
		return servant.getPixelsStats();  //Read-only, no concurrency problems.
	}

	public void setModel(final int model)
	{
		rndDefCopy.setModel(model);
		MethodCall mCall = new MethodCall() {
			public void doCall() { 
				servant.setModel(model);
			}
		};
		RenderingPropChange rpc = new RenderingPropChange(mCall);
		eventBus.post(rpc);
	}

	public int getModel()
	{
		return rndDefCopy.getModel();
	}

	public int getDefaultZ() 
	{
		return rndDefCopy.getDefaultZ();
	}

	public int getDefaultT()
	{
		return rndDefCopy.getDefaultT();
	}

	public void setQuantumStrategy(final int family, final double coefficient,
														final int bitResolution)
	{
		//TODO: this might go well w/ our copy, but then throw an exception
		//in the servant. We need a future.
		
		QuantumDef qd = rndDefCopy.getQuantumDef(), newQd;
		newQd = new QuantumDef(family, qd.pixelType, coefficient, 
										qd.cdStart, qd.cdEnd, bitResolution);
		rndDefCopy.setQuantumDef(newQd);
		MethodCall mCall = new MethodCall() {
			public void doCall() { 
				servant.setQuantumStrategy(family, coefficient, bitResolution);
			}
		};
		RenderingPropChange rpc = new RenderingPropChange(mCall);
		eventBus.post(rpc);
	}

	public void setCodomainInterval(final int start, final int end)
	{
		//TODO: this might go well w/ our copy, but then throw an exception
		//in the servant. We need a future.
		
		QuantumDef qd = rndDefCopy.getQuantumDef(), newQd;
		newQd = new QuantumDef(qd.family, qd.pixelType, qd.curveCoefficient, 
												start, end, qd.bitResolution);
		rndDefCopy.setQuantumDef(newQd);
		CodomainMapContext mapCtx;
		Iterator i = rndDefCopy.getCodomainChainDef().iterator();
		while (i.hasNext()) {
			mapCtx = (CodomainMapContext) i.next();
			mapCtx.setCodomain(start, end);
		}
		MethodCall mCall = new MethodCall() {
			public void doCall() { 
				servant.setCodomainInterval(start, end);
			}
		};
		RenderingPropChange rpc = new RenderingPropChange(mCall);
		eventBus.post(rpc);	
	}

	public QuantumDef getQuantumDef() 
	{
		return rndDefCopy.getQuantumDef();
	}

	public void setChannelWindow(final int w, 
								final Comparable start, final Comparable end) 
	{	
		//TODO: this might go well w/ our copy, but then throw an exception
		//in the servant. We need a future.
		
		ChannelBindings[] cb = rndDefCopy.getChannelBindings();
		cb[w].setInputWindow(start, end);
		MethodCall mCall = new MethodCall() {
			public void doCall() { 
				servant.setChannelWindow(w, start, end);
			}
		};
		RenderingPropChange rpc = new RenderingPropChange(mCall);
		eventBus.post(rpc);	
	}

	public Comparable getChannelWindowStart(int w) 
	{
		ChannelBindings[] cb = rndDefCopy.getChannelBindings();
		return cb[w].getInputStart();
	}

	public Comparable getChannelWindowEnd(int w) 
	{
		ChannelBindings[] cb = rndDefCopy.getChannelBindings();
		return cb[w].getInputEnd();
	}

	public void setRGBA(final int w, final int red, final int green, 
						final int blue, final int alpha) 
	{
		ChannelBindings[] cb = rndDefCopy.getChannelBindings();
		cb[w].setRGBA(red, green, blue, alpha);
		MethodCall mCall = new MethodCall() {
			public void doCall() { 
				servant.setRGBA(w, red, green, blue, alpha);
			}
		};
		RenderingPropChange rpc = new RenderingPropChange(mCall);
		eventBus.post(rpc);
	}

	public int[] getRGBA(int w) 
	{
		ChannelBindings[] cb = rndDefCopy.getChannelBindings();
		return cb[w].getRGBA();
	}

	public void setActive(final int w, final boolean active) 
	{
		ChannelBindings[] cb = rndDefCopy.getChannelBindings();
		cb[w].setActive(active);
		MethodCall mCall = new MethodCall() {
			public void doCall() { 
				servant.setActive(w, active);
			}
		};
		RenderingPropChange rpc = new RenderingPropChange(mCall);
		eventBus.post(rpc);
	}

	public boolean isActive(int w) 
	{
		ChannelBindings[] cb = rndDefCopy.getChannelBindings();
		return cb[w].isActive();
	}

	public void addCodomainMap(final CodomainMapContext mapCtx) 
	{
		rndDefCopy.addCodomainMapCtx(mapCtx);
		MethodCall mCall = new MethodCall() {
			public void doCall() { 
				servant.addCodomainMap(mapCtx);
			}
		};
		RenderingPropChange rpc = new RenderingPropChange(mCall);
		eventBus.post(rpc);
	}

	public void updateCodomainMap(final CodomainMapContext mapCtx) 
	{
		rndDefCopy.updateCodomainMapCtx(mapCtx);
		MethodCall mCall = new MethodCall() {
			public void doCall() { 
				servant.updateCodomainMap(mapCtx);
			}
		};
		RenderingPropChange rpc = new RenderingPropChange(mCall);
		eventBus.post(rpc);
	}

	public void removeCodomainMap(final CodomainMapContext mapCtx) {
		rndDefCopy.removeCodomainMapCtx(mapCtx);
		MethodCall mCall = new MethodCall() {
			public void doCall() { 
				servant.removeCodomainMap(mapCtx);
			}
		};
		RenderingPropChange rpc = new RenderingPropChange(mCall);
		eventBus.post(rpc);
	}

	public void saveCurrentSettings() 
	{
		MethodCall mCall = new MethodCall() {
			public void doCall() { 
				servant.saveCurrentSettings();
			}
		};
		RenderingPropChange rpc = new RenderingPropChange(mCall);
		eventBus.post(rpc);
	}

}
