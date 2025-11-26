package edu.chylaozgaoldakowski.location_manager.shop;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/shops")
public class ShopController {

    private final IShopService shopService;

    public ShopController(@Qualifier("ShopService") IShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    public String getShops(Model model) {
        model.addAttribute("shops", shopService.getAll());
        return "shop/shop-list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addShop(Model model) {
        model.addAttribute("shop", new ShopDto());
        return "shop/shop-form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String saveShop(@Valid @ModelAttribute("shop") ShopDto shop, BindingResult result) {
        if (result.hasErrors()) {
            return "shop/shop-form";
        }
        shopService.save(shop);
        return "redirect:/shops";
    }

    @GetMapping("/{id}")
    public String viewShop(@PathVariable Long id, Model model) {
        model.addAttribute("shop", shopService.getById(id));
        model.addAttribute("entries", shopService.getEntriesById(id));
        return "shop/shop-details";

    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editShop(@PathVariable Long id, Model model) {
            model.addAttribute("shop", shopService.getById(id));
            return "shop/shop-form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateShop(@PathVariable Long id, @Valid @ModelAttribute("shop") ShopDto shop, BindingResult result) {
        if (result.hasErrors()) {
            return "shop/shop-form";
        }
        shopService.update(id, shop);
        return "redirect:/shops";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteShop(@PathVariable Long id) {
            shopService.deleteById(id);
            return "redirect:/shops";
    }

    @GetMapping("/{id}/shop-data-download")
    public ResponseEntity<ShopData> downloadShopData(@PathVariable Long id) {
        ShopData shopData = shopService.getShopDataById(id);
        String filename = "shop-" + id + ".json";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.builder("attachment")
                        .filename(filename, StandardCharsets.UTF_8)
                        .build().toString())
                .body(shopData);
    }
}