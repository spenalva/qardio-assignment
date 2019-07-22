package com.spenalva.qardio.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import com.spenalva.qardio.controller.domain.request.CreateMeasurementDTO;
import com.spenalva.qardio.controller.domain.request.GetMeasurementDTO;
import com.spenalva.qardio.controller.domain.request.MeasurementsDTO;
import com.spenalva.qardio.domain.MeasurementDTO;
import com.spenalva.qardio.domain.MeasurementScale;
import com.spenalva.qardio.domain.NotFoundException;
import com.spenalva.qardio.service.MeasurementService;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

public class MeasurementControllerTest {
	private static final String PATH = "/measurements";
	@InjectMocks
	private MeasurementController controller;
	@Mock
	private MeasurementService service;
	@Captor
	private ArgumentCaptor<CreateMeasurementDTO> argCaptorCreate;
	@Captor
	private ArgumentCaptor<List<CreateMeasurementDTO>> argCaptorCreateAsync;
	@Captor
	private ArgumentCaptor<GetMeasurementDTO> argCaptorGetAll;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void createMeasurementOk() {
		MeasurementsDTO request = new MeasurementsDTO();
		List<CreateMeasurementDTO> list = new ArrayList<>();
		CreateMeasurementDTO measurement = new CreateMeasurementDTO(1L, new Date().getTime(), 25.0, MeasurementScale.C.name());
		list.add(measurement);
		request.setMeasurements(list);
		RestAssuredMockMvc.given().standaloneSetup(controller).contentType(ContentType.JSON).body(request).when().post(PATH)
		.then().statusCode(HttpStatus.CREATED.value());
		
		Mockito.verify(service, times(1)).createMeasurement(argCaptorCreate.capture());
		Mockito.verify(service, times(0)).asyncCreateMeasurements(any());
		CreateMeasurementDTO dtoToService = argCaptorCreate.getValue();
		Assert.assertEquals(measurement.getIdSensor(), dtoToService.getIdSensor());
		Assert.assertEquals(measurement.getValue(), dtoToService.getValue());
		Assert.assertEquals(measurement.getTimestamp(), dtoToService.getTimestamp());
	}
	
	@Test
	public void createMeasurementKONoSensor() {
		MeasurementsDTO request = new MeasurementsDTO();
		List<CreateMeasurementDTO> list = new ArrayList<>();
		CreateMeasurementDTO measurement = new CreateMeasurementDTO(null, new Date().getTime(), 25.0, MeasurementScale.C.name());
		list.add(measurement);
		request.setMeasurements(list);
		RestAssuredMockMvc.given().standaloneSetup(controller).contentType(ContentType.JSON).body(request).when().post(PATH)
		.then().statusCode(HttpStatus.BAD_REQUEST.value());
		Mockito.verify(service, times(0)).asyncCreateMeasurements(any());
		Mockito.verify(service, times(0)).createMeasurement(any());
	}
	
	@Test
	public void createMeasurementKONoValue() {
		MeasurementsDTO request = new MeasurementsDTO();
		List<CreateMeasurementDTO> list = new ArrayList<>();
		CreateMeasurementDTO measurement = new CreateMeasurementDTO(1L, new Date().getTime(), null, MeasurementScale.C.name());
		list.add(measurement);
		request.setMeasurements(list);
		RestAssuredMockMvc.given().standaloneSetup(controller).contentType(ContentType.JSON).body(request).when().post(PATH)
		.then().statusCode(HttpStatus.BAD_REQUEST.value());
		Mockito.verify(service, times(0)).asyncCreateMeasurements(any());
		Mockito.verify(service, times(0)).createMeasurement(any());
	}
	
	@Test
	public void createMeasurementKOWrongScale() {
		MeasurementsDTO request = new MeasurementsDTO();
		List<CreateMeasurementDTO> list = new ArrayList<>();
		CreateMeasurementDTO measurement = new CreateMeasurementDTO(1L, new Date().getTime(), 25.0, "J");
		list.add(measurement);
		request.setMeasurements(list);
		RestAssuredMockMvc.given().standaloneSetup(controller).contentType(ContentType.JSON).body(request).when().post(PATH)
		.then().statusCode(HttpStatus.BAD_REQUEST.value());
		Mockito.verify(service, times(0)).asyncCreateMeasurements(any());
		Mockito.verify(service, times(0)).createMeasurement(any());
	}
	
