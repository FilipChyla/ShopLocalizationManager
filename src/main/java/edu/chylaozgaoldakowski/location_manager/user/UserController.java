package edu.chylaozgaoldakowski.location_manager.user;

import edu.chylaozgaoldakowski.location_manager.shop.IShopService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
class UserController {
    private final CustomUserDetailsService userService;
    private final PasswordEncoder passwordEncoder;
    private final IShopService shopService;

    public UserController(CustomUserDetailsService userService, PasswordEncoder passwordEncoder, IShopService shopService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.shopService = shopService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new AppUserDto());
        model.addAttribute("shops", shopService.getAll());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid AppUserDto user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("shops", shopService.getAll());
            return "auth/register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.register(user);
        return "redirect:/login?registered";
    }
}
