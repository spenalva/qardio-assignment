package com.spenalva.qardio.controller.domain.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.spenalva.qardio.util.MeasurementScale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data model for a measurement to be created.
 * 
 * @see MeasurementScale
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMeasurementDTO {

	@NotNull
	private Long idSensor;
	@Positive
	private Long timestamp;
	@NotNull
	private Double value;
	@MeasurementScale
	private String scale = com.spenalva.qardio.domain.MeasurementScale.C.name();
}
