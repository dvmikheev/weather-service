package com.cloudator.weather.forecast.service

import com.cloudator.weather.forecast.model.consumer.WeatherForecast
import com.cloudator.weather.forecast.model.producer.City
import com.cloudator.weather.forecast.model.producer.WeatherConditions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import java.lang.RuntimeException
import java.math.BigDecimal

@Service
class WeatherService(
    @Value("\${weather.provider}")
    private val provider: String,
    @Value("\${weather.apiId}")
    private val apiId: String,
    @Value("\${weather.locations}")
    private val locations: List<String>,
    @Value("\${weather.tempLow}")
    private val tempLow: BigDecimal,
    @Value("\${weather.tempHigh}")
    private val tempHigh: BigDecimal,
    val restTemplate: RestTemplate
) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
    private val conditions: MutableList<City> = mutableListOf()

    @Scheduled(fixedDelayString = "\${weather.checkFrequency}")
    fun getWeatherData() {
        conditions.clear()
        locations.map { city ->
            val response = sendWeatherRequest(city)
            if (response != null) {
                conditions.add(
                    City(
                        cityName = response.city.name,
                        weatherConditions = response.list.filter {
                            kelvinToCelsius(it.main.tempMin) < tempLow ||
                                kelvinToCelsius(it.main.tempMax) > tempHigh
                        }.map {
                            WeatherConditions(
                                time = it.dtTxt,
                                tempMin = kelvinToCelsius(it.main.tempMin),
                                tempMax = kelvinToCelsius(it.main.tempMax)
                            )
                        }
                    )
                )
            } else
                throw RuntimeException("Error happened while processing response")
        }
        logger.info("Weather data successfully received from producer")
    }

    private fun sendWeatherRequest(cityName: String): WeatherForecast? {
        var forecast: WeatherForecast? = null
        try {
            forecast = restTemplate.getForEntity(
                "${provider}forecast?q=$cityName&appid=$apiId", WeatherForecast::class.java
            ).body
        } catch (e: ResourceAccessException) {
            logger.error("Error occured while processing weather data request")
        }
        return forecast
    }

    fun getStoredConditions(): List<City> {
        if (conditions.isEmpty())
            getWeatherData()
        return conditions
    }

    private fun kelvinToCelsius(temp: BigDecimal): BigDecimal {
        return temp.minus(273.toBigDecimal())
    }
}
