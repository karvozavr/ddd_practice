package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderCreatedDomainEvent
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class CreateOrderHandlerTest {

    @Test
    fun `order doesn't exist - order created successfully`() {

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()

        val handler = CreateOrderHandler(extractor, persister)

        val orderId = orderId()
        val address = address()

        val request = CreateOrderRequest(
            id = orderId.value,
            address = address
        )

        handler.execute(request)

        val order = persister[orderId]
        order.shouldNotBeNull()
        order.id shouldBe orderId
        order.deliveryAddress shouldBe address
        order.popEvents() shouldContainExactly listOf(DeliveryOrderCreatedDomainEvent(orderId))
    }

    @Test
    fun `order exists - order not created`() {

        val address = address()
        val existingOrder = order(address)

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor().apply {
            this[existingOrder.id] = existingOrder
        }

        val handler = CreateOrderHandler(extractor, persister)

        val request = CreateOrderRequest(
            id = existingOrder.id.value,
            address = address
        )

        handler.execute(request)

        val order = persister[existingOrder.id]
        order.shouldBeNull()
    }
}
