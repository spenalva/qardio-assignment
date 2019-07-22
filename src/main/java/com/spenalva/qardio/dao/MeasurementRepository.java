package com.spenalva.qardio.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.spenalva.qardio.domain.Measurement;

/**
 * Repository object that manages <code>Measurement</code> database access.
 * 
 * @see Measurement
 */
@Repository
@Transactional
public interface MeasurementRepository extends CrudRepository<Measurement, Long> {
	
	/**
	 * Method to filter and retrieve the measurements. The method needs the <code>idSensor</code> of the client,
	 * the <code>timestamp</code> that represents the initial date and the <code>timestamp</code> that represents
	 * the end date.
	 * 
	 * @param idSensor Client identifier.
	 * @param start Start date, in timestamp format.
	 * @param end End date, in timestamp format.
	 * @return <code>Optional list</code> containing all the matching records. The <code>Optional</code> will be empty
	 * if no matching records are found.
	 * 
	 * @see Measurement
	 */
	Optional<List<Measurement>> findByIdSensorAndTimestampBetween(Long idSensor, Long start, Long end);
	
}
