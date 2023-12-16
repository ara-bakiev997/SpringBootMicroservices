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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public void placeOrder(
            @Nonnull final OrderRequest orderRequest
    ) {
        final Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        final List<OrderLineItem> orderLineItems = mapToOrderLineItem(orderRequest.getOrderLineItemDTOList());

        order.setOrderLineItems(orderLineItems);

        orderRepository.save(order);
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
