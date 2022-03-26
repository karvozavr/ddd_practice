package com.stringconcat.ddd.shop.rest.order

import MockGetOrderById
import arrow.core.left
import arrow.core.right
import com.stringconcat.ddd.shop.domain.order.OrderState
import com.stringconcat.ddd.shop.domain.orderId
import com.stringconcat.ddd.shop.rest.API_V1_ORDER_GET_BY_ID
import com.stringconcat.ddd.shop.usecase.order.GetOrderById
import com.stringconcat.ddd.shop.usecase.order.GetOrderByIdUseCaseError
import io.kotest.matchers.collections.shouldHaveSize
import notFoundTypeUrl
import orderDetails
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import withHost
import withId

@WebMvcTest
@ContextConfiguration(classes = [GetOrderByIdEndpointTest.TestConfiguration::class])
internal class GetOrderByIdEndpointTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var getOrderById: MockGetOrderById

    @Test
    fun `order not found`() {
        getOrderById.response = GetOrderByIdUseCaseError.OrderNotFound.left()
        val url = API_V1_ORDER_GET_BY_ID.withId(orderId().toLongValue()).withHost()
        mockMvc.get(url)
            .andExpect {
                content {
                    contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    status { isNotFound() }
                    content {
                        jsonPath("$.type") { notFoundTypeUrl() }
                        jsonPath("$.status") { value(HttpStatus.NOT_FOUND.value()) }
                    }
                }
            }
    }

    @Test
    fun `returned successfully - order is ready for confirm or cancel`() {
        val details = orderDetails(orderState = OrderState.PAID)
        details.items.shouldHaveSize(1)
        val itemDetails = details.items[0]

        getOrderById.response = details.right()
        val url = API_V1_ORDER_GET_BY_ID.withId(details.id.toLongValue()).withHost()

        mockMvc.get(url)
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    jsonPath("$.id") { value(details.id.toLongValue()) }
                    jsonPath("$.address.street") { value(details.address.streetToStringValue()) }
                    jsonPath("$.address.building") { value(details.address.buildingToIntValue()) }
                    jsonPath("$.totalPrice") { value(details.total.toBigDecimalValue()) }
                    jsonPath("$.items.length()") { value(1) }
                    jsonPath("$.items[0].mealId") { value(itemDetails.mealId.toLongValue()) }
                    jsonPath("$.items[0].count") { value(itemDetails.count.toIntValue()) }
                    jsonPath("$.version") { value(details.version.toLongValue()) }
                }
            }
        getOrderById.verifyInvoked(details.id)
    }

    @Test
    fun `returned successfully - order isn't ready for confirm or cancel`() {
        val details = orderDetails(orderState = OrderState.CANCELLED)
        details.items.shouldHaveSize(1)
        val itemDetails = details.items[0]

        getOrderById.response = details.right()
        val url = API_V1_ORDER_GET_BY_ID.withId(details.id.toLongValue()).withHost()

        mockMvc.get(url)
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    jsonPath("$.id") { value(details.id.toLongValue()) }
                    jsonPath("$.address.street") { value(details.address.streetToStringValue()) }
                    jsonPath("$.address.building") { value(details.address.buildingToIntValue()) }
                    jsonPath("$.totalPrice") { value(details.total.toBigDecimalValue()) }
                    jsonPath("$.items.length()") { value(1) }
                    jsonPath("$.items[0].mealId") { value(itemDetails.mealId.toLongValue()) }
                    jsonPath("$.items[0].count") { value(itemDetails.count.toIntValue()) }
                    jsonPath("$.version") { value(details.version.toLongValue()) }
                }
            }
        getOrderById.verifyInvoked(details.id)
    }

    @Configuration
    class TestConfiguration {

        @Bean
        fun getOrderByIdEndpoint(getOrderById: GetOrderById) = GetOrderByIdEndpoint(getOrderById)

        @Bean
        fun getOrderById() = MockGetOrderById()
    }
}