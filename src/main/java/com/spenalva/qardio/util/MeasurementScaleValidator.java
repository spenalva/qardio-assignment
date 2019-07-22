package com.spenalva.qardio.util;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class MeasurementScaleValidator implements ConstraintValidator<MeasurementScale, String> {

	@Override
	public void initialize(MeasurementScale scale) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return Arrays.stream(com.spenalva.qardio.domain.MeasurementScale.values()).anyMatch(e -> e.name().equalsIgnoreCase(value));
	}

}
