package com.stringconcat.ddd.delivery.persistence.order

import arrow.core.Either
import com.stringconcat.ddd.common.types.base.DomainEvent
import com.stringconcat.ddd.common.types.base.EventPublisher
import com.stringconcat.ddd.common.types.base.Version
import com.stringconcat.ddd.common.types.common.Address
import com.stringconcat.ddd.common.types.common.Count
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrder
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderExtractor
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderPersister
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlin.random.Random

fun orderId() = DeliveryOrderId(Random.nextLong())

fun version() = Version.new()

fun address() = Address.from("Chaussée de Namur", 24)
    .orNull()
    .shouldNotBeNull()

fun count(value: Int = Random.nextInt(20, 5000)): Count {
    val result = Count.from(value)
    check(result is Either.Right<Count>)
    return result.b
}

fun order(id: DeliveryOrderId = orderId(), address: Address = address()): DeliveryOrder {
    return DeliveryOrder.create(
        id = id,
        deliveryAddress = address
    ).also {
        it.popEvents()
    }
}

fun orderWithEvents(id: DeliveryOrderId = orderId()): DeliveryOrder {
    return order(id).apply {
        deliver()
    }
}

class TestDeliveryOrderPersister : DeliveryOrderPersister, HashMap<DeliveryOrderId, DeliveryOrder>() {

    override fun save(order: DeliveryOrder) {
        this[order.id] = order
    }
}

class TestDeliveryOrderExtractor : DeliveryOrderExtractor, HashMap<DeliveryOrderId, DeliveryOrder>() {

    override fun getById(orderId: DeliveryOrderId): DeliveryOrder? {
        return this[orderId]
    }

    override fun getAll(): List<DeliveryOrder> {
        return values.toList()
    }
}

class TestEventPublisher : EventPublisher {
    internal val storage = ArrayList<DomainEvent>()
    override fun publish(events: Collection<DomainEvent>) {
        storage.addAll(events)
    }
}
