package edu.chylaozgaoldakowski.location_manager.shop;

import edu.chylaozgaoldakowski.location_manager.entry.Entry;
import edu.chylaozgaoldakowski.location_manager.product.Category;
import edu.chylaozgaoldakowski.location_manager.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

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

    @Test
    @DisplayName("toShopData should correctly map Shop and Entries to ShopData")
    void testToShopData() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setAddress("123 Test Street");
        shop.setCity("Test City");

        Product product = new Product(1L, "Milk", "Dairy Co", Category.DAIRY_PRODUCTS,
                "MILK001", "Fresh milk", BigDecimal.valueOf(5.99), List.of());
        Entry entry = new Entry(1L, shop, product, 2, BigDecimal.valueOf(11.98));
        List<Entry> entries = List.of(entry);

        // Act
        ShopData result = shopMapper.toShopData(shop, entries);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Shop");
        assertThat(result.getAddress()).isEqualTo("123 Test Street");
        assertThat(result.getCity()).isEqualTo("Test City");
        assertThat(result.getEntries()).hasSize(1);
    }

    @Test
    @DisplayName("toShopData should correctly map Entry data")
    void testToShopDataEntryMapping() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setAddress("123 Test Street");
        shop.setCity("Test City");

        Product product = new Product(1L, "Milk", "Dairy Co", Category.DAIRY_PRODUCTS,
                "MILK001", "Fresh milk", BigDecimal.valueOf(5.99), List.of());
        Entry entry = new Entry(1L, shop, product, 2, BigDecimal.valueOf(11.98));
        List<Entry> entries = List.of(entry);

        // Act
        ShopData result = shopMapper.toShopData(shop, entries);

        // Assert
        ShopData.EntryData entryData = result.getEntries().getFirst();
        assertThat(entryData).isNotNull();
        assertThat(entryData.getAmount()).isEqualTo(2);
        assertThat(entryData.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(11.98));
        assertThat(entryData.getProduct()).isNotNull();
    }

    @Test
    @DisplayName("toShopData should correctly map Product data")
    void testToShopDataProductMapping() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setAddress("123 Test Street");
        shop.setCity("Test City");

        Product product = new Product(1L, "Milk", "Dairy Co", Category.DAIRY_PRODUCTS,
                "MILK001", "Fresh milk", BigDecimal.valueOf(5.99), List.of());
        Entry entry = new Entry(1L, shop, product, 2, BigDecimal.valueOf(11.98));
        List<Entry> entries = List.of(entry);

        // Act
        ShopData result = shopMapper.toShopData(shop, entries);

        // Assert
        ShopData.ProductData productData = result.getEntries().getFirst().getProduct();
        assertThat(productData).isNotNull();
        assertThat(productData.getName()).isEqualTo("Milk");
        assertThat(productData.getManufacturer()).isEqualTo("Dairy Co");
        assertThat(productData.getCategory()).isEqualTo("dairy_products");
        assertThat(productData.getProductCode()).isEqualTo("MILK001");
        assertThat(productData.getDescription()).isEqualTo("Fresh milk");
    }

    @Test
    @DisplayName("toShopData should handle empty entries list")
    void testToShopDataWithEmptyEntries() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setAddress("123 Test Street");
        shop.setCity("Test City");

        List<Entry> emptyEntries = List.of();

        // Act
        ShopData result = shopMapper.toShopData(shop, emptyEntries);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Shop");
        assertThat(result.getAddress()).isEqualTo("123 Test Street");
        assertThat(result.getCity()).isEqualTo("Test City");
        assertThat(result.getEntries()).isEmpty();
    }

    @Test
    @DisplayName("toShopData should handle multiple entries")
    void testToShopDataWithMultipleEntries() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setAddress("123 Test Street");
        shop.setCity("Test City");

        Product product1 = new Product(1L, "Milk", "Dairy Co", Category.DAIRY_PRODUCTS,
                "MILK001", "Fresh milk", BigDecimal.valueOf(5.99), List.of());
        Product product2 = new Product(2L, "Bread", "Bakery Inc", Category.GRAINS_AND_CEREALS,
                "BREAD001", "Fresh bread", BigDecimal.valueOf(3.50), List.of());

        Entry entry1 = new Entry(1L, shop, product1, 2, BigDecimal.valueOf(11.98));
        Entry entry2 = new Entry(2L, shop, product2, 3, BigDecimal.valueOf(10.50));
        List<Entry> entries = List.of(entry1, entry2);

        // Act
        ShopData result = shopMapper.toShopData(shop, entries);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEntries()).hasSize(2);

        // Verify first entry
        ShopData.EntryData firstEntry = result.getEntries().getFirst();
        assertThat(firstEntry.getAmount()).isEqualTo(2);
        assertThat(firstEntry.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(11.98));
        assertThat(firstEntry.getProduct().getName()).isEqualTo("Milk");

        // Verify second entry
        ShopData.EntryData secondEntry = result.getEntries().get(1);
        assertThat(secondEntry.getAmount()).isEqualTo(3);
        assertThat(secondEntry.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(10.50));
        assertThat(secondEntry.getProduct().getName()).isEqualTo("Bread");
    }

    @Test
    @DisplayName("toShopData should handle null product description")
    void testToShopDataWithNullDescription() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setAddress("123 Test Street");
        shop.setCity("Test City");

        Product productWithNullDescription = new Product(1L, "Eggs", "Farm Co",
                Category.DAIRY_PRODUCTS, "EGG001", null, BigDecimal.valueOf(4.99), List.of());
        Entry entry = new Entry(1L, shop, productWithNullDescription, 1, BigDecimal.valueOf(4.99));
        List<Entry> entries = List.of(entry);

        // Act
        ShopData result = shopMapper.toShopData(shop, entries);

        // Assert
        ShopData.ProductData productData = result.getEntries().getFirst().getProduct();
        assertThat(productData).isNotNull();
        assertThat(productData.getDescription()).isEmpty(); // Should be empty string, not null
    }

    @Test
    @DisplayName("toShopData should convert category to lowercase")
    void testToShopDataCategoryLowerCase() {
        // Arrange
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setAddress("123 Test Street");
        shop.setCity("Test City");

        Product product = new Product(1L, "Milk", "Dairy Co", Category.DAIRY_PRODUCTS,
                "MILK001", "Fresh milk", BigDecimal.valueOf(5.99), List.of());
        Entry entry = new Entry(1L, shop, product, 2, BigDecimal.valueOf(11.98));
        List<Entry> entries = List.of(entry);

        // Act
        ShopData result = shopMapper.toShopData(shop, entries);

        // Assert
        ShopData.ProductData productData = result.getEntries().getFirst().getProduct();
        assertThat(productData.getCategory()).isEqualTo("dairy_products");
        assertThat(productData.getCategory()).isEqualTo(productData.getCategory().toLowerCase());
    }
}
