package edu.microservices.productservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.assertions.Assertions;
import edu.microservices.productservice.dto.ProductRequest;
import edu.microservices.productservice.dto.ProductResponse;
import edu.microservices.productservice.repository.ProductRepository;
import edu.microservices.productservice.service.ProductService;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @DynamicPropertySource
    static void setProperties(final DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        final ProductRequest productRequest = getProductRequest();
        final String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestString)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertTrue(productRepository.findAll().size() == 1);
    }

    @Test
    void getAllProductsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/product"))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(result -> {
                   final List<ProductResponse> productResponses = objectMapper.readValue(
                           result.getResponse().getContentAsString(),
                           new TypeReference<>() {
                           }
                   );
                   final List<ProductResponse> allProductsFromDB = productService.getAllProducts();

                   Assertions.assertTrue(productResponses.size() == allProductsFromDB.size());
                   Assertions.assertTrue(allProductsFromDB.equals(productResponses));
               });
    }


    @Nonnull
    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                             .name("Iphone 13")
                             .description("Iphone 13")
                             .price(BigDecimal.valueOf(1200))
                             .build();
    }
}
