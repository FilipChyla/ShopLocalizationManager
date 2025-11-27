package edu.chylaozgaoldakowski.location_manager.product;

import edu.chylaozgaoldakowski.location_manager.config.SecurityConfig;
import edu.chylaozgaoldakowski.location_manager.shop.ShopDto;
import edu.chylaozgaoldakowski.location_manager.user.AppUser;
import edu.chylaozgaoldakowski.location_manager.user.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
@DisplayName("ProductController Integration Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "ProductService")
    private ProductService productService;

    private ProductDto testProductDto;
    private List<ProductDto> testProducts;
    private CustomUserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        // Create test user
        AppUser testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole("USER");
        testUserDetails = new CustomUserDetails(testUser);

        testProductDto = new ProductDto();
        testProductDto.setId(1L);
        testProductDto.setName("Test Product");
        testProductDto.setManufacturer("Test Manufacturer");
        testProductDto.setCategory(Category.FRESH_PRODUCE);
        testProductDto.setProductCode("TEST-001");
        testProductDto.setDescription("Test Description");
        testProductDto.setPrice(new BigDecimal("99.99"));

        ProductDto testProductDto2 = new ProductDto();
        testProductDto2.setId(2L);
        testProductDto2.setName("Test Product 2");
        testProductDto2.setManufacturer("Test Manufacturer 2");
        testProductDto2.setCategory(Category.BAKERY_PRODUCTS);
        testProductDto2.setProductCode("TEST-002");
        testProductDto2.setDescription("Test Description 2");
        testProductDto2.setPrice(new BigDecimal("49.99"));

        testProducts = List.of(testProductDto, testProductDto2);
    }

    @Test
    @DisplayName("GET /products - should return product list view")
    @WithMockUser
    void testGetProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(testProducts);

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/product-list"))
                .andExpect(model().attributeExists("productsByCategory"))
                .andExpect(model().attribute("productsByCategory",
                        org.hamcrest.Matchers.instanceOf(Map.class)));

        verify(productService).getAllProducts();
    }

    @Test
    @DisplayName("GET /products/new - should return new product form for admin")
    @WithMockUser(roles = "ADMIN")
    void testNewProductForm() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/product-form"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("GET /products/new - should deny access for regular user")
    void testNewProductFormDeniedForUser() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/new")
                        .with(user(testUserDetails)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /products/{id} - should return product details view")
    void testGetProduct() throws Exception {
        // Arrange
        ProductLocalizationDto localizationDto = new ProductLocalizationDto();
        localizationDto.setShop(new ShopDto(1L, "Test Shop", "123 Test St", "Test City"));
        localizationDto.setAmount(10);
        localizationDto.setTotalPrice(new BigDecimal("999.90"));

        when(productService.getProductDetailsById(1L)).thenReturn(testProductDto);
        when(productService.getLocalizationsForCurrentUser(eq(1L), any(CustomUserDetails.class)))
                .thenReturn(List.of(localizationDto));

        // Act & Assert
        mockMvc.perform(get("/products/{id}", 1L)
                        .with(user(testUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("product/product-details"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("entries"));

        verify(productService).getProductDetailsById(1L);
        verify(productService).getLocalizationsForCurrentUser(eq(1L), any(CustomUserDetails.class));
    }

    @Test
    @DisplayName("POST /products - should create product and redirect for admin")
    @WithMockUser(roles = "ADMIN")
    void testAddProduct() throws Exception {
        // Arrange
        doNothing().when(productService).saveProduct(any(ProductDto.class));

        // Act & Assert
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .param("name", "New Product")
                        .param("manufacturer", "New Manufacturer")
                        .param("category", "FRESH_PRODUCE")
                        .param("productCode", "NEW-001")
                        .param("description", "New Description")
                        .param("price", "79.99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService).saveProduct(any(ProductDto.class));
    }

    @Test
    @DisplayName("POST /products - should return form with errors when validation fails")
    @WithMockUser(roles = "ADMIN")
    void testAddProductValidationFailure() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .param("name", "")  // Blank name should fail validation
                        .param("manufacturer", "Test Manufacturer")
                        .param("category", "FRESH_PRODUCE")
                        .param("productCode", "TEST-001")
                        .param("price", "99.99"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/product-form"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeHasFieldErrors("product", "name"));

        verify(productService, never()).saveProduct(any(ProductDto.class));
    }

    @Test
    @DisplayName("POST /products - should deny access for regular user")
    void testAddProductDeniedForUser() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .with(user(testUserDetails))
                        .param("name", "New Product")
                        .param("manufacturer", "New Manufacturer")
                        .param("category", "FRESH_PRODUCE")
                        .param("productCode", "NEW-001")
                        .param("price", "79.99"))
                .andExpect(status().isForbidden());

        verify(productService, never()).saveProduct(any(ProductDto.class));
    }

    @Test
    @DisplayName("GET /products/edit/{id} - should return edit form for admin")
    @WithMockUser(roles = "ADMIN")
    void testEditProducts() throws Exception {
        // Arrange
        when(productService.getProductDetailsById(1L)).thenReturn(testProductDto);

        // Act & Assert
        mockMvc.perform(get("/products/edit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("product/product-form"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("categories"));

        verify(productService).getProductDetailsById(1L);
    }

    @Test
    @DisplayName("POST /products/edit/{id} - should update product and redirect for admin")
    @WithMockUser(roles = "ADMIN")
    void testUpdateProduct() throws Exception {
        // Arrange
        doNothing().when(productService).updateProduct(eq(1L), any(ProductDto.class));

        // Act & Assert
        mockMvc.perform(post("/products/edit/{id}", 1L)
                        .with(csrf())
                        .param("name", "Updated Product")
                        .param("manufacturer", "Updated Manufacturer")
                        .param("category", "BAKERY_PRODUCTS")
                        .param("productCode", "UPD-001")
                        .param("description", "Updated Description")
                        .param("price", "149.99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService).updateProduct(eq(1L), any(ProductDto.class));
    }

    @Test
    @DisplayName("POST /products/edit/{id} - should return form with errors when validation fails")
    @WithMockUser(roles = "ADMIN")
    void testUpdateProductValidationFailure() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products/edit/{id}", 1L)
                        .with(csrf())
                        .param("name", "")  // Blank name should fail validation
                        .param("manufacturer", "Test Manufacturer")
                        .param("category", "FRESH_PRODUCE")
                        .param("productCode", "TEST-001")
                        .param("price", "-10"))  // Negative price should fail validation
                .andExpect(status().isOk())
                .andExpect(view().name("product/product-form"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeHasFieldErrors("product", "name", "price"));

        verify(productService, never()).updateProduct(eq(1L), any(ProductDto.class));
    }

    @Test
    @DisplayName("POST /products/delete/{id} - should delete product and redirect for admin")
    @WithMockUser(roles = "ADMIN")
    void testDeleteProduct() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProductById(1L);

        // Act & Assert
        mockMvc.perform(post("/products/delete/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService).deleteProductById(1L);
    }

    @Test
    @DisplayName("POST /products/delete/{id} - should deny access for regular user")
    void testDeleteProductDeniedForUser() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products/delete/{id}", 1L)
                        .with(csrf())
                        .with(user(testUserDetails)))
                .andExpect(status().isForbidden());

        verify(productService, never()).deleteProductById(1L);
    }
}
