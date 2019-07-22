package com.spenalva.qardio.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.spenalva.qardio.controller.domain.request.GetMeasurementDTO;
import com.spenalva.qardio.controller.domain.request.MeasurementsDTO;
import com.spenalva.qardio.domain.AggregationType;
import com.spenalva.qardio.domain.MeasurementScale;
import com.spenalva.qardio.domain.NotFoundException;
import com.spenalva.qardio.service.MeasurementService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller to manage measurements.
 * This controller allows to a client to <ul>
 * <li>Create one measurement</li>
 * <li>Create several measurement at once</li>
 * <li>Retrieve measurements, filtering by sensor and dates (optional).</li>
 * </ul>
 */
@RestController
@RequestMapping("/measurements")
@Slf4j
public class MeasurementController {

	@Autowired
	private MeasurementService service;
	
	/**
	 * Creates the measurements sent by the client. This endpoint produces and consumes <code>JSON</code>.
	 * This method needs at least one measurement to be saved.
	 * In case the client sent more than one measurement, the data will be saved asynchronously.
	 * The method needs <code>idSensor</code>, <code>timestamp</code> and <code>value</code> measured. Scale is optional, 
	 * but it must be one of {@link MeasurementScale} values if provided.
	 * @param request The data to be saved.
	 * @return Item created in case that just one measurement was saved, empty response otherwise.
	 * 
	 * @see MeasurementsDTO
	 * @see MeasurementScale
	 */
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public Object createMeasurement(@Valid @RequestBody MeasurementsDTO request) {
		log.info("Request to create new measurement");
		if (request.getMeasurements().size() > 1) {
			service.asyncCreateMeasurements(request.getMeasurements());
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
		}
		return service.createMeasurement(request.getMeasurements().iterator().next());
	}
	
	/**
	 * Retrieves the measurements stored in the system for the filters specified by the client.
	 * The method will return all the data for the <code>idSensor</code> specified and aggregate hourly (by default) or daily.
	 * By default, the data returned will be in {@link MeasurementScale.C} scale, but client can specify one of {@link MeasurementScale} values.
	 * Also, the client can filter by start or end timestamps. 
	 * @param request Filter to apply to the measurements.
	 * @return List with the measurements recovered aggregated as specified if there are matching measurements, empty response otherwise.
	 * 
	 * @see GetMeasurementDTO
	 * @see AggregationType
	 * @see MeasurementScale
	 */
	@GetMapping
	public Object getAllMeasurement(@Valid @ModelAttribute GetMeasurementDTO request) {
		try {
			return service.getAllMeasurement(request);
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
}
