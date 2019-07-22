package com.spenalva.qardio.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data model object to represent a measurement.
 */
@Data
@AllArgsConstructor
public class MeasurementDTO {
	private Long timestamp;
	private Long idSensor;
	private Double value;
	
	public MeasurementDTO(Measurement dbRow) {
		timestamp = dbRow.getTimestamp();
		idSensor = dbRow.getIdSensor();
		value = dbRow.getValue();
	}
}
