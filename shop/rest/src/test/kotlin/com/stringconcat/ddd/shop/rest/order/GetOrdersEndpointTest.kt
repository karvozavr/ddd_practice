package com.stringconcat.ddd.shop.rest.order

import APPLICATION_HAL_FORMS_JSON
import MockGetOrders
import arrow.core.left
import arrow.core.right
import badRequestTypeUrl
import com.stringconcat.ddd.shop.domain.orderId
import com.stringconcat.ddd.shop.rest.API_V1_ORDER_GET_WITH_PAGINATION
import com.stringconcat.ddd.shop.usecase.order.GetOrders
import com.stringconcat.ddd.shop.usecase.order.GetOrdersUseCaseError
import orderDetails
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import withHost
import withLimit
import withStartId

@WebMvcTest
internal class GetOrdersEndpointTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var getOrders: MockGetOrders

    @Test
    fun `limit reached`() {
        val startId = orderId()
        val limit = 10
        getOrders.response = GetOrdersUseCaseError.LimitExceed(limit + 1).left()

        val url = API_V1_ORDER_GET_WITH_PAGINATION.withStartId(startId.toLongValue()).withLimit(limit)
        mockMvc.get(url)
            .andExpect {
                content {
                    contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    status { isBadRequest() }
                    content {
                        jsonPath("$.type") { value(badRequestTypeUrl()) }
                        jsonPath("$.status") { value(HttpStatus.BAD_REQUEST.value()) }
                        jsonPath("$.invalid_params.length()") { value(1) }
                        jsonPath("$.invalid_params[0].message") { value("Max limit is 10") }
                    }
                }
            }

        getOrders.verifyInvoked(startId, limit + 1)
    }

    @Test
    fun `returned successfully without next page`() {
        val limit = 1

        val single = orderDetails()
        val firstItem = single.items[0]

        getOrders.response = listOf(single).right()

        val url = API_V1_ORDER_GET_WITH_PAGINATION.withStartId(single.id.toLongValue()).withLimit(limit).withHost()
        mockMvc.get(url)
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_HAL_FORMS_JSON)
                    jsonPath("$.count") { value(limit) }
                    jsonPath("$._links.self.href") { value(url) }
                    jsonPath("$._links.first.href") {
                        value(API_V1_ORDER_GET_WITH_PAGINATION.withStartId(0).withLimit(limit).withHost())
                    }
                    jsonPath("$._links.next.href") {
                        doesNotExist()
                    }

                    jsonPath("$._embedded.orders.length()") { value(limit) }
                    jsonPath("$._embedded.orders[0].id") { value(single.id.toLongValue()) }
                    jsonPath("$._embedded.orders[0].totalPrice") { value(single.total.toStringValue()) }
                    jsonPath("$._embedded.orders[0].version") { value(single.version.toLongValue()) }
                    jsonPath("$._embedded.orders[0].address.street") {
                        value(single.address.streetToStringValue())
                    }
                    jsonPath("$._embedded.orders[0].address.building") {
                        value(single.address.buildingToIntValue())
                    }
                    jsonPath("$._embedded.orders[0].items.length()") { value(1) }
                    jsonPath("$._embedded.orders[0].items[0].mealId") { value(firstItem.mealId.toLongValue()) }
                    jsonPath("$._embedded.orders[0].items[0].count") { value(firstItem.count.toIntValue()) }
                    jsonPath("$._embedded.orders[0]._links.self.href") { exists() }
                }
            }
        getOrders.verifyInvoked(single.id, limit + 1)
    }

    @Test
    fun `returned successfully with next page`() {
        val limit = 1

        val first = orderDetails()
        val firstItem = first.items[0]
        val second = orderDetails()

        getOrders.response = listOf(first, second).right()

        val url = API_V1_ORDER_GET_WITH_PAGINATION.withStartId(first.id.toLongValue()).withLimit(limit).withHost()

        mockMvc.get(url)
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_HAL_FORMS_JSON)
                    jsonPath("$.count") { value(limit) }
                    jsonPath("$._links.self.href") {
                        value(url)
                    }
                    jsonPath("$._links.first.href") {
                        value(API_V1_ORDER_GET_WITH_PAGINATION.withStartId(0).withLimit(limit).withHost())
                    }
                    jsonPath("$._links.next.href") {
                        value(API_V1_ORDER_GET_WITH_PAGINATION
                            .withStartId(second.id.toLongValue()).withLimit(limit).withHost())
                    }

                    jsonPath("$._embedded.orders.length()") { value(limit) }
                    jsonPath("$._embedded.orders[0].id") { value(first.id.toLongValue()) }
                    jsonPath("$._embedded.orders[0].totalPrice") { value(first.total.toStringValue()) }
                    jsonPath("$._embedded.orders[0].version") { value(first.version.toLongValue()) }
                    jsonPath("$._embedded.orders[0].address.street") {
                        value(first.address.streetToStringValue())
                    }
                    jsonPath("$._embedded.orders[0].address.building") {
                        value(first.address.buildingToIntValue())
                    }
                    jsonPath("$._embedded.orders[0].items.length()") { value(1) }
                    jsonPath("$._embedded.orders[0].items[0].mealId") { value(firstItem.mealId.toLongValue()) }
                    jsonPath("$._embedded.orders[0].items[0].count") { value(firstItem.count.toIntValue()) }
                    jsonPath("$._embedded.orders[0]._links.self.href") { exists() }
                }
            }
        getOrders.verifyInvoked(first.id, limit + 1)
    }

    @Configuration
    @EnableHypermediaSupport(type = [EnableHypermediaSupport.HypermediaType.HAL_FORMS])
    class TestConfiguration {

        @Bean
        fun getOrdersEndpoint(getOrders: GetOrders) = GetOrdersEndpoint(getOrders)

        @Bean
        fun getOrders() = MockGetOrders()
    }
}