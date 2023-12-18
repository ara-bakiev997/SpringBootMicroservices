package edu.microservices.orderservice.service;

import edu.microservices.orderservice.dto.OrderLineItemDTO;
import edu.microservices.orderservice.dto.OrderRequest;
import edu.microservices.orderservice.model.Order;
import edu.microservices.orderservice.model.OrderLineItem;
import edu.microservices.orderservice.repository.OrderRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient webClientInventoryService;

    @Transactional
    public void placeOrder(
            @Nonnull final OrderRequest orderRequest
    ) {
        final Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        final List<OrderLineItem> orderLineItems = mapToOrderLineItem(orderRequest.getOrderLineItemDTOList());

        order.setOrderLineItems(orderLineItems);

        /* call inventory service, and place order if product is in stock */
        final String skuCode = "iphone_131";
        final Boolean isInStock = webClientInventoryService
                .get()
                .uri("api/inventory/{skuCode}", skuCode)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (Boolean.TRUE.equals(isInStock)) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
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
