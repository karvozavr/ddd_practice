package com.stringconcat.dev.course.app.listeners

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderDeliveredDomainEvent
import com.stringconcat.ddd.order.domain.order.CustomerOrderId
import com.stringconcat.ddd.order.usecase.order.CompleteOrder
import com.stringconcat.ddd.order.usecase.order.CompleteOrderUseCaseError
import com.stringconcat.dev.course.app.deliveryOrder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class CompleteOrderWhenDeliveredRuleTest {

    @Test
    fun `should complete the order when it's delivered`() {
        // Given
        val deliveryOrder = deliveryOrder()
        val completeOrderUseCase = TestCompleteOrder()

        val rule = CompleteOrderWhenDeliveredRule(
            completeOrderUseCase = completeOrderUseCase
        )
        val event = DeliveryOrderDeliveredDomainEvent(deliveryOrder.id)

        // When
        rule.handle(event)

        // Then
        completeOrderUseCase.orderId shouldBe CustomerOrderId(deliveryOrder.id.value)
    }

    @Test
    fun `should fail if CompleteOrder use case fails`() {
        // Given
        val deliveryOrder = deliveryOrder()
        val completeOrderUseCase = TestCompleteOrder(shouldFail = true)

        val rule = CompleteOrderWhenDeliveredRule(
            completeOrderUseCase = completeOrderUseCase
        )
        val event = DeliveryOrderDeliveredDomainEvent(deliveryOrder.id)

        // When
        shouldThrow<IllegalStateException> {
            rule.handle(event)
        }
    }

    private class TestCompleteOrder(private val shouldFail: Boolean = false) : CompleteOrder {

        var orderId: CustomerOrderId? = null

        override fun execute(orderId: CustomerOrderId): Either<CompleteOrderUseCaseError, Unit> {
            this.orderId = orderId
            return if (shouldFail) CompleteOrderUseCaseError.InvalidOrderState.left() else Unit.right()
        }
    }
}