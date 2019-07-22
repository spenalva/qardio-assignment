package com.spenalva.qardio.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.spenalva.qardio.controller.domain.request.CreateMeasurementDTO;
import com.spenalva.qardio.controller.domain.request.GetMeasurementDTO;
import com.spenalva.qardio.dao.MeasurementRepository;
import com.spenalva.qardio.domain.AggregationType;
import com.spenalva.qardio.domain.Measurement;
import com.spenalva.qardio.domain.MeasurementDTO;
import com.spenalva.qardio.domain.MeasurementScale;
import com.spenalva.qardio.domain.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MeasurementServiceImpl implements MeasurementService {

	@Autowired
	private MeasurementRepository repository;
	
	@Override
	public MeasurementDTO createMeasurement(CreateMeasurementDTO measurement) {
		log.info("Creating new measurement: {}", measurement);
		try {
			return new MeasurementDTO(repository.save(new Measurement(measurement)));
		} catch (DataAccessException e) {
			log.error("Something went wrong saving measurement", e);
			throw e;
		}
	}

	@Override
	public List<MeasurementDTO> getAllMeasurement(GetMeasurementDTO request) {
		log.info("Getting measurements: {}", request);
		Long timestampFrom = getTimestampFromIfNull(request.getTimestampFrom());
		Long timestampTo = getTimestampToIfNull(request.getTimestampTo());
		AggregationType aggregationType = AggregationType.getAggregationType(request.getAggregationType());
		Optional<List<Measurement>> results = repository.findByIdSensorAndTimestampBetween(request.getIdSensor(), timestampFrom, timestampTo);
		if (results.isPresent()) {
			log.info("Results found. Returning aggregated by {}", aggregationType);
			return getAggregatedResults(results, aggregationType, request.getIdSensor(), MeasurementScale.valueOf(request.getScale()));
		}
		log.warn("Results not found");
		throw new NotFoundException();
	}

	private long getTimestampFromIfNull(Long timestampFrom) {
		return  timestampFrom == null ? 0 : timestampFrom;
	}

	private long getTimestampToIfNull(Long timestampTo) {
		return  timestampTo == null ? Long.MAX_VALUE : timestampTo;
	}

	private List<MeasurementDTO> getAggregatedResults(Optional<List<Measurement>> results, AggregationType aggregationType, Long idSensor, MeasurementScale scale) {
		Map<Object, Double> aggregatedResults = results.get().stream().map(MeasurementDTO::new).collect(Collectors.groupingBy(obj -> obj.getTimestamp() / (aggregationType.getMultiplier()), Collectors.averagingDouble(MeasurementDTO::getValue)));
		return aggregatedResults.entrySet().stream().map(e -> new MeasurementDTO((long) e.getKey()* aggregationType.getMultiplier(), idSensor, scale.transformTo(e.getValue(), scale))).collect(Collectors.toList());
	}

	@Override
	@Async
	public void asyncCreateMeasurements(List<CreateMeasurementDTO> request) {
		log.info("Saving {} measurements", request.size());
		try {
			repository.saveAll(request.stream().map(e -> new Measurement(e)).collect(Collectors.toList()));
		} catch (DataAccessException e) {
			log.error("Something went wrong saving measurements", e);
			throw e;
		}
	}

}
