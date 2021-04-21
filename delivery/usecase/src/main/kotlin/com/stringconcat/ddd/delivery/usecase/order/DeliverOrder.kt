package com.stringconcat.ddd.delivery.usecase.order

import arrow.core.Either
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId

interface DeliverOrder {
    fun execute(request: DeliverOrderRequest): Either<DeliverOrderUseCaseError, Unit>
}

data class DeliverOrderRequest(val orderId: DeliveryOrderId)

sealed class DeliverOrderUseCaseError(val message: String) {
    object OrderNotFound : DeliverOrderUseCaseError("Order not found")
    object OrderAlreadyDelivered : DeliverOrderUseCaseError("Order has been already delivered")
}