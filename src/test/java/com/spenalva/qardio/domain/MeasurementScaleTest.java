package com.spenalva.qardio.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
public class MeasurementScaleTest {

	@Test
	public void transformToTestOK() {
		MeasurementScale to = MeasurementScale.K;		
		Assert.assertEquals((Double)273.15, to.transformTo(0.0, to));
		
		to = MeasurementScale.F;
		Assert.assertEquals((Double)32.0, to.transformTo(0.0, to));
		
		to = MeasurementScale.C;
		Assert.assertEquals((Double)0.0, to.transformTo(0.0, to));
	}
	
	@Test
	public void transformToCOK() {
		MeasurementScale from = MeasurementScale.K;		
		Assert.assertEquals((Double)0.0, from.transformToC(273.15, from));
		
		from = MeasurementScale.F;
		Assert.assertEquals((Double)0.0, from.transformToC(32.0, from));
		
		from = MeasurementScale.C;
		Assert.assertEquals((Double)0.0, from.transformToC(0.0, from));
	}
}
