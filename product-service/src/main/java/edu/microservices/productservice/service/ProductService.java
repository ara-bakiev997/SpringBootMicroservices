package edu.microservices.productservice.service;

import edu.microservices.productservice.dto.ProductRequest;
import edu.microservices.productservice.dto.ProductResponse;
import edu.microservices.productservice.model.Product;
import edu.microservices.productservice.repository.ProductRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void createProduct(@Nonnull final ProductRequest productRequest) {
        final Product product = convertToProduct(productRequest);
        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    @Nonnull
    public List<ProductResponse> getAllProducts() {
        final List<Product> products = productRepository.findAll();

        return convertToProductResponses(products);
    }

    @Nonnull
    private Product convertToProduct(@Nonnull final ProductRequest productRequest) {
        return Product.builder()
                      .name(productRequest.getName())
                      .description(productRequest.getDescription())
                      .price(productRequest.getPrice())
                      .build();
    }

    @Nonnull
    private ProductResponse convertToProductResponse(@Nonnull final Product product) {
        return ProductResponse.builder()
                              .id(product.getId())
                              .name(product.getName())
                              .description(product.getDescription())
                              .price(product.getPrice())
                              .build();
    }

    @Nonnull
    private List<ProductResponse> convertToProductResponses(
            @Nonnull final List<Product> products
    ) {
        return products.stream().map(this::convertToProductResponse).collect(Collectors.toList());
    }

}
