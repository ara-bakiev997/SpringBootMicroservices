package edu.microservices.inventoryservice.repository;

import edu.microservices.inventoryservice.model.Inventory;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findBySkuCode(@Nonnull final String skuCode);
}
