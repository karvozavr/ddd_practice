package com.stringconcat.ddd.shop.app.controllers

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest
@ContextConfiguration(classes = [IndexEndpointTest.TestConfiguration::class])
internal class IndexEndpointTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `get index information`() {
        val url = "http://localhost"
        mockMvc.get(url)
                .andExpect {
                    status { isOk() }
                    content {
                        contentType("application/json")
                        jsonPath("$.title") { value(IndexModel().title) }
                    }
                }
    }

    @Configuration
    class TestConfiguration {
        @Bean
        fun indexEndpoint() = IndexEndpoint()
    }
}