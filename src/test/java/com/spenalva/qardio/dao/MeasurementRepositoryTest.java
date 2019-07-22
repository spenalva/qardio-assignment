package com.spenalva.qardio.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.spenalva.qardio.Application;
import com.spenalva.qardio.domain.Measurement;
import com.spenalva.qardio.domain.MeasurementScale;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Application.class})
@Transactional
public class MeasurementRepositoryTest {
	
	@Autowired
	private MeasurementRepository repository;
	
	@Test
	@Rollback
	public void findByIdSensorAndTimestampBetweenOK() {
		long idSensor = 123;
		Measurement measurement = new Measurement(Timestamp.valueOf("2019-01-01 00:00:00").getTime(), idSensor, 30.0, MeasurementScale.C);
		repository.save(measurement);
		
		measurement = new Measurement(Timestamp.valueOf("2019-01-02 00:00:00").getTime(), idSensor, 40.0, MeasurementScale.C);
		repository.save(measurement);

		measurement = new Measurement(Timestamp.valueOf("2019-01-01 00:00:01").getTime(), idSensor+idSensor, 50.0, MeasurementScale.C);
		repository.save(measurement);
		
		Optional<List<Measurement>> result = repository.findByIdSensorAndTimestampBetween(idSensor, Timestamp.valueOf("2019-01-01 00:00:00").getTime(), Timestamp.valueOf("2019-01-01 01:00:00").getTime());
		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(result.get().size(), 1);

		result = repository.findByIdSensorAndTimestampBetween(idSensor, 0L, Long.MAX_VALUE);
		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(result.get().size(), 2);
	}
}
