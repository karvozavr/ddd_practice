package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrder
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId

class CreateOrderHandler(
    private val orderExtractor: DeliveryOrderExtractor,
    private val orderPersister: DeliveryOrderPersister
) : CreateOrder {

    override fun execute(request: CreateOrderRequest) {
        val orderId = DeliveryOrderId(request.id)
        if (orderExtractor.getById(orderId) == null) {
            createNewOrder(request)
        }
    }

    private fun createNewOrder(request: CreateOrderRequest) {
        DeliveryOrder.create(
            id = DeliveryOrderId(request.id),
            deliveryAddress = request.address
        ).also { order ->
            orderPersister.save(order)
        }
    }
}
