package edu.chylaozgaoldakowski.location_manager.entry;

import edu.chylaozgaoldakowski.location_manager.product.Product;
import edu.chylaozgaoldakowski.location_manager.product.ProductRepository;
import edu.chylaozgaoldakowski.location_manager.shop.Shop;
import edu.chylaozgaoldakowski.location_manager.shop.ShopRepository;
import edu.chylaozgaoldakowski.location_manager.user.AppUser;
import edu.chylaozgaoldakowski.location_manager.user.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EntryService Unit Tests")
class EntryServiceTest {

    @Mock
    private EntryRepository entryRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntryMapper entryMapper;

    @InjectMocks
    private EntryService entryService;

    private Shop testShop;
    private Product testProduct;
    private Entry testEntry;
    private EntryDto testEntryDto;
    private AppUser testUser;
    private CustomUserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        testShop = new Shop();
        testShop.setId(1L);
        testShop.setName("Test Shop");
        testShop.setAddress("123 Test St");
        testShop.setCity("Test City");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("99.99"));

        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole("USER");
        testUser.setAssignedShop(testShop);

        testUserDetails = new CustomUserDetails(testUser);

        testEntry = new Entry();
        testEntry.setId(1L);
        testEntry.setShop(testShop);
        testEntry.setProduct(testProduct);
        testEntry.setAmount(10);
        testEntry.setTotalPrice(new BigDecimal("999.90"));

        testEntryDto = new EntryDto();
        testEntryDto.setId(1L);
        testEntryDto.setShopId(1L);
        testEntryDto.setProductId(1L);
        testEntryDto.setProductName("Test Product");
        testEntryDto.setAmount(10);
        testEntryDto.setTotalPrice(new BigDecimal("999.90"));
    }

    @Test
    @DisplayName("save should create new entry when user has access to shop")
    void testSaveSuccess() {
        // Arrange
        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(entryRepository.save(any(Entry.class))).thenReturn(testEntry);

        ArgumentCaptor<Entry> entryCaptor = ArgumentCaptor.forClass(Entry.class);

        // Act
        entryService.save(testEntryDto, testUserDetails);

        // Assert
        verify(shopRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(entryRepository).save(entryCaptor.capture());

        Entry savedEntry = entryCaptor.getValue();
        assertThat(savedEntry.getShop()).isEqualTo(testShop);
        assertThat(savedEntry.getProduct()).isEqualTo(testProduct);
        assertThat(savedEntry.getAmount()).isEqualTo(10);
        assertThat(savedEntry.getTotalPrice()).isEqualByComparingTo(new BigDecimal("999.90"));
    }

    @Test
    @DisplayName("save should throw AccessDeniedException when user does not have access to shop")
    void testSaveAccessDenied() {
        // Arrange
        Shop otherShop = new Shop();
        otherShop.setId(2L);
        otherShop.setName("Other Shop");

        EntryDto entryDto = new EntryDto();
        entryDto.setShopId(2L);
        entryDto.setProductId(1L);
        entryDto.setAmount(10);

        when(shopRepository.findById(2L)).thenReturn(Optional.of(otherShop));

        // Act & Assert
        assertThatThrownBy(() -> entryService.save(entryDto, testUserDetails))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Cannot create entry");

        verify(shopRepository).findById(2L);
        verify(entryRepository, never()).save(any());
    }

    @Test
    @DisplayName("save should throw AccessDeniedException when user is null")
    void testSaveNullUser() {
        // Arrange
        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));

        // Act & Assert
        assertThatThrownBy(() -> entryService.save(testEntryDto, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Cannot create entry");

        verify(shopRepository).findById(1L);
        verify(entryRepository, never()).save(any());
    }

    @Test
    @DisplayName("save should throw NoSuchElementException when shop does not exist")
    void testSaveShopNotFound() {
        // Arrange
        when(shopRepository.findById(999L)).thenReturn(Optional.empty());

        EntryDto entryDto = new EntryDto();
        entryDto.setShopId(999L);
        entryDto.setProductId(1L);
        entryDto.setAmount(10);

        // Act & Assert
        assertThatThrownBy(() -> entryService.save(entryDto, testUserDetails))
                .isInstanceOf(NoSuchElementException.class);

        verify(shopRepository).findById(999L);
        verify(entryRepository, never()).save(any());
    }

    @Test
    @DisplayName("save should throw NoSuchElementException when product does not exist")
    void testSaveProductNotFound() {
        // Arrange
        EntryDto entryDto = new EntryDto();
        entryDto.setShopId(1L);
        entryDto.setProductId(999L);
        entryDto.setAmount(10);

        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> entryService.save(entryDto, testUserDetails))
                .isInstanceOf(NoSuchElementException.class);

        verify(shopRepository).findById(1L);
        verify(productRepository).findById(999L);
        verify(entryRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteById should delete entry when user has access to shop")
    void testDeleteByIdSuccess() {
        // Arrange
        when(entryRepository.findById(1L)).thenReturn(Optional.of(testEntry));
        doNothing().when(entryRepository).deleteById(1L);

        // Act
        entryService.deleteById(1L, testUserDetails);

        // Assert
        verify(entryRepository).findById(1L);
        verify(entryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById should throw AccessDeniedException when user does not have access to shop")
    void testDeleteByIdAccessDenied() {
        // Arrange
        Shop otherShop = new Shop();
        otherShop.setId(2L);

        Entry entry = new Entry();
        entry.setId(1L);
        entry.setShop(otherShop);

        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));

        // Act & Assert
        assertThatThrownBy(() -> entryService.deleteById(1L, testUserDetails))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Cannot delete entry");

        verify(entryRepository).findById(1L);
        verify(entryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteById should throw AccessDeniedException when user is null")
    void testDeleteByIdNullUser() {
        // Arrange
        when(entryRepository.findById(1L)).thenReturn(Optional.of(testEntry));

        // Act & Assert
        assertThatThrownBy(() -> entryService.deleteById(1L, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Cannot delete entry");

        verify(entryRepository).findById(1L);
        verify(entryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteById should throw NoSuchElementException when entry does not exist")
    void testDeleteByIdNotFound() {
        // Arrange
        when(entryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> entryService.deleteById(999L, testUserDetails))
                .isInstanceOf(NoSuchElementException.class);

        verify(entryRepository).findById(999L);
        verify(entryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getById should return entry DTO when user has access to shop")
    void testGetByIdSuccess() {
        // Arrange
        when(entryRepository.findById(1L)).thenReturn(Optional.of(testEntry));
        when(entryMapper.toDto(testEntry)).thenReturn(testEntryDto);

        // Act
        EntryDto result = entryService.getById(1L, testUserDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getShopId()).isEqualTo(1L);
        verify(entryRepository).findById(1L);
        verify(entryMapper).toDto(testEntry);
    }

    @Test
    @DisplayName("getById should throw AccessDeniedException when user does not have access to shop")
    void testGetByIdAccessDenied() {
        // Arrange
        Shop otherShop = new Shop();
        otherShop.setId(2L);

        Entry entry = new Entry();
        entry.setId(1L);
        entry.setShop(otherShop);

        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));

        // Act & Assert
        assertThatThrownBy(() -> entryService.getById(1L, testUserDetails))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Cannot access entry with id: 1");

        verify(entryRepository).findById(1L);
        verify(entryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("getById should throw AccessDeniedException when user is null")
    void testGetByIdNullUser() {
        // Arrange
        when(entryRepository.findById(1L)).thenReturn(Optional.of(testEntry));

        // Act & Assert
        assertThatThrownBy(() -> entryService.getById(1L, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Cannot access entry with id: 1");

        verify(entryRepository).findById(1L);
        verify(entryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("getById should throw NoSuchElementException when entry does not exist")
    void testGetByIdNotFound() {
        // Arrange
        when(entryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> entryService.getById(999L, testUserDetails))
                .isInstanceOf(NoSuchElementException.class);

        verify(entryRepository).findById(999L);
        verify(entryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("update should update entry when user has access to shop")
    void testUpdateSuccess() {
        // Arrange
        EntryDto updatedDto = new EntryDto();
        updatedDto.setId(1L);
        updatedDto.setShopId(1L);
        updatedDto.setProductId(1L);
        updatedDto.setAmount(20);

        when(entryRepository.findById(1L)).thenReturn(Optional.of(testEntry));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(entryRepository.save(testEntry)).thenReturn(testEntry);

        // Act
        entryService.update(1L, updatedDto, testUserDetails);

        // Assert
        verify(entryRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(entryRepository).save(testEntry);
        assertThat(testEntry.getAmount()).isEqualTo(20);
        assertThat(testEntry.getTotalPrice()).isEqualByComparingTo(new BigDecimal("1999.80"));
    }

    @Test
    @DisplayName("update should throw AccessDeniedException when user does not have access to shop")
    void testUpdateAccessDenied() {
        // Arrange
        Shop otherShop = new Shop();
        otherShop.setId(2L);

        Entry entry = new Entry();
        entry.setId(1L);
        entry.setShop(otherShop);

        EntryDto updatedDto = new EntryDto();
        updatedDto.setId(1L);
        updatedDto.setShopId(2L);
        updatedDto.setProductId(1L);
        updatedDto.setAmount(20);

        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));

        // Act & Assert
        assertThatThrownBy(() -> entryService.update(1L, updatedDto, testUserDetails))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Cannot update entry with id: 1");

        verify(entryRepository).findById(1L);
        verify(entryRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should throw AccessDeniedException when user is null")
    void testUpdateNullUser() {
        // Arrange
        EntryDto updatedDto = new EntryDto();
        updatedDto.setId(1L);
        updatedDto.setAmount(20);

        when(entryRepository.findById(1L)).thenReturn(Optional.of(testEntry));

        // Act & Assert
        assertThatThrownBy(() -> entryService.update(1L, updatedDto, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Cannot update entry with id: 1");

        verify(entryRepository).findById(1L);
        verify(entryRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should throw NoSuchElementException when entry does not exist")
    void testUpdateEntryNotFound() {
        // Arrange
        EntryDto updatedDto = new EntryDto();
        updatedDto.setAmount(20);

        when(entryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> entryService.update(999L, updatedDto, testUserDetails))
                .isInstanceOf(NoSuchElementException.class);

        verify(entryRepository).findById(999L);
        verify(entryRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should throw NoSuchElementException when product does not exist")
    void testUpdateProductNotFound() {
        // Arrange
        EntryDto updatedDto = new EntryDto();
        updatedDto.setId(1L);
        updatedDto.setShopId(1L);
        updatedDto.setProductId(999L);
        updatedDto.setAmount(20);

        when(entryRepository.findById(1L)).thenReturn(Optional.of(testEntry));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> entryService.update(1L, updatedDto, testUserDetails))
                .isInstanceOf(NoSuchElementException.class);

        verify(entryRepository).findById(1L);
        verify(productRepository).findById(999L);
        verify(entryRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should correctly calculate total price based on amount and product price")
    void testUpdateTotalPriceCalculation() {
        // Arrange
        Product expensiveProduct = new Product();
        expensiveProduct.setId(2L);
        expensiveProduct.setName("Expensive Product");
        expensiveProduct.setPrice(new BigDecimal("250.50"));

        EntryDto updatedDto = new EntryDto();
        updatedDto.setId(1L);
        updatedDto.setShopId(1L);
        updatedDto.setProductId(2L);
        updatedDto.setAmount(3);

        when(entryRepository.findById(1L)).thenReturn(Optional.of(testEntry));
        when(productRepository.findById(2L)).thenReturn(Optional.of(expensiveProduct));
        when(entryRepository.save(testEntry)).thenReturn(testEntry);

        // Act
        entryService.update(1L, updatedDto, testUserDetails);

        // Assert
        assertThat(testEntry.getProduct()).isEqualTo(expensiveProduct);
        assertThat(testEntry.getAmount()).isEqualTo(3);
        assertThat(testEntry.getTotalPrice()).isEqualByComparingTo(new BigDecimal("751.50"));
        verify(entryRepository).save(testEntry);
    }
}