	@Test
	public void createMeasurementKONegativeTimestamp() {
		MeasurementsDTO request = new MeasurementsDTO();
		List<CreateMeasurementDTO> list = new ArrayList<>();
		CreateMeasurementDTO measurement = new CreateMeasurementDTO(1L, -100L, 25.0, MeasurementScale.C.name());
		list.add(measurement);
		request.setMeasurements(list);
		RestAssuredMockMvc.given().standaloneSetup(controller).contentType(ContentType.JSON).body(request).when().post(PATH)
		.then().statusCode(HttpStatus.BAD_REQUEST.value());
		Mockito.verify(service, times(0)).asyncCreateMeasurements(any());
		Mockito.verify(service, times(0)).createMeasurement(any());
	}
	
	@Test
	public void createMeasurementOKSeveralRows() {
		MeasurementsDTO request = new MeasurementsDTO();
		List<CreateMeasurementDTO> list = new ArrayList<>();
		IntStream.range(0, 10).forEach(i -> {
			CreateMeasurementDTO measurement = new CreateMeasurementDTO(1L, new Date().getTime()+i, 25.0+i, MeasurementScale.C.name());
			list.add(measurement);
		});		
		request.setMeasurements(list);
		RestAssuredMockMvc.given().standaloneSetup(controller).contentType(ContentType.JSON).body(request).when().post(PATH)
		.then().statusCode(HttpStatus.ACCEPTED.value());
		
		Mockito.verify(service, times(1)).asyncCreateMeasurements(argCaptorCreateAsync.capture());
		Mockito.verify(service, times(0)).createMeasurement(any());
		List<CreateMeasurementDTO> dtoToService = argCaptorCreateAsync.getValue();
		Assert.assertEquals(list.size(), dtoToService.size());
	}
	
	@Test
	public void createMeasurementsKONoData() {
		MeasurementsDTO request = new MeasurementsDTO();
		List<CreateMeasurementDTO> list = new ArrayList<>();
		request.setMeasurements(list);
		RestAssuredMockMvc.given().standaloneSetup(controller).contentType(ContentType.JSON).body(request).when().post(PATH)
		.then().statusCode(HttpStatus.BAD_REQUEST.value());
		Mockito.verify(service, times(0)).asyncCreateMeasurements(any());
		Mockito.verify(service, times(0)).createMeasurement(any());
	}
	
	@Test
	public void createMeasurementKO() {
		MeasurementsDTO request = new MeasurementsDTO();
		List<CreateMeasurementDTO> list = new ArrayList<>();
		CreateMeasurementDTO measurement = new CreateMeasurementDTO();
		list.add(measurement);
		request.setMeasurements(list);
		RestAssuredMockMvc.given().standaloneSetup(controller).contentType(ContentType.JSON).body(request).when().post(PATH)
		.then().statusCode(HttpStatus.BAD_REQUEST.value());
		
		Mockito.verify(service, times(0)).createMeasurement(any());
		Mockito.verify(service, times(0)).asyncCreateMeasurements(any());
	}
	
