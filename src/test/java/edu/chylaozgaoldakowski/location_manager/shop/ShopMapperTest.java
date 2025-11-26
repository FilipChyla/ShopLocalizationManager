package edu.chylaozgaoldakowski.location_manager.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ShopMapper Unit Tests")
class ShopMapperTest {

    private ShopMapper shopMapper;

    @BeforeEach
    void setUp() {
        shopMapper = new ShopMapper();
    }

    @Test
    @DisplayName("toDto should correctly map Shop entity to ShopDto")
    void testToDto() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setAddress("123 Test Street");
        shop.setCity("Test City");

        // Act
        ShopDto result = shopMapper.toDto(shop);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Shop");
        assertThat(result.getAddress()).isEqualTo("123 Test Street");
        assertThat(result.getCity()).isEqualTo("Test City");
    }

    @Test
    @DisplayName("toDto should handle shop with null id")
    void testToDtoWithNullId() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(null);
        shop.setName("New Shop");
        shop.setAddress("456 New Street");
        shop.setCity("New City");

        // Act
        ShopDto result = shopMapper.toDto(shop);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("New Shop");
        assertThat(result.getAddress()).isEqualTo("456 New Street");
        assertThat(result.getCity()).isEqualTo("New City");
    }

    @Test
    @DisplayName("updateEntityFromDto should update all fields except id")
    void testUpdateEntityFromDto() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Old Name");
        shop.setAddress("Old Address");
        shop.setCity("Old City");

        ShopDto dto = new ShopDto();
        dto.setId(999L); // This should not update the entity's id
        dto.setName("New Name");
        dto.setAddress("New Address");
        dto.setCity("New City");

        // Act
        shopMapper.updateEntityFromDto(shop, dto);

        // Assert
        assertThat(shop.getId()).isEqualTo(1L); // ID should not change
        assertThat(shop.getName()).isEqualTo("New Name");
        assertThat(shop.getAddress()).isEqualTo("New Address");
        assertThat(shop.getCity()).isEqualTo("New City");
    }

    @Test
    @DisplayName("updateEntityFromDto should handle empty strings")
    void testUpdateEntityFromDtoWithEmptyStrings() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Original Name");
        shop.setAddress("Original Address");
        shop.setCity("Original City");

        ShopDto dto = new ShopDto();
        dto.setName("");
        dto.setAddress("");
        dto.setCity("");

        // Act
        shopMapper.updateEntityFromDto(shop, dto);

        // Assert
        assertThat(shop.getName()).isEmpty();
        assertThat(shop.getAddress()).isEmpty();
        assertThat(shop.getCity()).isEmpty();
    }

    @Test
    @DisplayName("updateEntityFromDto should update entity with null id")
    void testUpdateEntityFromDtoWithNullEntityId() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(null);
        shop.setName("Old Name");
        shop.setAddress("Old Address");
        shop.setCity("Old City");

        ShopDto dto = new ShopDto();
        dto.setId(1L);
        dto.setName("New Name");
        dto.setAddress("New Address");
        dto.setCity("New City");

        // Act
        shopMapper.updateEntityFromDto(shop, dto);

        // Assert
        assertThat(shop.getId()).isNull(); // Should remain null
        assertThat(shop.getName()).isEqualTo("New Name");
        assertThat(shop.getAddress()).isEqualTo("New Address");
        assertThat(shop.getCity()).isEqualTo("New City");
    }
}
