package edu.chylaozgaoldakowski.location_manager.user;

import edu.chylaozgaoldakowski.location_manager.shop.Shop;
import edu.chylaozgaoldakowski.location_manager.shop.ShopRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    public CustomUserDetailsService(UserRepository userRepository, ShopRepository shopRepository) {
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CustomUserDetails(user);
    }

    public void register(AppUserDto appUser) {
        AppUser newUser = new AppUser();
        newUser.setUsername(appUser.getUsername());
        newUser.setRole("USER");
        newUser.setPassword(appUser.getPassword());

        Shop usersShop = shopRepository.getReferenceById(appUser.getAssignedShopId());
        newUser.setAssignedShop(usersShop);

        userRepository.save(newUser);
    }
}
