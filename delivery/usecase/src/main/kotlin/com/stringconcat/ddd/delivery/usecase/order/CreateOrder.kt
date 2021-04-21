package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.common.types.common.Address

interface CreateOrder {
    fun execute(request: CreateOrderRequest)
}

data class CreateOrderRequest(
    val id: Long,
    val address: Address
)
