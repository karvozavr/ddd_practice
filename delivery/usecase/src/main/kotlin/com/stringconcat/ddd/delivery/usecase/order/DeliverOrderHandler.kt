package com.stringconcat.ddd.delivery.usecase.order

import arrow.core.Either
import arrow.core.left

class DeliverOrderHandler(
    private val orderExtractor: DeliveryOrderExtractor,
    private val orderPersister: DeliveryOrderPersister
) : DeliverOrder {

    override fun execute(request: DeliverOrderRequest): Either<DeliverOrderUseCaseError, Unit> {
        val order = orderExtractor.getById(request.orderId) ?: return DeliverOrderUseCaseError.OrderNotFound.left()
        return order.deliver().map {
            orderPersister.save(order)
        }.mapLeft {
            DeliverOrderUseCaseError.OrderAlreadyDelivered
        }
    }
}
