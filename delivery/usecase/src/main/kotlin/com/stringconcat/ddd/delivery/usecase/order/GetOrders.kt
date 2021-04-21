package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.common.types.common.Address
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId

interface GetOrders {
    fun execute(): List<DeliveryOrderInfo>
}

data class DeliveryOrderInfo(
    val id: DeliveryOrderId,
    val address: Address,
    val delivered: Boolean
)
