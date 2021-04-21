package com.stringconcat.ddd.delivery.domain.order

import arrow.core.Either
import com.stringconcat.ddd.common.types.base.Version
import com.stringconcat.ddd.common.types.common.Address
import com.stringconcat.ddd.common.types.common.Count
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

fun order(address: Address): DeliveryOrder {
    return DeliveryOrder.create(
        id = orderId(),
        deliveryAddress = address
    )
}
