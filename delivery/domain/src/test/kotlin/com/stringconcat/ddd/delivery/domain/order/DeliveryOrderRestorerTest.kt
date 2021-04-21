package com.stringconcat.ddd.delivery.domain.order

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class DeliveryOrderRestorerTest {

    @Test
    fun `should restore the order`() {
        val id = orderId()
        val address = address()
        val delivered = true
        val version = version()

        val order = DeliveryOrderRestorer.restoreOrder(
            id = id,
            address = address,
            delivered = delivered,
            version = version
        )

        order.id shouldBe id
        order.deliveryAddress shouldBe address
        order.delivered shouldBe true
        order.version shouldBe version
        order.popEvents().shouldBeEmpty()
    }
}