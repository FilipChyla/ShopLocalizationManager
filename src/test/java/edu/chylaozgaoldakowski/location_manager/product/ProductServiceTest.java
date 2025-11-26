package edu.chylaozgaoldakowski.location_manager.product;

import edu.chylaozgaoldakowski.location_manager.entry.Entry;
import edu.chylaozgaoldakowski.location_manager.entry.EntryMapper;
import edu.chylaozgaoldakowski.location_manager.entry.EntryRepository;
import edu.chylaozgaoldakowski.location_manager.shop.Shop;
import edu.chylaozgaoldakowski.location_manager.shop.ShopDto;
import edu.chylaozgaoldakowski.location_manager.user.AppUser;
import edu.chylaozgaoldakowski.location_manager.user.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntryRepository entryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private EntryMapper entryMapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductDto testProductDto;
    private Shop testShop;
    private AppUser testUser;
    private CustomUserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        testShop = new Shop();
        testShop.setId(1L);
        testShop.setName("Test Shop");
        testShop.setAddress("123 Test St");
        testShop.setCity("Test City");

        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole("USER");
        testUser.setAssignedShop(testShop);

        testUserDetails = new CustomUserDetails(testUser);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setManufacturer("Test Manufacturer");
        testProduct.setCategory(Category.FRESH_PRODUCE);
        testProduct.setProductCode("TEST-001");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));

        testProductDto = new ProductDto();
        testProductDto.setId(1L);
        testProductDto.setName("Test Product");
        testProductDto.setManufacturer("Test Manufacturer");
        testProductDto.setCategory(Category.FRESH_PRODUCE);
        testProductDto.setProductCode("TEST-001");
        testProductDto.setDescription("Test Description");
        testProductDto.setPrice(new BigDecimal("99.99"));
    }

    @Test
    @DisplayName("getAllProducts should return list of all products as DTOs")
    void testGetAllProducts() {
        // Arrange
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");

        ProductDto productDto2 = new ProductDto();
        productDto2.setId(2L);
        productDto2.setName("Product 2");

        List<Product> products = List.of(testProduct, product2);

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toProductDetailsDto(testProduct)).thenReturn(testProductDto);
        when(productMapper.toProductDetailsDto(product2)).thenReturn(productDto2);

        // Act
        List<ProductDto> result = productService.getAllProducts();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testProductDto, productDto2);
        verify(productRepository).findAll();
        verify(productMapper, times(2)).toProductDetailsDto(any(Product.class));
    }

    @Test
    @DisplayName("getAllProducts should return empty list when no products exist")
    void testGetAllProductsEmpty() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of());

        // Act
        List<ProductDto> result = productService.getAllProducts();

        // Assert
        assertThat(result).isEmpty();
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("getProductDetailsById should return product DTO when product exists")
    void testGetProductDetailsById() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toProductDetailsDto(testProduct)).thenReturn(testProductDto);

        // Act
        ProductDto result = productService.getProductDetailsById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository).findById(1L);
        verify(productMapper).toProductDetailsDto(testProduct);
    }

    @Test
    @DisplayName("getProductDetailsById should throw exception when product does not exist")
    void testGetProductDetailsByIdNotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getProductDetailsById(999L))
                .isInstanceOf(NoSuchElementException.class);
        verify(productRepository).findById(999L);
        verify(productMapper, never()).toProductDetailsDto(any());
    }

    @Test
    @DisplayName("getLocalizationsForCurrentUser should return empty list when user is null")
    void testGetLocalizationsForCurrentUserNull() {
        // Arrange
        Entry entry = createEntry(1L, testShop, testProduct, 10, new BigDecimal("999.90"));
        when(entryRepository.findByProduct_Id(1L)).thenReturn(List.of(entry));

        // Act
        List<ProductLocalizationDto> result = productService.getLocalizationsForCurrentUser(1L, null);

        // Assert
        assertThat(result).isEmpty();
        verify(entryRepository).findByProduct_Id(1L);
    }

    @Test
    @DisplayName("getLocalizationsForCurrentUser should return all localizations for admin")
    void testGetLocalizationsForCurrentUserAdmin() {
        // Arrange
        AppUser adminUser = new AppUser();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setPassword("password");
        adminUser.setRole("ADMIN");
        adminUser.setAssignedShop(testShop);
        CustomUserDetails adminUserDetails = new CustomUserDetails(adminUser);

        Shop shop2 = new Shop();
        shop2.setId(2L);
        shop2.setName("Shop 2");

        Entry entry1 = createEntry(1L, testShop, testProduct, 10, new BigDecimal("999.90"));
        Entry entry2 = createEntry(2L, shop2, testProduct, 5, new BigDecimal("499.95"));

        ShopDto shopDto1 = new ShopDto(1L, "Test Shop", "123 Test St", "Test City");
        ShopDto shopDto2 = new ShopDto(2L, "Shop 2", "456 Test Ave", "Test City 2");

        ProductLocalizationDto localization1 = new ProductLocalizationDto(shopDto1, 10, new BigDecimal("999.90"));
        ProductLocalizationDto localization2 = new ProductLocalizationDto(shopDto2, 5, new BigDecimal("499.95"));

        when(entryRepository.findByProduct_Id(1L)).thenReturn(List.of(entry1, entry2));
        when(entryMapper.toProductLocalizationDto(entry1)).thenReturn(localization1);
        when(entryMapper.toProductLocalizationDto(entry2)).thenReturn(localization2);

        // Act
        List<ProductLocalizationDto> result = productService.getLocalizationsForCurrentUser(1L, adminUserDetails);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(localization1, localization2);
        verify(entryRepository).findByProduct_Id(1L);
    }

    @Test
    @DisplayName("getLocalizationsForCurrentUser should return only user's shop localizations for regular user")
    void testGetLocalizationsForCurrentUserRegularUser() {
        // Arrange
        Shop shop2 = new Shop();
        shop2.setId(2L);
        shop2.setName("Shop 2");

        Entry entry1 = createEntry(1L, testShop, testProduct, 10, new BigDecimal("999.90"));
        Entry entry2 = createEntry(2L, shop2, testProduct, 5, new BigDecimal("499.95"));

        ShopDto shopDto1 = new ShopDto(1L, "Test Shop", "123 Test St", "Test City");
        ShopDto shopDto2 = new ShopDto(2L, "Shop 2", "456 Test Ave", "Test City 2");

        ProductLocalizationDto localization1 = new ProductLocalizationDto(shopDto1, 10, new BigDecimal("999.90"));
        ProductLocalizationDto localization2 = new ProductLocalizationDto(shopDto2, 5, new BigDecimal("499.95"));

        when(entryRepository.findByProduct_Id(1L)).thenReturn(List.of(entry1, entry2));
        when(entryMapper.toProductLocalizationDto(entry1)).thenReturn(localization1);
        when(entryMapper.toProductLocalizationDto(entry2)).thenReturn(localization2);

        // Act
        List<ProductLocalizationDto> result = productService.getLocalizationsForCurrentUser(1L, testUserDetails);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(localization1);
        assertThat(result.get(0).getShop().getId()).isEqualTo(1L);
        verify(entryRepository).findByProduct_Id(1L);
    }

    @Test
    @DisplayName("getLocalizationsForCurrentUser should return empty list when no entries match user's shop")
    void testGetLocalizationsForCurrentUserNoMatch() {
        // Arrange
        Shop shop2 = new Shop();
        shop2.setId(2L);

        Entry entry = createEntry(1L, shop2, testProduct, 10, new BigDecimal("999.90"));
        ShopDto shopDto2 = new ShopDto(2L, "Shop 2", "456 Test Ave", "Test City 2");
        ProductLocalizationDto localization = new ProductLocalizationDto(shopDto2, 10, new BigDecimal("999.90"));

        when(entryRepository.findByProduct_Id(1L)).thenReturn(List.of(entry));
        when(entryMapper.toProductLocalizationDto(entry)).thenReturn(localization);

        // Act
        List<ProductLocalizationDto> result = productService.getLocalizationsForCurrentUser(1L, testUserDetails);

        // Assert
        assertThat(result).isEmpty();
        verify(entryRepository).findByProduct_Id(1L);
    }

    @Test
    @DisplayName("saveProduct should create new product from DTO")
    void testSaveProduct() {
        // Arrange
        Product newProduct = new Product();
        doAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            ProductDto dto = invocation.getArgument(1);
            product.setName(dto.getName());
            product.setManufacturer(dto.getManufacturer());
            product.setCategory(dto.getCategory());
            product.setProductCode(dto.getProductCode());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            return null;
        }).when(productMapper).updateEntityFromDto(any(Product.class), eq(testProductDto));

        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        // Act
        productService.saveProduct(testProductDto);

        // Assert
        verify(productMapper).updateEntityFromDto(any(Product.class), eq(testProductDto));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("deleteProductById should delete product when it exists")
    void testDeleteProductById() {
        // Arrange
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProductById(1L);

        // Assert
        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("updateProduct should update existing product with new data")
    void testUpdateProduct() {
        // Arrange
        ProductDto updatedDto = new ProductDto();
        updatedDto.setId(1L);
        updatedDto.setName("Updated Product");
        updatedDto.setManufacturer("Updated Manufacturer");
        updatedDto.setCategory(Category.BAKERY_PRODUCTS);
        updatedDto.setProductCode("UPD-001");
        updatedDto.setDescription("Updated Description");
        updatedDto.setPrice(new BigDecimal("149.99"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            ProductDto dto = invocation.getArgument(1);
            product.setName(dto.getName());
            product.setManufacturer(dto.getManufacturer());
            product.setCategory(dto.getCategory());
            product.setProductCode(dto.getProductCode());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            return null;
        }).when(productMapper).updateEntityFromDto(testProduct, updatedDto);
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // Act
        productService.updateProduct(1L, updatedDto);

        // Assert
        verify(productRepository).findById(1L);
        verify(productMapper).updateEntityFromDto(testProduct, updatedDto);
        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("updateProduct should throw exception when product does not exist")
    void testUpdateProductNotFound() {
        // Arrange
        ProductDto updatedDto = new ProductDto();
        updatedDto.setName("Updated Product");

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(999L, updatedDto))
                .isInstanceOf(NoSuchElementException.class);
        verify(productRepository).findById(999L);
        verify(productMapper, never()).updateEntityFromDto(any(), any());
        verify(productRepository, never()).save(any());
    }

    private Entry createEntry(Long id, Shop shop, Product product, int amount, BigDecimal totalPrice) {
        Entry entry = new Entry();
        entry.setId(id);
        entry.setShop(shop);
        entry.setProduct(product);
        entry.setAmount(amount);
        entry.setTotalPrice(totalPrice);
        return entry;
    }
}
