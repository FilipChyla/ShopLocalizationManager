package edu.chylaozgaoldakowski.location_manager.shop;

import edu.chylaozgaoldakowski.location_manager.entry.Entry;
import edu.chylaozgaoldakowski.location_manager.entry.EntryDto;
import edu.chylaozgaoldakowski.location_manager.entry.EntryMapper;
import edu.chylaozgaoldakowski.location_manager.entry.EntryRepository;
import edu.chylaozgaoldakowski.location_manager.product.Category;
import edu.chylaozgaoldakowski.location_manager.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ShopMapper shopMapper;

    @Mock
    private EntryMapper entryMapper;

    @Mock
    private EntryRepository entryRepository;

    @InjectMocks
    private ShopService shopService;

    private Shop testShop;
    private ShopDto testShopDto;

    @BeforeEach
    void setUp() {
        testShop = new Shop(1L, "Test Shop", "123 Main St", "Warsaw", List.of());
        testShopDto = new ShopDto(1L, "Test Shop", "123 Main St", "Warsaw");
    }

    @Test
    void getAll_shouldReturnListOfShopDtos() {
        // Given
        List<Shop> shops = List.of(testShop);
        when(shopRepository.findAll()).thenReturn(shops);
        when(shopMapper.toDto(testShop)).thenReturn(testShopDto);

        // When
        List<ShopDto> result = shopService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testShopDto, result.getFirst());
        verify(shopRepository).findAll();
        verify(shopMapper).toDto(testShop);
    }

    @Test
    void save_shouldSaveNewShop() {
        // Given
        ShopDto newShopDto = new ShopDto(null, "New Shop", "456 Oak Ave", "Krakow");

        // When
        shopService.save(newShopDto);

        // Then
        verify(shopMapper).updateEntityFromDto(any(Shop.class), eq(newShopDto));
        verify(shopRepository).save(any(Shop.class));
    }

    @Test
    void getById_shouldReturnShopDto() {
        // Given
        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));
        when(shopMapper.toDto(testShop)).thenReturn(testShopDto);

        // When
        ShopDto result = shopService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testShopDto, result);
        verify(shopRepository).findById(1L);
        verify(shopMapper).toDto(testShop);
    }

    @Test
    void getById_shouldThrowExceptionWhenNotFound() {
        // Given
        when(shopRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> shopService.getById(999L));
        verify(shopRepository).findById(999L);
    }

    @Test
    void update_shouldUpdateExistingShop() {
        // Given
        ShopDto updatedDto = new ShopDto(null, "Updated Shop", "789 Elm St", "Gdansk");
        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));

        // When
        shopService.update(1L, updatedDto);

        // Then
        verify(shopRepository).findById(1L);
        verify(shopMapper).updateEntityFromDto(testShop, updatedDto);
        verify(shopRepository).save(testShop);
    }

    @Test
    void deleteById_shouldDeleteShop() {
        // When
        shopService.deleteById(1L);

        // Then
        verify(shopRepository).deleteById(1L);
    }

    @Test
    void getEntriesById_shouldReturnListOfEntryDtos() {
        // Given
        Product product = new Product(1L, "Milk", "Dairy Co", Category.DAIRY_PRODUCTS,
                "MILK001", "Fresh milk", BigDecimal.valueOf(5.99), List.of());
        Entry entry = new Entry(1L, testShop, product, 2, BigDecimal.valueOf(11.98));
        EntryDto entryDto = new EntryDto(1L, 1L, 1L, "Milk", 2, BigDecimal.valueOf(11.98));

        when(entryRepository.findByShop_Id(1L)).thenReturn(List.of(entry));
        when(entryMapper.toDto(entry)).thenReturn(entryDto);

        // When
        List<EntryDto> result = shopService.getEntriesById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(entryDto, result.getFirst());
        verify(entryRepository).findByShop_Id(1L);
        verify(entryMapper).toDto(entry);
    }

    @Test
    void getShopDataById_shouldReturnShopData() {
        // Given
        Product product = new Product(1L, "Milk", "Dairy Co", Category.DAIRY_PRODUCTS,
                "MILK001", "Fresh milk", BigDecimal.valueOf(5.99), List.of());
        Entry entry = new Entry(1L, testShop, product, 2, BigDecimal.valueOf(11.98));

        ShopData.ProductData productData = ShopData.ProductData.builder()
                .name("Milk")
                .manufacturer("Dairy Co")
                .category("dairy_products")
                .productCode("MILK001")
                .description("Fresh milk")
                .build();

        ShopData.EntryData entryData = ShopData.EntryData.builder()
                .product(productData)
                .amount(2)
                .totalPrice(BigDecimal.valueOf(11.98))
                .build();

        ShopData shopData = ShopData.builder()
                .name("Test Shop")
                .address("123 Main St")
                .city("Warsaw")
                .entries(List.of(entryData))
                .build();

        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));
        when(entryRepository.findByShop_Id(1L)).thenReturn(List.of(entry));
        when(shopMapper.toShopData(testShop, List.of(entry))).thenReturn(shopData);

        // When
        ShopData result = shopService.getShopDataById(1L);

        // Then
        assertNotNull(result);
        assertEquals(shopData, result);
        verify(shopRepository).findById(1L);
        verify(entryRepository).findByShop_Id(1L);
        verify(shopMapper).toShopData(testShop, List.of(entry));
    }

    @Test
    void getShopDataById_shouldThrowExceptionWhenNotFound() {
        // Given
        when(shopRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> shopService.getShopDataById(999L));
        verify(shopRepository).findById(999L);
    }
}
