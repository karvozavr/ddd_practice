package com.stringconcat.dev.course.app.configuration

import com.stringconcat.ddd.common.types.base.EventPublisher
import com.stringconcat.ddd.delivery.persistence.order.InMemoryDeliveryOrderRepository
import com.stringconcat.ddd.delivery.usecase.order.CreateOrderHandler
import com.stringconcat.ddd.delivery.usecase.order.DeliverOrderHandler
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderExtractor
import com.stringconcat.ddd.delivery.usecase.order.DeliveryOrderPersister
import com.stringconcat.ddd.delivery.usecase.order.GetOrdersHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DeliveryContextConfiguration {

    @Bean
    fun deliveryOrderRepository(eventPublisher: EventPublisher) = InMemoryDeliveryOrderRepository(eventPublisher)

    @Bean
    fun deliverOrderUseCase(
        deliveryOrderExtractor: DeliveryOrderExtractor,
        deliveryOrderPersister: DeliveryOrderPersister
    ) = DeliverOrderHandler(deliveryOrderExtractor, deliveryOrderPersister)

    @Bean
    fun createDeliveryOrderHandler(
        deliveryOrderExtractor: DeliveryOrderExtractor,
        deliveryOrderPersister: DeliveryOrderPersister
    ) = CreateOrderHandler(deliveryOrderExtractor, deliveryOrderPersister)

    @Bean
    fun getDeliveryOrdersUseCase(deliveryOrderExtractor: DeliveryOrderExtractor) =
        GetOrdersHandler(deliveryOrderExtractor)
}
