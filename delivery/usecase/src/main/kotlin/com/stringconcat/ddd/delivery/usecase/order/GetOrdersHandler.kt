package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrder

class GetOrdersHandler(
    private val orderExtractor: DeliveryOrderExtractor
) : GetOrders {

    override fun execute(): List<DeliveryOrderInfo> {
        return orderExtractor.getAll().map(this::toOrderInfo)
    }

    private fun toOrderInfo(deliveryOrder: DeliveryOrder) =
        DeliveryOrderInfo(
            id = deliveryOrder.id,
            address = deliveryOrder.deliveryAddress,
            delivered = deliveryOrder.delivered
        )
}
