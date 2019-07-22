package com.spenalva.qardio.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import com.spenalva.qardio.controller.domain.request.CreateMeasurementDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain object representing the <code>Measurement</code> entity.
 * 
 * @see MeasurementId
 */
@Entity
@IdClass(MeasurementId.class)
@Data
@NoArgsConstructor
public class Measurement implements Serializable {
	private static final long serialVersionUID = -88464504805024063L;
	@Id
	private Long timestamp;
	@Id
	private Long idSensor;
	private Double value;
	private MeasurementScale scale;
	
	public Measurement(Long timestamp, Long idSensor, Double value, MeasurementScale scale) {
		this.timestamp = timestamp;
		this.idSensor = idSensor;
		this.value = MeasurementScale.C.transformToC(value, scale);
		this.scale = scale;
	}
	
	public Measurement(CreateMeasurementDTO measurementDTO) {
		this.timestamp = measurementDTO.getTimestamp();
		this.idSensor = measurementDTO.getIdSensor();
		this.value = MeasurementScale.C.transformToC(measurementDTO.getValue(), MeasurementScale.valueOf(measurementDTO.getScale()));
		this.scale = MeasurementScale.valueOf(measurementDTO.getScale().toUpperCase());
	}
}
