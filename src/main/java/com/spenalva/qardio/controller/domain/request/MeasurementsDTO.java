package com.spenalva.qardio.controller.domain.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data model for measurements to be created.
 */
@Data
@NoArgsConstructor
public class MeasurementsDTO {
	@Valid @NotNull @Size(min = 1)
	List<CreateMeasurementDTO> measurements;
}
