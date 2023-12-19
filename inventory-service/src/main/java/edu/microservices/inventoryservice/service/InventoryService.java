package edu.microservices.inventoryservice.service;

import edu.microservices.inventoryservice.dto.InventoryResponse;
import edu.microservices.inventoryservice.model.Inventory;
import edu.microservices.inventoryservice.repository.InventoryRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @Nonnull
    public List<InventoryResponse> getInventoryResponseBySkuCode(@Nonnull final List<String> skuCodes) {
        return convertToResponse(inventoryRepository.findBySkuCodeIn(skuCodes));
    }

    @Nonnull
    private List<InventoryResponse> convertToResponse(@Nonnull final List<Inventory> inventories) {
        return inventories.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Nonnull
    private InventoryResponse convertToResponse(@Nonnull final Inventory inventory) {
        return InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build();
    }
}