	@Test
	public void getAllMeasurementsOKAllArgs() {
		List<String> aggregationValues = Arrays.asList("d","D","h","H");
		String aggregationType = aggregationValues.get(new Random().nextInt(aggregationValues.size()));
		List<MeasurementDTO> result = new ArrayList<>();
		IntStream.range(0, 10).forEach(i -> {
			MeasurementDTO measurement = new MeasurementDTO(new Date().getTime()+i, 1L, 25.0+i);
			result.add(measurement);
		});

		Long timestampTo = new Date().getTime();
		Long timestampFrom = new Date().getTime();
		when(service.getAllMeasurement(any())).thenReturn(result);
		RestAssuredMockMvc.given().standaloneSetup(controller).queryParam("idSensor", 1L).queryParam("timestampFrom", timestampFrom).queryParam("timestampTo", timestampTo).queryParam("aggregationType", aggregationType).queryParam("scale", "C")
		.when().get(PATH).then().statusCode(HttpStatus.OK.value());
		
		Mockito.verify(service, times(1)).getAllMeasurement(argCaptorGetAll.capture());
		GetMeasurementDTO measurement = argCaptorGetAll.getValue();
		Assert.assertEquals(Long.valueOf(1), measurement.getIdSensor());
		Assert.assertEquals(timestampFrom, measurement.getTimestampFrom());
		Assert.assertEquals(timestampTo, measurement.getTimestampTo());
		Assert.assertEquals(aggregationType, measurement.getAggregationType());
	}
	
	@Test
	public void getAllMeasurementsOKWithoutTimestamps() {
		List<String> aggregationValues = Arrays.asList("d","D","h","H");
		String aggregationType = aggregationValues.get(new Random().nextInt(aggregationValues.size()));
		List<MeasurementDTO> result = new ArrayList<>();
		IntStream.range(0, 10).forEach(i -> {
			MeasurementDTO measurement = new MeasurementDTO(new Date().getTime()+i, 1L, 25.0+i);
			result.add(measurement);
		});

		
		when(service.getAllMeasurement(any())).thenReturn(result);
		RestAssuredMockMvc.given().standaloneSetup(controller).queryParam("idSensor", 1L).queryParam("aggregationType", aggregationType)
		.when().get(PATH).then().statusCode(HttpStatus.OK.value());
		
		Mockito.verify(service, times(1)).getAllMeasurement(argCaptorGetAll.capture());
		GetMeasurementDTO measurement = argCaptorGetAll.getValue();
		Assert.assertEquals(Long.valueOf(1), measurement.getIdSensor());
		Assert.assertNull(measurement.getTimestampFrom());
		Assert.assertNull(measurement.getTimestampTo());
		Assert.assertEquals(aggregationType, measurement.getAggregationType());
	}
	
	@Test
	public void getAllMeasurementsNoResultsOK() {
		when(service.getAllMeasurement(any())).thenThrow(new NotFoundException());
		RestAssuredMockMvc.given().standaloneSetup(controller).queryParam("idSensor", 1L).when().get(PATH)
		.then().statusCode(HttpStatus.NOT_FOUND.value());
		Mockito.verify(service, times(1)).getAllMeasurement(argCaptorGetAll.capture());
		GetMeasurementDTO measurement = argCaptorGetAll.getValue();
		Assert.assertEquals(Long.valueOf(1), measurement.getIdSensor());
	}
	
	@Test
	public void getAllMeasurementsKONoIdSensor() {
		when(service.getAllMeasurement(any())).thenThrow(new NotFoundException());
		RestAssuredMockMvc.given().standaloneSetup(controller).when().get(PATH)
		.then().statusCode(HttpStatus.BAD_REQUEST.value());
		Mockito.verify(service, times(0)).getAllMeasurement(any());
	}
	
	@Test
	public void getAllMeasurementsKOWrongAggregationType() {
		when(service.getAllMeasurement(any())).thenThrow(new NotFoundException());
		RestAssuredMockMvc.given().standaloneSetup(controller).queryParam("idSensor", 1L).queryParam("aggregationType", "Q").when().get(PATH)
		.then().statusCode(HttpStatus.BAD_REQUEST.value());
		Mockito.verify(service, times(0)).getAllMeasurement(any());
	}
	
	@Test
	public void getAllMeasurementsKOWrongScale() {
		when(service.getAllMeasurement(any())).thenThrow(new NotFoundException());
		RestAssuredMockMvc.given().standaloneSetup(controller).queryParam("idSensor", 1L).queryParam("scale", "D").when().get(PATH)
		.then().statusCode(HttpStatus.BAD_REQUEST.value());
		Mockito.verify(service, times(0)).getAllMeasurement(any());
	}
}
