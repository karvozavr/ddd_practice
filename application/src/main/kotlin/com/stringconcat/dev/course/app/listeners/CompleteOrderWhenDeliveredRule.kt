package com.stringconcat.dev.course.app.listeners

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderDeliveredDomainEvent
import com.stringconcat.ddd.order.domain.order.CustomerOrderId
import com.stringconcat.ddd.order.usecase.order.CompleteOrder
import com.stringconcat.dev.course.app.event.DomainEventListener
import kotlin.reflect.KClass

class CompleteOrderWhenDeliveredRule(
    private val completeOrderUseCase: CompleteOrder
) : DomainEventListener<DeliveryOrderDeliveredDomainEvent> {

    override fun eventType(): KClass<DeliveryOrderDeliveredDomainEvent> {
        return DeliveryOrderDeliveredDomainEvent::class
    }

    override fun handle(event: DeliveryOrderDeliveredDomainEvent) {
        val customerOrderId = CustomerOrderId(event.orderId.value)
        completeOrderUseCase.execute(customerOrderId).mapLeft {
            throw error("Failed to complete the order with error: $it")
        }
    }
}
