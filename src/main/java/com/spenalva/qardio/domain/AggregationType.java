package com.spenalva.qardio.domain;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AggregationType {
	H(3_600_000), D(24 * 3_600_000);

	private long multiplier;

	/**
	 * Get the matching enum instance for the <code>aggregationType</code> specified.
	 * @param aggregationType <code>String</code> for the type of aggregation.
	 * @return Enum instance.
	 * @throws NotFoundException In case no value exists in the enum for the <code>aggregationType</code> specified.
	 */
	public static AggregationType getAggregationType (String aggregationType) {
		return Arrays.asList(AggregationType.values()).stream().filter(at -> at.name().equalsIgnoreCase(aggregationType)).findFirst().orElseThrow(NotFoundException::new);
	}
}