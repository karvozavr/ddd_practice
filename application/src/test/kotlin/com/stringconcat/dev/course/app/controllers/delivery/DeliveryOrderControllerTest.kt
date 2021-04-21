package com.stringconcat.dev.course.app.controllers.delivery

import com.stringconcat.ddd.common.types.base.Version
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderRestorer
import com.stringconcat.ddd.delivery.persistence.order.InMemoryDeliveryOrderRepository
import com.stringconcat.dev.course.app.address
import com.stringconcat.dev.course.app.controllers.URLs
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(controllers = [DeliveryOrderController::class])
class DeliveryOrderControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var orderRepository: InMemoryDeliveryOrderRepository

    @Test
    fun `should get existing delivery orders`() {
        val order = DeliveryOrderRestorer.restoreOrder(DeliveryOrderId(42), address(), false, Version.new())
        orderRepository.save(order)

        val result = mockMvc.get(URLs.delivery_orders)
            .andExpect { status { is2xxSuccessful() } }
            .andReturn()

        val address = order.deliveryAddress
        val addressJSON = """{"street":"${address.street}","building":${address.building}}"""
        result.response.contentAsString shouldBe """[{"orderId":${order.id.value},""" +
                """"address":$addressJSON,""" +
                """"delivered":false}]"""
    }

    @Test
    fun `should deliver the order`() {
        val order = DeliveryOrderRestorer.restoreOrder(DeliveryOrderId(43), address(), false, Version.new())
        orderRepository.save(order)

        mockMvc.post(URLs.deliver_order + "?orderId=43")
            .andExpect { status { is2xxSuccessful() } }
            .andReturn()

        orderRepository.getById(DeliveryOrderId(43))?.delivered shouldBe true
    }

    @Test
    fun `should return error for non-existing order`() {
        mockMvc.post(URLs.deliver_order + "?orderId=1")
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `should return error for already delivered order`() {
        val order = DeliveryOrderRestorer.restoreOrder(DeliveryOrderId(1), address(), true, Version.new())
        orderRepository.save(order)

        mockMvc.post(URLs.deliver_order + "?orderId=1")
            .andExpect { status { isBadRequest() } }

        orderRepository.getById(DeliveryOrderId(1))?.delivered shouldBe true
    }
}
