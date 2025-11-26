package edu.chylaozgaoldakowski.location_manager.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductMapper Unit Tests")
class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();
    }

    @Test
    @DisplayName("toProductDetailsDto should correctly map Product entity to ProductDto")
    void testToProductDetailsDto() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setManufacturer("Test Manufacturer");
        product.setCategory(Category.FRESH_PRODUCE);
        product.setProductCode("PROD-001");
        product.setDescription("A test fresh produce product");
        product.setPrice(new BigDecimal("299.99"));

        // Act
        ProductDto result = productMapper.toProductDetailsDto(product);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getManufacturer()).isEqualTo("Test Manufacturer");
        assertThat(result.getCategory()).isEqualTo(Category.FRESH_PRODUCE);
        assertThat(result.getProductCode()).isEqualTo("PROD-001");
        assertThat(result.getDescription()).isEqualTo("A test fresh produce product");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("299.99"));
    }

    @Test
    @DisplayName("toProductDetailsDto should handle product with null id")
    void testToProductDetailsDtoWithNullId() {
        // Arrange
        Product product = new Product();
        product.setId(null);
        product.setName("New Product");
        product.setManufacturer("New Manufacturer");
        product.setCategory(Category.DAIRY_PRODUCTS);
        product.setProductCode("DAIRY-001");
        product.setDescription("New dairy product");
        product.setPrice(new BigDecimal("49.99"));

        // Act
        ProductDto result = productMapper.toProductDetailsDto(product);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("New Product");
        assertThat(result.getManufacturer()).isEqualTo("New Manufacturer");
        assertThat(result.getCategory()).isEqualTo(Category.DAIRY_PRODUCTS);
        assertThat(result.getProductCode()).isEqualTo("DAIRY-001");
        assertThat(result.getDescription()).isEqualTo("New dairy product");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("49.99"));
    }

    @Test
    @DisplayName("toProductDetailsDto should handle null description")
    void testToProductDetailsDtoWithNullDescription() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setManufacturer("Manufacturer");
        product.setCategory(Category.BEVERAGES);
        product.setProductCode("BEV-001");
        product.setDescription(null);
        product.setPrice(new BigDecimal("9.99"));

        // Act
        ProductDto result = productMapper.toProductDetailsDto(product);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isNull();
    }

    @Test
    @DisplayName("updateEntityFromDto should update all fields except id")
    void testUpdateEntityFromDto() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Old Name");
        product.setManufacturer("Old Manufacturer");
        product.setCategory(Category.FRESH_PRODUCE);
        product.setProductCode("OLD-001");
        product.setDescription("Old description");
        product.setPrice(new BigDecimal("100.00"));

        ProductDto dto = new ProductDto();
        dto.setId(999L); // This should not update the entity's id
        dto.setName("New Name");
        dto.setManufacturer("New Manufacturer");
        dto.setCategory(Category.BAKERY_PRODUCTS);
        dto.setProductCode("NEW-001");
        dto.setDescription("New description");
        dto.setPrice(new BigDecimal("200.00"));

        // Act
        productMapper.updateEntityFromDto(product, dto);

        // Assert
        assertThat(product.getId()).isEqualTo(1L); // ID should not change
        assertThat(product.getName()).isEqualTo("New Name");
        assertThat(product.getManufacturer()).isEqualTo("New Manufacturer");
        assertThat(product.getCategory()).isEqualTo(Category.BAKERY_PRODUCTS);
        assertThat(product.getProductCode()).isEqualTo("NEW-001");
        assertThat(product.getDescription()).isEqualTo("New description");
        assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("updateEntityFromDto should handle null description in DTO")
    void testUpdateEntityFromDtoWithNullDescription() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setManufacturer("Manufacturer");
        product.setCategory(Category.FRESH_PRODUCE);
        product.setProductCode("PROD-001");
        product.setDescription("Original description");
        product.setPrice(new BigDecimal("50.00"));

        ProductDto dto = new ProductDto();
        dto.setName("Updated Product");
        dto.setManufacturer("Updated Manufacturer");
        dto.setCategory(Category.SNACKS_AND_CONFECTIONERY);
        dto.setProductCode("PROD-002");
        dto.setDescription(null);
        dto.setPrice(new BigDecimal("75.00"));

        // Act
        productMapper.updateEntityFromDto(product, dto);

        // Assert
        assertThat(product.getDescription()).isNull();
        assertThat(product.getName()).isEqualTo("Updated Product");
        assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("75.00"));
    }

    @Test
    @DisplayName("updateEntityFromDto should handle all product categories")
    void testUpdateEntityFromDtoWithDifferentCategories() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setManufacturer("Manufacturer");
        product.setProductCode("PROD-001");
        product.setPrice(new BigDecimal("10.00"));

        ProductDto dto = new ProductDto();
        dto.setName("Product");
        dto.setManufacturer("Manufacturer");
        dto.setProductCode("PROD-001");
        dto.setPrice(new BigDecimal("10.00"));

        // Test each category
        for (Category category : Category.values()) {
            dto.setCategory(category);
            productMapper.updateEntityFromDto(product, dto);
            assertThat(product.getCategory()).isEqualTo(category);
        }
    }

    @Test
    @DisplayName("updateEntityFromDto should update product with null id")
    void testUpdateEntityFromDtoWithNullEntityId() {
        // Arrange
        Product product = new Product();
        product.setId(null);
        product.setName("Old Name");
        product.setManufacturer("Old Manufacturer");
        product.setCategory(Category.FRESH_PRODUCE);
        product.setProductCode("OLD-001");
        product.setDescription("Old description");
        product.setPrice(new BigDecimal("100.00"));

        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setName("New Name");
        dto.setManufacturer("New Manufacturer");
        dto.setCategory(Category.BAKERY_PRODUCTS);
        dto.setProductCode("NEW-001");
        dto.setDescription("New description");
        dto.setPrice(new BigDecimal("200.00"));

        // Act
        productMapper.updateEntityFromDto(product, dto);

        // Assert
        assertThat(product.getId()).isNull(); // Should remain null
        assertThat(product.getName()).isEqualTo("New Name");
        assertThat(product.getManufacturer()).isEqualTo("New Manufacturer");
        assertThat(product.getCategory()).isEqualTo(Category.BAKERY_PRODUCTS);
        assertThat(product.getProductCode()).isEqualTo("NEW-001");
        assertThat(product.getDescription()).isEqualTo("New description");
        assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("200.00"));
    }
}
