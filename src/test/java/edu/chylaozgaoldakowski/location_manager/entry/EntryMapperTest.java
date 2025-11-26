package edu.chylaozgaoldakowski.location_manager.entry;

import edu.chylaozgaoldakowski.location_manager.product.Product;
import edu.chylaozgaoldakowski.location_manager.product.ProductLocalizationDto;
import edu.chylaozgaoldakowski.location_manager.shop.Shop;
import edu.chylaozgaoldakowski.location_manager.shop.ShopDto;
import edu.chylaozgaoldakowski.location_manager.shop.ShopMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EntryMapper Unit Tests")
class EntryMapperTest {

    @Mock
    private ShopMapper shopMapper;

    @InjectMocks
    private EntryMapper entryMapper;

    private Shop testShop;
    private ShopDto testShopDto;
    private Product testProduct;
    private Entry testEntry;

    @BeforeEach
    void setUp() {
        testShop = new Shop();
        testShop.setId(1L);
        testShop.setName("Test Shop");
        testShop.setAddress("123 Test St");
        testShop.setCity("Test City");

        testShopDto = new ShopDto();
        testShopDto.setId(1L);
        testShopDto.setName("Test Shop");
        testShopDto.setAddress("123 Test St");
        testShopDto.setCity("Test City");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("99.99"));

        testEntry = new Entry();
        testEntry.setId(1L);
        testEntry.setShop(testShop);
        testEntry.setProduct(testProduct);
        testEntry.setAmount(10);
        testEntry.setTotalPrice(new BigDecimal("999.90"));
    }

    @Test
    @DisplayName("toDto should correctly map Entry entity to EntryDto")
    void testToDto() {
        // Act
        EntryDto result = entryMapper.toDto(testEntry);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getShopId()).isEqualTo(1L);
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getProductName()).isEqualTo("Test Product");
        assertThat(result.getAmount()).isEqualTo(10);
        assertThat(result.getTotalPrice()).isEqualByComparingTo(new BigDecimal("999.90"));
    }

    @Test
    @DisplayName("toDto should handle entry with null id")
    void testToDtoWithNullId() {
        // Arrange
        testEntry.setId(null);

        // Act
        EntryDto result = entryMapper.toDto(testEntry);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getShopId()).isEqualTo(1L);
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getProductName()).isEqualTo("Test Product");
        assertThat(result.getAmount()).isEqualTo(10);
        assertThat(result.getTotalPrice()).isEqualByComparingTo(new BigDecimal("999.90"));
    }

    @Test
    @DisplayName("toDto should correctly extract shop and product ids")
    void testToDtoExtractsIds() {
        // Arrange
        Shop differentShop = new Shop();
        differentShop.setId(5L);
        differentShop.setName("Different Shop");

        Product differentProduct = new Product();
        differentProduct.setId(7L);
        differentProduct.setName("Different Product");

        Entry entry = new Entry();
        entry.setId(2L);
        entry.setShop(differentShop);
        entry.setProduct(differentProduct);
        entry.setAmount(5);
        entry.setTotalPrice(new BigDecimal("250.00"));

        // Act
        EntryDto result = entryMapper.toDto(entry);

        // Assert
        assertThat(result.getShopId()).isEqualTo(5L);
        assertThat(result.getProductId()).isEqualTo(7L);
        assertThat(result.getProductName()).isEqualTo("Different Product");
    }

    @Test
    @DisplayName("toProductLocalizationDto should correctly map Entry to ProductLocalizationDto")
    void testToProductLocalizationDto() {
        // Arrange
        when(shopMapper.toDto(testShop)).thenReturn(testShopDto);

        // Act
        ProductLocalizationDto result = entryMapper.toProductLocalizationDto(testEntry);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getShop()).isNotNull();
        assertThat(result.getShop().getId()).isEqualTo(1L);
        assertThat(result.getShop().getName()).isEqualTo("Test Shop");
        assertThat(result.getAmount()).isEqualTo(10);
        assertThat(result.getTotalPrice()).isEqualByComparingTo(new BigDecimal("999.90"));
        verify(shopMapper).toDto(testShop);
    }

    @Test
    @DisplayName("toProductLocalizationDto should use shopMapper for shop conversion")
    void testToProductLocalizationDtoUsesShopMapper() {
        // Arrange
        ShopDto customShopDto = new ShopDto();
        customShopDto.setId(1L);
        customShopDto.setName("Custom Shop");
        customShopDto.setAddress("Custom Address");
        customShopDto.setCity("Custom City");

        when(shopMapper.toDto(testShop)).thenReturn(customShopDto);

        // Act
        ProductLocalizationDto result = entryMapper.toProductLocalizationDto(testEntry);

        // Assert
        assertThat(result.getShop()).isEqualTo(customShopDto);
        assertThat(result.getShop().getName()).isEqualTo("Custom Shop");
        verify(shopMapper).toDto(testShop);
    }

    @Test
    @DisplayName("toProductLocalizationDto should handle different amounts and prices")
    void testToProductLocalizationDtoWithDifferentValues() {
        // Arrange
        Entry differentEntry = new Entry();
        differentEntry.setId(2L);
        differentEntry.setShop(testShop);
        differentEntry.setProduct(testProduct);
        differentEntry.setAmount(25);
        differentEntry.setTotalPrice(new BigDecimal("2499.75"));

        when(shopMapper.toDto(testShop)).thenReturn(testShopDto);

        // Act
        ProductLocalizationDto result = entryMapper.toProductLocalizationDto(differentEntry);

        // Assert
        assertThat(result.getAmount()).isEqualTo(25);
        assertThat(result.getTotalPrice()).isEqualByComparingTo(new BigDecimal("2499.75"));
    }

    @Test
    @DisplayName("toDto should handle zero amount")
    void testToDtoWithZeroAmount() {
        // Arrange
        testEntry.setAmount(0);
        testEntry.setTotalPrice(BigDecimal.ZERO);

        // Act
        EntryDto result = entryMapper.toDto(testEntry);

        // Assert
        assertThat(result.getAmount()).isEqualTo(0);
        assertThat(result.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("toProductLocalizationDto should handle zero amount")
    void testToProductLocalizationDtoWithZeroAmount() {
        // Arrange
        testEntry.setAmount(0);
        testEntry.setTotalPrice(BigDecimal.ZERO);
        when(shopMapper.toDto(testShop)).thenReturn(testShopDto);

        // Act
        ProductLocalizationDto result = entryMapper.toProductLocalizationDto(testEntry);

        // Assert
        assertThat(result.getAmount()).isEqualTo(0);
        assertThat(result.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
