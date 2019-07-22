package com.spenalva.qardio.config;

import java.sql.Timestamp;
import java.util.stream.IntStream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.spenalva.qardio.dao.MeasurementRepository;
import com.spenalva.qardio.domain.Measurement;
import com.spenalva.qardio.domain.MeasurementScale;

@Configuration
@ComponentScan(basePackages = "com.spenalva.qardio")
@EnableJpaRepositories(basePackages = "com.spenalva.qardio.dao")
@EntityScan("com.spenalva.qardio.domain")
public class ApplicationConfig {

	@Autowired
	private MeasurementRepository measurementRepository;

	@Bean
	InitializingBean presetData() {
		return () -> {
			long offset = Timestamp.valueOf("2018-06-01 00:00:00").getTime();
			long end = Timestamp.valueOf("2019-01-01 00:00:00").getTime();
			long diff = end - offset + 1;
			IntStream.range(0, 0).forEach(i -> {
				Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));
				measurementRepository.save(new Measurement(rand.getTime(), 1L, 10.0 + (273 + Math.random() * 25) , MeasurementScale.K));

			});
		};
	}

}