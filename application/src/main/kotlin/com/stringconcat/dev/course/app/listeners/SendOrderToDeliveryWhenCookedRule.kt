package com.stringconcat.dev.course.app.listeners

import com.stringconcat.ddd.delivery.usecase.order.CreateOrder
import com.stringconcat.ddd.delivery.usecase.order.CreateOrderRequest
import com.stringconcat.ddd.kitchen.domain.order.KitchenOrderCookedDomainEvent
import com.stringconcat.ddd.order.domain.order.CustomerOrderId
import com.stringconcat.ddd.order.usecase.order.CustomerOrderExtractor
import com.stringconcat.dev.course.app.event.DomainEventListener
import kotlin.reflect.KClass

class SendOrderToDeliveryWhenCookedRule(
    private val customerOrderExtractor: CustomerOrderExtractor,
    private val createOrderUseCase: CreateOrder
) : DomainEventListener<KitchenOrderCookedDomainEvent> {

    override fun eventType(): KClass<KitchenOrderCookedDomainEvent> {
        return KitchenOrderCookedDomainEvent::class
    }

    override fun handle(event: KitchenOrderCookedDomainEvent) {
        val customerOrderId = CustomerOrderId(event.orderId.value)
        val customerOrder = customerOrderExtractor.getById(orderId = customerOrderId)
        checkNotNull(customerOrder) {
            "Customer order #${event.orderId} not found"
        }

        val request = CreateOrderRequest(customerOrderId.value, customerOrder.address)
        createOrderUseCase.execute(request)
    }
}
