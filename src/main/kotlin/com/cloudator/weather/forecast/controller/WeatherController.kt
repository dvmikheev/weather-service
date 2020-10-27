package com.cloudator.weather.forecast.controller

import com.cloudator.weather.forecast.model.producer.City
import com.cloudator.weather.forecast.service.WeatherService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class WeatherController(
    val weatherService: WeatherService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }

    @GetMapping("/weather")
    fun readWeatherData(): List<City> {
        logger.info("Weather data request received")
        return weatherService.getStoredConditions()
    }
}
