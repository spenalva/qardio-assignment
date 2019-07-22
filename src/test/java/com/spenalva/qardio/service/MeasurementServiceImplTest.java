package com.spenalva.qardio.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.spenalva.qardio.controller.domain.request.CreateMeasurementDTO;
import com.spenalva.qardio.controller.domain.request.GetMeasurementDTO;
import com.spenalva.qardio.dao.MeasurementRepository;
import com.spenalva.qardio.domain.AggregationType;
import com.spenalva.qardio.domain.Measurement;
import com.spenalva.qardio.domain.MeasurementDTO;
import com.spenalva.qardio.domain.MeasurementScale;
import com.spenalva.qardio.domain.NotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
public class MeasurementServiceImplTest {

	@InjectMocks
	private MeasurementServiceImpl service;
	
	@Mock
	private MeasurementRepository repository;
	
	@Captor
	private ArgumentCaptor<Measurement> argCaptorCreate;
	
	@Captor
	private ArgumentCaptor<List<Measurement>> argCaptorCreateAsync;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void createMeasurementTestOK() {
		CreateMeasurementDTO measurement = new CreateMeasurementDTO();
		measurement.setIdSensor(1L);
		measurement.setTimestamp(new Date().getTime());
		measurement.setScale("C");
		measurement.setValue(25.0);
		
		Measurement saveResult = new Measurement(measurement);
		
		when(repository.save(any())).thenReturn(saveResult);
		
		MeasurementDTO result = service.createMeasurement(measurement);
		
		Mockito.verify(repository, times(1)).save(argCaptorCreate.capture());
		Assert.assertNotNull(result);
		Measurement dbrow = argCaptorCreate.getValue();
		Assert.assertEquals(dbrow.getIdSensor(), result.getIdSensor());
		Assert.assertEquals(dbrow.getTimestamp(), result.getTimestamp());
		Assert.assertEquals(dbrow.getValue(), result.getValue());
	}
	
	@Test
	public void asyncCreateMeasurementOK() {
		List<CreateMeasurementDTO> request = new ArrayList<>();
		IntStream.of(10).forEach(i -> {
			CreateMeasurementDTO measurement = new CreateMeasurementDTO();
			measurement.setIdSensor(1L);
			measurement.setTimestamp(new Date().getTime()+i);
			measurement.setScale("C");
			measurement.setValue(25.0);
			request.add(measurement);
		});
		
		service.asyncCreateMeasurements(request);
		
		Mockito.verify(repository, times(1)).saveAll(argCaptorCreateAsync.capture());
		Assert.assertEquals(request.size(), argCaptorCreateAsync.getValue().size());
	}
	
	@Test
	public void getAllMeasurementAllParametersOK() {
		GetMeasurementDTO request = new GetMeasurementDTO(1L, AggregationType.H.name(), new Date().getTime()-100000, new Date().getTime(), MeasurementScale.C.name());
		List<Measurement> rows = new ArrayList<>();
		IntStream.range(0, 10).forEach(i -> {
			Measurement measurement = new Measurement();
			measurement.setIdSensor(1L);
			measurement.setTimestamp(new Date().getTime()+i);
			measurement.setValue(25.0);
			measurement.setScale(MeasurementScale.C);
			rows.add(measurement);
		});
		Optional<List<Measurement>> dbResult = Optional.of(rows);
		
		when(repository.findByIdSensorAndTimestampBetween(anyLong(), anyLong(), any())).thenReturn(dbResult);
		
		List<MeasurementDTO> result = service.getAllMeasurement(request);
		Mockito.verify(repository, times(1)).findByIdSensorAndTimestampBetween(request.getIdSensor(), request.getTimestampFrom(), request.getTimestampTo());
		
		Assert.assertEquals(result.get(0).getIdSensor(), request.getIdSensor());
	}
	
