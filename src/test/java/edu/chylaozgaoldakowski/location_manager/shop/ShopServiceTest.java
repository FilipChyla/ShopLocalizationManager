package edu.chylaozgaoldakowski.location_manager.shop;

import edu.chylaozgaoldakowski.location_manager.entry.Entry;
import edu.chylaozgaoldakowski.location_manager.entry.EntryDto;
import edu.chylaozgaoldakowski.location_manager.entry.EntryMapper;
import edu.chylaozgaoldakowski.location_manager.entry.EntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShopService Unit Tests")
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
    }

    @Test
    @DisplayName("getAll should return list of all shops as DTOs")
    void testGetAll() {
        // Arrange
        Shop shop2 = new Shop();
        shop2.setId(2L);
        shop2.setName("Shop 2");
        shop2.setAddress("456 Test Ave");
        shop2.setCity("Test City 2");

        List<Shop> shops = List.of(testShop, shop2);

        ShopDto shopDto2 = new ShopDto();
        shopDto2.setId(2L);
        shopDto2.setName("Shop 2");
        shopDto2.setAddress("456 Test Ave");
        shopDto2.setCity("Test City 2");

        when(shopRepository.findAll()).thenReturn(shops);
        when(shopMapper.toDto(testShop)).thenReturn(testShopDto);
        when(shopMapper.toDto(shop2)).thenReturn(shopDto2);

        // Act
        List<ShopDto> result = shopService.getAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testShopDto, shopDto2);
        verify(shopRepository).findAll();
        verify(shopMapper, times(2)).toDto(any(Shop.class));
    }

    @Test
    @DisplayName("getAll should return empty list when no shops exist")
    void testGetAllEmpty() {
        // Arrange
        when(shopRepository.findAll()).thenReturn(List.of());

        // Act
        List<ShopDto> result = shopService.getAll();

        // Assert
        assertThat(result).isEmpty();
        verify(shopRepository).findAll();
    }

    @Test
    @DisplayName("save should create new shop from DTO")
    void testSave() {
        // Arrange
        Shop newShop = new Shop();
        doAnswer(invocation -> {
            Shop shop = invocation.getArgument(0);
            ShopDto dto = invocation.getArgument(1);
            shop.setName(dto.getName());
            shop.setAddress(dto.getAddress());
            shop.setCity(dto.getCity());
            return null;
        }).when(shopMapper).updateEntityFromDto(any(Shop.class), eq(testShopDto));

        when(shopRepository.save(any(Shop.class))).thenReturn(newShop);

        // Act
        shopService.save(testShopDto);

        // Assert
        verify(shopMapper).updateEntityFromDto(any(Shop.class), eq(testShopDto));
        verify(shopRepository).save(any(Shop.class));
    }

    @Test
    @DisplayName("getById should return shop DTO when shop exists")
    void testGetById() {
        // Arrange
        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));
        when(shopMapper.toDto(testShop)).thenReturn(testShopDto);

        // Act
        ShopDto result = shopService.getById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Shop");
        verify(shopRepository).findById(1L);
        verify(shopMapper).toDto(testShop);
    }

    @Test
    @DisplayName("getById should throw exception when shop does not exist")
    void testGetByIdNotFound() {
        // Arrange
        when(shopRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> shopService.getById(999L))
                .isInstanceOf(NoSuchElementException.class);
        verify(shopRepository).findById(999L);
        verify(shopMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("deleteById should delete shop when it exists")
    void testDeleteById() {
        // Arrange
        doNothing().when(shopRepository).deleteById(1L);

        // Act
        shopService.deleteById(1L);

        // Assert
        verify(shopRepository).deleteById(1L);
    }

    @Test
    @DisplayName("update should update existing shop with new data")
    void testUpdate() {
        // Arrange
        ShopDto updatedDto = new ShopDto();
        updatedDto.setId(1L);
        updatedDto.setName("Updated Shop");
        updatedDto.setAddress("789 New St");
        updatedDto.setCity("New City");

        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));
        doAnswer(invocation -> {
            Shop shop = invocation.getArgument(0);
            ShopDto dto = invocation.getArgument(1);
            shop.setName(dto.getName());
            shop.setAddress(dto.getAddress());
            shop.setCity(dto.getCity());
            return null;
        }).when(shopMapper).updateEntityFromDto(testShop, updatedDto);
        when(shopRepository.save(testShop)).thenReturn(testShop);

        // Act
        shopService.update(1L, updatedDto);

        // Assert
        verify(shopRepository).findById(1L);
        verify(shopMapper).updateEntityFromDto(testShop, updatedDto);
        verify(shopRepository).save(testShop);
    }

    @Test
    @DisplayName("update should throw exception when shop does not exist")
    void testUpdateNotFound() {
        // Arrange
        ShopDto updatedDto = new ShopDto();
        updatedDto.setName("Updated Shop");

        when(shopRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> shopService.update(999L, updatedDto))
                .isInstanceOf(NoSuchElementException.class);
        verify(shopRepository).findById(999L);
        verify(shopMapper, never()).updateEntityFromDto(any(), any());
        verify(shopRepository, never()).save(any());
    }

    @Test
    @DisplayName("getEntriesById should return list of entries for a shop")
    void testGetEntriesById() {
        // Arrange
        Entry entry1 = new Entry();
        entry1.setId(1L);
        Entry entry2 = new Entry();
        entry2.setId(2L);
        List<Entry> entries = List.of(entry1, entry2);

        EntryDto entryDto1 = new EntryDto();
        entryDto1.setId(1L);
        EntryDto entryDto2 = new EntryDto();
        entryDto2.setId(2L);

        when(entryRepository.findByShop_Id(1L)).thenReturn(entries);
        when(entryMapper.toDto(entry1)).thenReturn(entryDto1);
        when(entryMapper.toDto(entry2)).thenReturn(entryDto2);

        // Act
        List<EntryDto> result = shopService.getEntriesById(1L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(entryDto1, entryDto2);
        verify(entryRepository).findByShop_Id(1L);
        verify(entryMapper, times(2)).toDto(any(Entry.class));
    }

    @Test
    @DisplayName("getEntriesById should return empty list when shop has no entries")
    void testGetEntriesByIdEmpty() {
        // Arrange
        when(entryRepository.findByShop_Id(1L)).thenReturn(new ArrayList<>());

        // Act
        List<EntryDto> result = shopService.getEntriesById(1L);

        // Assert
        assertThat(result).isEmpty();
        verify(entryRepository).findByShop_Id(1L);
    }
}
