package edu.microservices.inventoryservice.service;

import edu.microservices.inventoryservice.dto.InventoryResponse;
import edu.microservices.inventoryservice.model.Inventory;
import edu.microservices.inventoryservice.repository.InventoryRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Nonnull
    @Transactional(readOnly = true)
    @SneakyThrows // don't use in production
    public List<InventoryResponse> getInventoryResponseBySkuCode(@Nonnull final List<String> skuCodes) {
        log.info("Wait Started");
        Thread.sleep(10000);
        log.info("Wait Ended");

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