	@Test
	public void getAllMeasurementConvertToKelvinOK() {
		GetMeasurementDTO request = new GetMeasurementDTO(1L, AggregationType.H.name(), null, null, MeasurementScale.K.name());
		List<Measurement> rows = new ArrayList<>();
		IntStream.range(0, 10).forEach(i -> {
			Measurement measurement = new Measurement();
			measurement.setIdSensor(1L);
			measurement.setTimestamp(new Date().getTime()+i);
			measurement.setValue(25.0);
			measurement.setScale(MeasurementScale.C);
			rows.add(measurement);
		});
		Optional<List<Measurement>> dbResult = Optional.of(rows);
		
		when(repository.findByIdSensorAndTimestampBetween(anyLong(), anyLong(), any())).thenReturn(dbResult);
		
		List<MeasurementDTO> result = service.getAllMeasurement(request);
		Mockito.verify(repository, times(1)).findByIdSensorAndTimestampBetween(request.getIdSensor(), 0L, Long.MAX_VALUE);
		
		Assert.assertEquals(result.get(0).getIdSensor(), request.getIdSensor());
		Assert.assertEquals(result.get(0).getValue(), (Double)298.15);
	}
	
	@Test
	public void getAllMeasurementConvertToFahrenheitOK() {
		GetMeasurementDTO request = new GetMeasurementDTO(1L, AggregationType.H.name(), new Date().getTime()-100000, new Date().getTime(), MeasurementScale.F.name());
		List<Measurement> rows = new ArrayList<>();
		IntStream.range(0, 10).forEach(i -> {
			Measurement measurement = new Measurement();
			measurement.setIdSensor(1L);
			measurement.setTimestamp(new Date().getTime()+i);
			measurement.setValue(0.0);
			measurement.setScale(MeasurementScale.C);
			rows.add(measurement);
		});
		Optional<List<Measurement>> dbResult = Optional.of(rows);
		
		when(repository.findByIdSensorAndTimestampBetween(anyLong(), anyLong(), any())).thenReturn(dbResult);
		
		List<MeasurementDTO> result = service.getAllMeasurement(request);
		Mockito.verify(repository, times(1)).findByIdSensorAndTimestampBetween(request.getIdSensor(), request.getTimestampFrom(), request.getTimestampTo());
		
		Assert.assertEquals(result.get(0).getIdSensor(), request.getIdSensor());
		Assert.assertEquals(result.get(0).getValue(), (Double)32.0);
	}
	
	@Test(expected = NotFoundException.class)
	public void getAllMeasurementNoResults () {
		GetMeasurementDTO request = new GetMeasurementDTO(1L, AggregationType.H.name(), new Date().getTime()-100000, new Date().getTime(), MeasurementScale.C.name());
		when(repository.findByIdSensorAndTimestampBetween(anyLong(), anyLong(), any())).thenReturn(Optional.empty());
		service.getAllMeasurement(request);
	}
	
