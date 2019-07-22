package com.spenalva.qardio.domain;


/**
 * Enumeration that represents the scales allowed by the system:
 * <ul><li><b>K</b> (Kelvin)</li>
 * <li><b>C</b> (Celsius)</li>
 * <li><b>F</b> (Fahrenheit)</li>
 * </ul>
 *
 * This class is also responsible of the transformation from/to any scale to another.
 */
public enum MeasurementScale {
	C, F, K;
	
	/**
	 * Transform the specified <code>value</code> from the scale specified to {@link MeasurementScale.C} scale.
	 * 
	 * @param value The value to be transformed.
	 * @param from The scale that the value is before transformation.
	 * @return The value transformed in Celsius.
	 */
	public Double transformToC(Double value, MeasurementScale from) {
		Double valueInC;
		switch (from) {
		case F: valueInC = (value - 32.0) * (5.0/9.0);
				break;
		case K: valueInC = value - 273.15;
				break;
		case C:
		default:
				valueInC = value;
		}
		return valueInC;
	}
	
	/**
	 * Transform the specified <code>value</code> from {@link MeasurementScale.C} scale to the specified scale.
	 * 
	 * @param value The value to be transformed.
	 * @param to The scale that the value is going to be transformed.
	 * @return The value transformed in the specified scale.
	 */
	public Double transformTo(Double value, MeasurementScale to) {
		Double valueInC;
		switch (to) {
		case F: valueInC = (value * 9.0/5.0) + 32.0;
				break;
		case K: valueInC = value + 273.15;
				break;
		case C:
		default:
				valueInC = value;
		}
		return valueInC;
	}
}