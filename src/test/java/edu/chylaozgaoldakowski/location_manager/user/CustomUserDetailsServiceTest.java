package edu.chylaozgaoldakowski.location_manager.user;

import edu.chylaozgaoldakowski.location_manager.shop.Shop;
import edu.chylaozgaoldakowski.location_manager.shop.ShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Unit Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private AppUser testUser;
    private Shop testShop;
    private AppUserDto testUserDto;

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
        testUser.setPassword("password123");
        testUser.setRole("USER");
        testUser.setAssignedShop(testShop);

        testUserDto = new AppUserDto();
        testUserDto.setUsername("newuser");
        testUserDto.setPassword("newpassword123");
        testUserDto.setAssignedShopId(1L);
    }

    @Test
    @DisplayName("loadUserByUsername should return UserDetails when user exists")
    void testLoadUserByUsernameSuccess() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CustomUserDetails.class);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("password123");
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("loadUserByUsername should throw UsernameNotFoundException when user does not exist")
    void testLoadUserByUsernameNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: nonexistent");
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("loadUserByUsername should handle different usernames correctly")
    void testLoadUserByUsernameWithDifferentUsernames() {
        // Arrange
        AppUser adminUser = new AppUser();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setPassword("adminpass");
        adminUser.setRole("ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        // Assert
        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getPassword()).isEqualTo("adminpass");
        verify(userRepository).findByUsername("admin");
    }

    @Test
    @DisplayName("register should create new user with correct details")
    void testRegisterSuccess() {
        // Arrange
        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);

        // Act
        customUserDetailsService.register(testUserDto);

        // Assert
        verify(shopRepository).findById(1L);
        verify(userRepository).save(userCaptor.capture());

        AppUser savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("newuser");
        assertThat(savedUser.getPassword()).isEqualTo("newpassword123");
        assertThat(savedUser.getRole()).isEqualTo("USER");
        assertThat(savedUser.getAssignedShop()).isEqualTo(testShop);
    }

    @Test
    @DisplayName("register should throw exception when shop does not exist")
    void testRegisterShopNotFound() {
        // Arrange
        when(shopRepository.findById(999L)).thenReturn(Optional.empty());

        AppUserDto userDto = new AppUserDto();
        userDto.setUsername("newuser");
        userDto.setPassword("password");
        userDto.setAssignedShopId(999L);

        // Act & Assert
        assertThatThrownBy(() -> customUserDetailsService.register(userDto))
                .isInstanceOf(NoSuchElementException.class);
        verify(shopRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register should always set role to USER regardless of input")
    void testRegisterAlwaysSetsUserRole() {
        // Arrange
        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);

        // Act
        customUserDetailsService.register(testUserDto);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("register should correctly associate user with shop")
    void testRegisterAssociatesUserWithShop() {
        // Arrange
        Shop specificShop = new Shop();
        specificShop.setId(5L);
        specificShop.setName("Specific Shop");
        specificShop.setAddress("456 Specific Ave");
        specificShop.setCity("Specific City");

        AppUserDto userDto = new AppUserDto();
        userDto.setUsername("shopuser");
        userDto.setPassword("password");
        userDto.setAssignedShopId(5L);

        when(shopRepository.findById(5L)).thenReturn(Optional.of(specificShop));
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);

        // Act
        customUserDetailsService.register(userDto);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        AppUser savedUser = userCaptor.getValue();
        assertThat(savedUser.getAssignedShop()).isEqualTo(specificShop);
        assertThat(savedUser.getAssignedShop().getId()).isEqualTo(5L);
        assertThat(savedUser.getAssignedShop().getName()).isEqualTo("Specific Shop");
    }

    @Test
    @DisplayName("loadUserByUsername should handle users with null assigned shop")
    void testLoadUserByUsernameWithNullShop() {
        // Arrange
        AppUser userWithoutShop = new AppUser();
        userWithoutShop.setId(3L);
        userWithoutShop.setUsername("noshopuser");
        userWithoutShop.setPassword("password");
        userWithoutShop.setRole("USER");
        userWithoutShop.setAssignedShop(null);

        when(userRepository.findByUsername("noshopuser")).thenReturn(Optional.of(userWithoutShop));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername("noshopuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("noshopuser");
        verify(userRepository).findByUsername("noshopuser");
    }
}