	@Test
	public void getAllMeasurementsAggregatedByHourOK() {
		GetMeasurementDTO request = new GetMeasurementDTO(1L, AggregationType.H.name(), new Date().getTime()-100000, new Date().getTime(), MeasurementScale.C.name());
		List<Measurement> rows = new ArrayList<>();
		long offset = Timestamp.valueOf("2019-01-01 00:00:00").getTime();
		long end = Timestamp.valueOf("2019-01-01 01:59:00").getTime();
		long diff = end - offset + 1;
		IntStream.range(0, 100).forEach(i -> {
			Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));
			rows.add(new Measurement(rand.getTime(), 1L, (Math.random() * 25) , MeasurementScale.C));
		});
		Optional<List<Measurement>> dbResult = Optional.of(rows);
		when(repository.findByIdSensorAndTimestampBetween(anyLong(), anyLong(), any())).thenReturn(dbResult);
		List<MeasurementDTO> result = service.getAllMeasurement(request);
		Mockito.verify(repository, times(1)).findByIdSensorAndTimestampBetween(request.getIdSensor(), request.getTimestampFrom(), request.getTimestampTo());
		Assert.assertEquals(2, result.size());
		Assert.assertEquals((Long)Timestamp.valueOf("2019-01-01 00:00:00").getTime(), result.get(0).getTimestamp());
		Assert.assertEquals((Long)Timestamp.valueOf("2019-01-01 01:00:00").getTime(), result.get(1).getTimestamp());
	}

	@Test
	public void getAllMeasurementsAggregatedByDayOK() {
		GetMeasurementDTO request = new GetMeasurementDTO(1L, AggregationType.D.name(), new Date().getTime()-100000, new Date().getTime(), MeasurementScale.C.name());
		List<Measurement> rows = new ArrayList<>();
		long offset = Timestamp.valueOf("2019-01-01 14:00:00").getTime();
		long end = Timestamp.valueOf("2019-01-02 14:00:00").getTime();
		long diff = end - offset;
		IntStream.range(0, 100).forEach(i -> {
			Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));
			rows.add(new Measurement(rand.getTime(), 1L, (Math.random() * 25) , MeasurementScale.C));
		});
		Optional<List<Measurement>> dbResult = Optional.of(rows);
		when(repository.findByIdSensorAndTimestampBetween(anyLong(), anyLong(), any())).thenReturn(dbResult);
		List<MeasurementDTO> result = service.getAllMeasurement(request);
		Mockito.verify(repository, times(1)).findByIdSensorAndTimestampBetween(request.getIdSensor(), request.getTimestampFrom(), request.getTimestampTo());
		Assert.assertEquals(2, result.size());
		Assert.assertEquals((Long)Instant.ofEpochMilli(offset).truncatedTo(ChronoUnit.DAYS).toEpochMilli(), result.get(0).getTimestamp());
		Assert.assertEquals((Long)Instant.ofEpochMilli(end).truncatedTo(ChronoUnit.DAYS).toEpochMilli(), result.get(1).getTimestamp());
	}
	
	@Test
	public void getAllMeasurementsAverageOK() {
		GetMeasurementDTO request = new GetMeasurementDTO(1L, AggregationType.D.name(), new Date().getTime()-100000, new Date().getTime(), MeasurementScale.C.name());
		List<Measurement> rows = new ArrayList<>();
		rows.add(new Measurement(Timestamp.valueOf("2019-01-01 14:00:00").getTime(), 1L, 100.0 , MeasurementScale.C));
		rows.add(new Measurement(Timestamp.valueOf("2019-01-01 15:00:00").getTime(), 1L, 90.0 , MeasurementScale.C));
		Optional<List<Measurement>> dbResult = Optional.of(rows);
		when(repository.findByIdSensorAndTimestampBetween(anyLong(), anyLong(), any())).thenReturn(dbResult);
		List<MeasurementDTO> result = service.getAllMeasurement(request);
		Mockito.verify(repository, times(1)).findByIdSensorAndTimestampBetween(request.getIdSensor(), request.getTimestampFrom(), request.getTimestampTo());
		Assert.assertEquals(1, result.size());
		Assert.assertEquals((Double)95.0, result.get(0).getValue());
	}
	
	@Test(expected = DataAccessException.class)
	public void createMeasurementException() {
		when(repository.save(any())).thenThrow(new RecoverableDataAccessException(""));
		CreateMeasurementDTO measurement = new CreateMeasurementDTO();
		measurement.setIdSensor(1L);
		measurement.setTimestamp(new Date().getTime());
		measurement.setScale("C");
		measurement.setValue(25.0);
		service.createMeasurement(measurement);
	}
	
	@Test(expected = DataAccessException.class)
	public void asyncCreateMeasurementException() {
		List<CreateMeasurementDTO> request = new ArrayList<>();
		IntStream.of(10).forEach(i -> {
			CreateMeasurementDTO measurement = new CreateMeasurementDTO();
			measurement.setIdSensor(1L);
			measurement.setTimestamp(new Date().getTime()+i);
			measurement.setScale("C");
			measurement.setValue(25.0);
			request.add(measurement);
		});
		
		when(repository.saveAll(any())).thenThrow(new RecoverableDataAccessException(""));
		service.asyncCreateMeasurements(request);
	}
}
