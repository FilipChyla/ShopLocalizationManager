package edu.chylaozgaoldakowski.location_manager.shop;

import edu.chylaozgaoldakowski.location_manager.config.SecurityConfig;
import edu.chylaozgaoldakowski.location_manager.entry.EntryDto;
import edu.chylaozgaoldakowski.location_manager.user.AppUser;
import edu.chylaozgaoldakowski.location_manager.user.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShopController.class)
@Import(SecurityConfig.class)
@DisplayName("ShopController Integration Tests")
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "ShopService")
    private IShopService shopService;

    private ShopDto testShopDto;
    private List<ShopDto> testShops;
    private ShopData testShopData;
    private CustomUserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        testShopDto = new ShopDto();
        testShopDto.setId(1L);
        testShopDto.setName("Test Shop");
        testShopDto.setAddress("123 Test St");
        testShopDto.setCity("Test City");

        // Create test user with shop
        AppUser testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole("USER");
        testUserDetails = new CustomUserDetails(testUser);

        ShopDto testShopDto2 = new ShopDto();
        testShopDto2.setId(2L);
        testShopDto2.setName("Test Shop 2");
        testShopDto2.setAddress("456 Test Ave");
        testShopDto2.setCity("Test City 2");

        testShops = List.of(testShopDto, testShopDto2);

        // Setup ShopData for JSON export test
        ShopData.ProductData productData = ShopData.ProductData.builder()
                .name("Test Product")
                .manufacturer("Test Manufacturer")
                .category("FRESH_PRODUCE")
                .productCode("TEST-001")
                .description("Test Description")
                .build();

        ShopData.EntryData entryData = ShopData.EntryData.builder()
                .product(productData)
                .amount(10)
                .totalPrice(new BigDecimal("999.90"))
                .build();

        testShopData = ShopData.builder()
                .name("Test Shop")
                .address("123 Test St")
                .city("Test City")
                .entries(List.of(entryData))
                .build();
    }

    @Test
    @DisplayName("GET /shops - should return shop list view")
    @WithMockUser
    void testGetShops() throws Exception {
        // Arrange
        when(shopService.getAll()).thenReturn(testShops);

        // Act & Assert
        mockMvc.perform(get("/shops"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop/shop-list"))
                .andExpect(model().attributeExists("shops"))
                .andExpect(model().attribute("shops", hasSize(2)));

        verify(shopService).getAll();
    }

    @Test
    @DisplayName("GET /shops/new - should return new shop form for admin")
    @WithMockUser(roles = "ADMIN")
    void testAddShop() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/shops/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop/shop-form"))
                .andExpect(model().attributeExists("shop"));
    }

    @Test
    @DisplayName("GET /shops/new - should deny access for regular user")
    void testAddShopDeniedForUser() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/shops/new")
                        .with(user(testUserDetails)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /shops/{id} - should return shop details view")
    void testViewShop() throws Exception {
        // Arrange
        EntryDto entryDto = new EntryDto();
        entryDto.setId(1L);
        entryDto.setAmount(10);
        entryDto.setTotalPrice(new BigDecimal("999.90"));

        when(shopService.getById(1L)).thenReturn(testShopDto);
        when(shopService.getEntriesById(1L)).thenReturn(List.of(entryDto));

        // Act & Assert
        mockMvc.perform(get("/shops/{id}", 1L)
                        .with(user(testUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("shop/shop-details"))
                .andExpect(model().attributeExists("shop"))
                .andExpect(model().attributeExists("entries"));

        verify(shopService).getById(1L);
        verify(shopService).getEntriesById(1L);
    }

    @Test
    @DisplayName("POST /shops - should create shop and redirect for admin")
    @WithMockUser(roles = "ADMIN")
    void testSaveShop() throws Exception {
        // Arrange
        doNothing().when(shopService).save(any(ShopDto.class));

        // Act & Assert
        mockMvc.perform(post("/shops")
                        .with(csrf())
                        .param("name", "New Shop")
                        .param("address", "789 New St")
                        .param("city", "New City"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shops"));

        verify(shopService).save(any(ShopDto.class));
    }

    @Test
    @DisplayName("POST /shops - should return form with errors when validation fails")
    @WithMockUser(roles = "ADMIN")
    void testSaveShopValidationFailure() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/shops")
                        .with(csrf())
                        .param("name", "")  // Blank name should fail validation
                        .param("address", "789 New St")
                        .param("city", "New City"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop/shop-form"))
                .andExpect(model().attributeHasFieldErrors("shop", "name"));

        verify(shopService, never()).save(any(ShopDto.class));
    }

    @Test
    @DisplayName("POST /shops - should deny access for regular user")
    void testSaveShopDeniedForUser() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/shops")
                        .with(csrf())
                        .with(user(testUserDetails))
                        .param("name", "New Shop")
                        .param("address", "789 New St")
                        .param("city", "New City"))
                .andExpect(status().isForbidden());

        verify(shopService, never()).save(any(ShopDto.class));
    }

    @Test
    @DisplayName("GET /shops/edit/{id} - should return edit form for admin")
    @WithMockUser(roles = "ADMIN")
    void testEditShop() throws Exception {
        // Arrange
        when(shopService.getById(1L)).thenReturn(testShopDto);

        // Act & Assert
        mockMvc.perform(get("/shops/edit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("shop/shop-form"))
                .andExpect(model().attributeExists("shop"));

        verify(shopService).getById(1L);
    }

    @Test
    @DisplayName("GET /shops/edit/{id} - should deny access for regular user")
    void testEditShopDeniedForUser() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/shops/edit/{id}", 1L)
                        .with(user(testUserDetails)))
                .andExpect(status().isForbidden());

        verify(shopService, never()).getById(1L);
    }

    @Test
    @DisplayName("POST /shops/edit/{id} - should update shop and redirect for admin")
    @WithMockUser(roles = "ADMIN")
    void testUpdateShop() throws Exception {
        // Arrange
        doNothing().when(shopService).update(eq(1L), any(ShopDto.class));

        // Act & Assert
        mockMvc.perform(post("/shops/edit/{id}", 1L)
                        .with(csrf())
                        .param("name", "Updated Shop")
                        .param("address", "Updated Address")
                        .param("city", "Updated City"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shops"));

        verify(shopService).update(eq(1L), any(ShopDto.class));
    }

    @Test
    @DisplayName("POST /shops/edit/{id} - should return form with errors when validation fails")
    @WithMockUser(roles = "ADMIN")
    void testUpdateShopValidationFailure() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/shops/edit/{id}", 1L)
                        .with(csrf())
                        .param("name", "Updated Shop")
                        .param("address", "")  // Blank address should fail validation
                        .param("city", ""))    // Blank city should fail validation
                .andExpect(status().isOk())
                .andExpect(view().name("shop/shop-form"))
                .andExpect(model().attributeHasFieldErrors("shop", "address", "city"));

        verify(shopService, never()).update(eq(1L), any(ShopDto.class));
    }

    @Test
    @DisplayName("POST /shops/edit/{id} - should deny access for regular user")
    void testUpdateShopDeniedForUser() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/shops/edit/{id}", 1L)
                        .with(csrf())
                        .with(user(testUserDetails))
                        .param("name", "Updated Shop")
                        .param("address", "Updated Address")
                        .param("city", "Updated City"))
                .andExpect(status().isForbidden());

        verify(shopService, never()).update(eq(1L), any(ShopDto.class));
    }

    @Test
    @DisplayName("POST /shops/delete/{id} - should delete shop and redirect for admin")
    @WithMockUser(roles = "ADMIN")
    void testDeleteShop() throws Exception {
        // Arrange
        doNothing().when(shopService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(post("/shops/delete/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shops"));

        verify(shopService).deleteById(1L);
    }

    @Test
    @DisplayName("POST /shops/delete/{id} - should deny access for regular user")
    void testDeleteShopDeniedForUser() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/shops/delete/{id}", 1L)
                        .with(csrf())
                        .with(user(testUserDetails)))
                .andExpect(status().isForbidden());

        verify(shopService, never()).deleteById(1L);
    }

    @Test
    @DisplayName("GET /shops/{id}/shop-data-download - should return JSON response")
    @WithMockUser
    void testDownloadShopData() throws Exception {
        // Arrange
        when(shopService.getShopDataById(1L)).thenReturn(testShopData);

        // Act & Assert
        mockMvc.perform(get("/shops/{id}/shop-data-download", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("shop-1.json")))
                .andExpect(jsonPath("$.name").value("Test Shop"))
                .andExpect(jsonPath("$.address").value("123 Test St"))
                .andExpect(jsonPath("$.city").value("Test City"))
                .andExpect(jsonPath("$.entries", hasSize(1)))
                .andExpect(jsonPath("$.entries[0].amount").value(10))
                .andExpect(jsonPath("$.entries[0].totalPrice").value(999.90))
                .andExpect(jsonPath("$.entries[0].product.name").value("Test Product"));

        verify(shopService).getShopDataById(1L);
    }
}
