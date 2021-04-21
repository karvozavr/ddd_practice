package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.common.types.base.Version
import com.stringconcat.ddd.common.types.common.Address
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrder
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlin.random.Random

fun orderId() = DeliveryOrderId(Random.nextLong())

fun version() = Version.new()

fun address() = Address.from("Chaussée de Namur", 24)
    .orNull()
    .shouldNotBeNull()

fun order(address: Address = address()): DeliveryOrder {
    return DeliveryOrder.create(
        id = orderId(),
        deliveryAddress = address
    ).also {
        it.popEvents()
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
