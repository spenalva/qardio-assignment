package com.spenalva.qardio.service;

import java.util.List;

import com.spenalva.qardio.controller.domain.request.CreateMeasurementDTO;
import com.spenalva.qardio.controller.domain.request.GetMeasurementDTO;
import com.spenalva.qardio.domain.AggregationType;
import com.spenalva.qardio.domain.MeasurementDTO;
import com.spenalva.qardio.domain.MeasurementScale;

/**
 * Service that manages the measurement operations exposed by this system.
 */
public interface MeasurementService {
	
	/**
	 * Creates one measurement. The method will transform the value sent from the scale to <b>C</b> before save it.
	 * In case that the pair <code>idSensor-timestamp</code> already exist in the database, the value stored will 
	 * be override by the value sent.
	 * If not start timestamp is provided, <code>0</code> will be used.
	 * If not end timestamp is provided, <code>Long.MAX_VALUE</code> will be used.
	 * @param measurement The data to be saved.
	 * @return Data created.
	 * 
	 * @see MeasurementDTO
	 * @see MeasurementScale
	 * @see CreateMeasurementDTO
	 */
	MeasurementDTO createMeasurement(CreateMeasurementDTO measurement);
	
	/**
	 * Retrieves the measurements stored in the system for the filters specified by the client.
	 * The method will return all the data for the <code>idSensor</code> specified and aggregate hourly (by default) or daily.
	 * By default, the data returned will be in {@link MeasurementScale.C} scale, but client can specify one of {@link MeasurementScale} values.
	 * Also, the client can filter by start or end timestamps.
	 * @param request The data that will be used to filter the records.
	 * @return List of records that match the filter.
	 * @throws NotFoundException If no records match the <code>request</code> filter.
	 * 
	 * @see GetMeasurementDTO
	 * @see MeasurementScale
	 * @see MeasurementDTO
	 * @see AggregationType
	 */
	List<MeasurementDTO> getAllMeasurement(GetMeasurementDTO request);

	/**
	 * Creates a list of measurements asynchronously. 
	 * Apart from that, the behaviour is the same that {@link MeasurementService#createMeasurement}.
	 * @param request The data to be saved.
	 */
	void asyncCreateMeasurements(List<CreateMeasurementDTO> request);
}