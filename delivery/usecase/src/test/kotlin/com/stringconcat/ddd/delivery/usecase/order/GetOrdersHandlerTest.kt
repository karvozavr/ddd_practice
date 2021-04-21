package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.common.types.common.Address
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class GetOrdersHandlerTest {

    @Test
    fun `should return orders`() {
        // Given
        val regentStreet = Address.from("Regent Street", 42).orNull().shouldNotBeNull()
        val liverpoolStreet = Address.from("Liverpool Street", 12).orNull().shouldNotBeNull()

        val order1 = order(address = regentStreet)
        val order2 = order(address = liverpoolStreet).apply { deliver(); popEvents() }

        val orderExtractor = TestDeliveryOrderExtractor().also {
            it[order1.id] = order1
            it[order2.id] = order2
        }

        val handler = GetOrdersHandler(orderExtractor = orderExtractor)

        // When
        val result = handler.execute()

        // Then
        result shouldContainAll listOf(
            DeliveryOrderInfo(order1.id, regentStreet, false),
            DeliveryOrderInfo(order2.id, liverpoolStreet, true)
        )
    }
}
