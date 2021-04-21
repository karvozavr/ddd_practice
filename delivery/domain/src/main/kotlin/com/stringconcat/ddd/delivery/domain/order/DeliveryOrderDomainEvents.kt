package com.stringconcat.ddd.delivery.domain.order

import com.stringconcat.ddd.common.types.base.DomainEvent

data class DeliveryOrderCreatedDomainEvent(val orderId: DeliveryOrderId) : DomainEvent()

data class DeliveryOrderDeliveredDomainEvent(val orderId: DeliveryOrderId) : DomainEvent()
