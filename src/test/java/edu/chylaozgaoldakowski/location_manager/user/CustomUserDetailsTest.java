package edu.chylaozgaoldakowski.location_manager.user;

import edu.chylaozgaoldakowski.location_manager.shop.Shop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CustomUserDetails Unit Tests")
class CustomUserDetailsTest {

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        Shop testShop = new Shop();
        testShop.setId(1L);
        testShop.setName("Test Shop");
        testShop.setAddress("123 Test St");
        testShop.setCity("Test City");

        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRole("USER");
        testUser.setAssignedShop(testShop);
    }

    @Test
    @DisplayName("getAuthorities should return correct authority for USER role")
    void testGetAuthoritiesUserRole() {
        // Arrange
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        // Act
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Assert
        assertThat(authorities).isNotNull();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("getAuthorities should return correct authority for ADMIN role")
    void testGetAuthoritiesAdminRole() {
        // Arrange
        testUser.setRole("ADMIN");
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        // Act
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Assert
        assertThat(authorities).isNotNull();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    @DisplayName("getAuthorities should prefix role with ROLE_")
    void testGetAuthoritiesPrefixesRole() {
        // Arrange
        testUser.setRole("MANAGER");
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        // Act
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Assert
        assertThat(authorities).isNotNull();
        assertThat(authorities).hasSize(1);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_MANAGER");
    }

    @Test
    @DisplayName("getPassword should return user password")
    void testGetPassword() {
        // Arrange
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        // Act
        String password = userDetails.getPassword();

        // Assert
        assertThat(password).isEqualTo("password123");
    }

    @Test
    @DisplayName("getPassword should return different password for different users")
    void testGetPasswordDifferentUser() {
        // Arrange
        testUser.setPassword("differentPassword456");
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        // Act
        String password = userDetails.getPassword();

        // Assert
        assertThat(password).isEqualTo("differentPassword456");
    }

    @Test
    @DisplayName("getUsername should return user username")
    void testGetUsername() {
        // Arrange
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        // Act
        String username = userDetails.getUsername();

        // Assert
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("getUsername should return different username for different users")
    void testGetUsernameDifferentUser() {
        // Arrange
        testUser.setUsername("anotheruser");
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        // Act
        String username = userDetails.getUsername();

        // Assert
        assertThat(username).isEqualTo("anotheruser");
    }

    @Test
    @DisplayName("getShopId should return shop id when shop is assigned")
    void testGetShopIdWithAssignedShop() {
        // Arrange
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        // Act
        Long shopId = userDetails.getShopId();

        // Assert
        assertThat(shopId).isNotNull();
        assertThat(shopId).isEqualTo(1L);
    }

    @Test
    @DisplayName("getShopId should return null when no shop is assigned")
    void testGetShopIdWithoutAssignedShop() {
        // Arrange
        testUser.setAssignedShop(null);
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        // Act
        Long shopId = userDetails.getShopId();

        // Assert
        assertThat(shopId).isNull();
    }

    @Test
    @DisplayName("getShopId should return correct shop id for different shops")
    void testGetShopIdDifferentShop() {
        // Arrange
        Shop anotherShop = new Shop();
        anotherShop.setId(5L);
        anotherShop.setName("Another Shop");
        testUser.setAssignedShop(anotherShop);
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        // Act
        Long shopId = userDetails.getShopId();

        // Assert
        assertThat(shopId).isEqualTo(5L);
    }

    @Test
    @DisplayName("CustomUserDetails should work with complete user information")
    void testCompleteUserDetails() {
        // Arrange
        CustomUserDetails userDetails = new CustomUserDetails(testUser);

        // Assert all methods return expected values
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.getShopId()).isEqualTo(1L);
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("CustomUserDetails should work with minimal user information")
    void testMinimalUserDetails() {
        // Arrange
        AppUser minimalUser = new AppUser();
        minimalUser.setUsername("minimal");
        minimalUser.setPassword("pass");
        minimalUser.setRole("GUEST");
        minimalUser.setAssignedShop(null);

        CustomUserDetails userDetails = new CustomUserDetails(minimalUser);

        // Assert
        assertThat(userDetails.getUsername()).isEqualTo("minimal");
        assertThat(userDetails.getPassword()).isEqualTo("pass");
        assertThat(userDetails.getShopId()).isNull();
        assertThat(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .containsExactly("ROLE_GUEST");
    }
}
