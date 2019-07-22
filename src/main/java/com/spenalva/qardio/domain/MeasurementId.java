package com.spenalva.qardio.domain;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data model object that set the composite id used for <code>Measurement</code> entity.
 * 
 * @see Measurement
 */
@Data
@NoArgsConstructor
public class MeasurementId implements Serializable {
	private static final long serialVersionUID = 8777962111649098399L;
	private Long timestamp;
	private Long idSensor;
}
