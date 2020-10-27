package com.cloudator.weather.forecast.service

import com.cloudator.weather.forecast.model.consumer.WeatherForecast
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
internal class WeatherServiceTest(
    @Value("\${weather.provider}")
    private val provider: String,
    @Value("\${weather.apiId}")
    private val apiId: String,
    @Value("\${weather.locations}")
    private val locations: List<String>,
    @Value("\${weather.tempLow}")
    private val tempLow: BigDecimal,
    @Value("\${weather.tempHigh}")
    private val tempHigh: BigDecimal
) {
    companion object {
        private val objectMapper = jacksonObjectMapper()
        val mockWeather = objectMapper.readValue(
            ClassPathResource("jsonPayloads/weatherResponse.json").file.readText(),
            WeatherForecast::class.java
        )
    }

    @MockK
    private lateinit var restTemplate: RestTemplate

    @InjectMockKs
    private lateinit var weatherService: WeatherService

    @Test
    fun `should retrieve weather data`() {
        val url = "${provider}forecast?q=${locations[0]}&appid=$apiId"

        every {
            restTemplate.getForEntity(url, WeatherForecast::class.java)
        } returns ResponseEntity(mockWeather, HttpStatus.OK)

        val response = weatherService.getStoredConditions()

        assertEquals(response.size, 1)
        assertEquals(response[0].cityName, locations[0])
    }
}
