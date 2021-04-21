package com.stringconcat.dev.course.app.controllers.delivery

import com.stringconcat.ddd.common.types.common.Address
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId
import com.stringconcat.ddd.delivery.usecase.order.DeliverOrder
import com.stringconcat.ddd.delivery.usecase.order.DeliverOrderRequest
import com.stringconcat.ddd.delivery.usecase.order.DeliverOrderUseCaseError
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderInfo
import com.stringconcat.ddd.delivery.usecase.order.GetOrders
import com.stringconcat.dev.course.app.controllers.URLs
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class DeliveryOrderController(
    private val getOrders: GetOrders,
    private val deliverOrder: DeliverOrder
) {

    @GetMapping(URLs.delivery_orders)
    fun getDeliveryOrders(): ResponseEntity<List<DeliveryOrderView>> {
        return ResponseEntity(getOrders.execute().map(DeliveryOrderView::fromOrder), HttpStatus.OK)
    }

    @PostMapping(URLs.deliver_order)
    fun deliverOrder(@RequestParam orderId: Long): ResponseEntity<Unit> {
        deliverOrder.execute(DeliverOrderRequest(DeliveryOrderId(orderId))).mapLeft {
            return when (it) {
                is DeliverOrderUseCaseError.OrderNotFound -> ResponseEntity(HttpStatus.NOT_FOUND)
                is DeliverOrderUseCaseError.OrderAlreadyDelivered -> ResponseEntity(HttpStatus.BAD_REQUEST)
            }
        }

        return ResponseEntity(HttpStatus.OK)
    }
}

data class DeliveryOrderView(
    val orderId: Long,
    val address: AddressView,
    val delivered: Boolean
) {
    companion object {
        fun fromOrder(order: DeliveryOrderInfo) =
            DeliveryOrderView(
                orderId = order.id.value,
                address = AddressView.fromAddress(order.address),
                delivered = order.delivered
            )
    }
}

data class AddressView(
    val street: String,
    val building: Int
) {
    companion object {
        fun fromAddress(address: Address): AddressView =
            AddressView(
                street = address.street,
                building = address.building
            )
    }
}
