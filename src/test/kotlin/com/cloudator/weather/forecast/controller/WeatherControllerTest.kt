package com.cloudator.weather.forecast.controller

import com.cloudator.weather.forecast.model.producer.City
import com.cloudator.weather.forecast.model.producer.WeatherConditions
import com.cloudator.weather.forecast.service.WeatherService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

@SpringJUnitConfig
@WebMvcTest(controllers = [WeatherController::class])
internal class WeatherControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var weatherService: WeatherService

    companion object {
        private var mockWeather = listOf(
            City(
                cityName = "London",
                weatherConditions = listOf(
                    WeatherConditions(
                        time = "2020-10-29 12:00:00",
                        tempMin = 14.49.toBigDecimal(),
                        tempMax = 14.49.toBigDecimal()
                    )
                )
            )
        )
    }

    @Test
    fun `should call weather service and return weather data`() {
        every { weatherService.getStoredConditions() } returns mockWeather

        mockMvc.get("/weather")
            .andDo { print() }
            .andExpect {
                status { isOk }
                content().contentType(MediaType.APPLICATION_JSON)
                jsonPath("$", hasSize<Any>(1))
                jsonPath("$[0].cityName", equalTo("London"))
            }
    }
}
