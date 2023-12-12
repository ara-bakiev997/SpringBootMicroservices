package edu.microservices.productservice.service;

import edu.microservices.productservice.dto.ProductRequest;
import edu.microservices.productservice.model.Product;
import edu.microservices.productservice.repository.ProductRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void createProduct(@Nonnull final ProductRequest productRequest) {

    }

    @Nonnull
    private Product convertToProduct(@Nonnull final ProductRequest productRequest) {
        return null;
    }
}
