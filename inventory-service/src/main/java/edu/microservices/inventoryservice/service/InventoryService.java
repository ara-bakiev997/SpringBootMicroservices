package edu.microservices.inventoryservice.service;

import edu.microservices.inventoryservice.repository.InventoryRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean isInStock(@Nonnull final String skuCode) {
        return inventoryRepository.findBySkuCode(skuCode).isPresent();
    }
}
