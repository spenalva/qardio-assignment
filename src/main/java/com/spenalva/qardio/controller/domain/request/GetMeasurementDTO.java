package com.spenalva.qardio.controller.domain.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.lang.Nullable;

import com.spenalva.qardio.domain.AggregationType;
import com.spenalva.qardio.util.MeasurementScale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data model for filtering the searches.
 * 
 * @see MeasurementScale
 * @see AggregationType
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMeasurementDTO {
	@NotNull
	private Long idSensor;
	@Nullable @Pattern(regexp = "[dDhH]", message="Aggregation must be \"H\" or \"D\" if provided")
	private String aggregationType = AggregationType.D.name();
	@Nullable
	private Long timestampFrom;
	@Nullable
	private Long timestampTo;
	@Nullable @MeasurementScale
	private String scale = com.spenalva.qardio.domain.MeasurementScale.C.name();
}
