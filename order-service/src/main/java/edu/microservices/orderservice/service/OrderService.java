package edu.microservices.orderservice.service;

import edu.microservices.orderservice.dto.InventoryResponse;
import edu.microservices.orderservice.dto.OrderLineItemDTO;
import edu.microservices.orderservice.dto.OrderRequest;
import edu.microservices.orderservice.event.OrderPlacedEvent;
import edu.microservices.orderservice.model.Order;
import edu.microservices.orderservice.model.OrderLineItem;
import edu.microservices.orderservice.repository.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientInventoryServiceBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Nonnull
    @Transactional
    public String placeOrder(
            @Nonnull final OrderRequest orderRequest
    ) {
        final Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        final List<OrderLineItem> orderLineItems = mapToOrderLineItem(orderRequest.getOrderLineItemDTOList());

        order.setOrderLineItems(orderLineItems);

        final List<String> skuCodes = order
                .getOrderLineItems()
                .stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        /* call inventory service, and place order if product is in stock */
        final boolean allProductsIsInStock = isAllOrderLineItemsInStock(skuCodes);

        if (allProductsIsInStock) {
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));

            return "Order Placed Successfully";
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }


    private boolean isAllOrderLineItemsInStock(@Nonnull final List<String> skuCodes) {
        log.info("Calling inventory service");

        final Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

        try (final Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {
            final List<InventoryResponse> inventories = webClientInventoryServiceBuilder
                    .build()
                    .get()
                    .uri(
                            "/api/inventory",
                            uriBuilder -> uriBuilder
                                    .queryParam(
                                            "skuCode",
                                            skuCodes
                                    )
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<InventoryResponse>>() {
                    })
                    .block();

            return Optional.ofNullable(inventories)
                           .map(list ->
                                   !list.isEmpty() && list.stream().allMatch(InventoryResponse::isInStock)
                           )
                           .orElse(false);

        } finally {
            inventoryServiceLookup.end();
        }
    }

    private List<OrderLineItem> mapToOrderLineItem(
            @Nonnull final List<OrderLineItemDTO> orderLineItemDTOList
    ) {
        return orderLineItemDTOList.stream().map(this::mapToOrderLineItem).collect(Collectors.toList());
    }

    @Nonnull
    private OrderLineItem mapToOrderLineItem(@Nonnull final OrderLineItemDTO orderLineItemDTO) {
        return OrderLineItem.builder()
                            .skuCode(orderLineItemDTO.getSkuCode())
                            .price(orderLineItemDTO.getPrice())
                            .quantity(orderLineItemDTO.getQuantity())
                            .build();
    }

}
