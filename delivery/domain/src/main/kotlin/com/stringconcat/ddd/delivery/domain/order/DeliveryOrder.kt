package com.stringconcat.ddd.delivery.domain.order

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.stringconcat.ddd.common.types.base.AggregateRoot
import com.stringconcat.ddd.common.types.base.Version
import com.stringconcat.ddd.common.types.common.Address
import com.stringconcat.ddd.common.types.error.BusinessError

data class DeliveryOrderId(val value: Long)

class DeliveryOrder internal constructor(
    id: DeliveryOrderId,
    version: Version,
    val deliveryAddress: Address
) : AggregateRoot<DeliveryOrderId>(id, version) {

    var delivered: Boolean = false
        internal set

    companion object {
        fun create(
            id: DeliveryOrderId,
            deliveryAddress: Address
        ): DeliveryOrder {
            return DeliveryOrder(
                id = id,
                version = Version.new(),
                deliveryAddress = deliveryAddress
            ).apply {
                addEvent(DeliveryOrderCreatedDomainEvent(id))
            }
        }
    }

    fun deliver(): Either<OrderAlreadyDelivered, Unit> {
        return if (!delivered) {
            delivered = true
            addEvent(DeliveryOrderDeliveredDomainEvent(id))
            Unit.right()
        } else {
            OrderAlreadyDelivered.left()
        }
    }
}

object OrderAlreadyDelivered : BusinessError
