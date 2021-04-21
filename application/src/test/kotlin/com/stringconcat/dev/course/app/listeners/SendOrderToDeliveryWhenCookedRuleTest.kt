package com.stringconcat.dev.course.app.listeners

import com.stringconcat.ddd.delivery.usecase.order.CreateOrder
import com.stringconcat.ddd.delivery.usecase.order.CreateOrderRequest
import com.stringconcat.ddd.kitchen.domain.order.KitchenOrderCookedDomainEvent
import com.stringconcat.ddd.kitchen.domain.order.KitchenOrderId
import com.stringconcat.dev.course.app.TestCustomerOrderExtractor
import com.stringconcat.dev.course.app.customerOrder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class SendOrderToDeliveryWhenCookedRuleTest {

    @Test
    fun `should create delivery order when cooked`() {
        // Given
        val order = customerOrder()
        val createDeliveryOrderUseCase = TestCreateOrder()
        val customerOrderExtractor = TestCustomerOrderExtractor().apply {
            this[order.id] = order
        }

        val rule = SendOrderToDeliveryWhenCookedRule(
            customerOrderExtractor = customerOrderExtractor,
            createOrderUseCase = createDeliveryOrderUseCase
        )
        val event = KitchenOrderCookedDomainEvent(orderId = KitchenOrderId(order.id.value))

        // When
        rule.handle(event)

        // Then
        createDeliveryOrderUseCase.request shouldBe CreateOrderRequest(order.id.value, order.address)
    }

    @Test
    fun `should fail if the order is not found`() {
        // Given
        val createDeliveryOrderUseCase = TestCreateOrder()
        val customerOrderExtractor = TestCustomerOrderExtractor()

        val rule = SendOrderToDeliveryWhenCookedRule(
            customerOrderExtractor = customerOrderExtractor,
            createOrderUseCase = createDeliveryOrderUseCase
        )
        val event = KitchenOrderCookedDomainEvent(orderId = KitchenOrderId(42))

        // When
        shouldThrow<IllegalStateException> {
            rule.handle(event)
        }

        // Then
        createDeliveryOrderUseCase.verifyZeroInteraction()
    }

    private class TestCreateOrder : CreateOrder {

        lateinit var request: CreateOrderRequest

        override fun execute(request: CreateOrderRequest) {
            this.request = request
        }

        fun verifyZeroInteraction() {
            ::request.isInitialized.shouldBeFalse()
        }
    }
}
