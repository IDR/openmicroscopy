/*
 *   Copyright (C) 2009-2011 University of Dundee & Open Microscopy Environment.
 *   All rights reserved.
 *
 *   Use is subject to license terms supplied in LICENSE.txt
 */
package omeis.providers.re.utests;

import ome.model.enums.PixelsType;
import omeis.providers.re.quantum.Quantization_32_bit;
import omeis.providers.re.quantum.Quantization_8_16_bit;
import omeis.providers.re.quantum.QuantumFactory;

import org.testng.annotations.Test;

public class TestStandard32BitRendererLUTSizesFullRange extends BaseRenderingTest
{

    @Override
    protected QuantumFactory createQuantumFactory()
    {
        TestQuantumFactory qf = new TestQuantumFactory();
        qf.setStrategy(new Quantization_32_bit(settings.getQuantization(),
                pixels.getPixelsType()));
        return qf;
    }

	@Override
	protected int getSizeX()
	{
		return 2;
	}
	
	@Override
	protected int getSizeY()
	{
		return 2;
	}
	
	@Override
	protected byte[] getPlane()
	{
		return new byte[] {
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				};
	}
	
	@Override
	protected int getBytesPerPixel()
	{
		return 4;
	}
	
	@Override
	protected PixelsType getPixelsType()
	{
		PixelsType pixelsType = new PixelsType();
		pixelsType.setValue("uint32");
		return pixelsType;
	}
	
	@Test
	public void testPixelValues() throws Exception
	{
		assertEquals(0.0, data.getPixelValue(0));
		assertEquals(0.0, data.getPixelValue(1));
		assertEquals(0.0, data.getPixelValue(2));
		assertEquals(0.0, data.getPixelValue(3));
		assertEquals(Math.pow(2, 32)-1, data.getPixelValue(4));
		assertEquals(Math.pow(2, 32)-1, data.getPixelValue(5));
		assertEquals(Math.pow(2, 32)-1, data.getPixelValue(6));
		assertEquals(Math.pow(2, 32)-1, data.getPixelValue(7));
		try
		{
			assertEquals(0.0, data.getPixelValue(8));
			fail("Should have thrown an IndexOutOfBoundsException.");
		}
		catch (IndexOutOfBoundsException e) { }
	}
}