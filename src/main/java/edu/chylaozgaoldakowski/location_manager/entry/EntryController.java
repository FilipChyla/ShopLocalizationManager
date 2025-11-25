package edu.chylaozgaoldakowski.location_manager.entry;

import edu.chylaozgaoldakowski.location_manager.product.IProductService;
import edu.chylaozgaoldakowski.location_manager.shop.IShopService;
import edu.chylaozgaoldakowski.location_manager.user.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/entries")
class EntryController {
    private final IProductService productService;
    private final IEntryService entryService;
    private final IShopService shopService;

    EntryController(@Qualifier("ProductService") IProductService productService,
                    @Qualifier("EntryService") IEntryService entryService,
                    @Qualifier("ShopService") IShopService shopService) {
        this.productService = productService;
        this.entryService = entryService;
        this.shopService = shopService;
    }

    @GetMapping("/new")
    public String showAddEntryForm(@RequestParam Long shopId, Model model) {
        EntryDto entry = new EntryDto();
        entry.setShopId(shopId);

        model.addAttribute("entry", entry);
        model.addAttribute("shop", shopService.getById(shopId));
        model.addAttribute("products", productService.getAllProducts());
        return "entry/entry-form";
    }

    @PostMapping
    public String saveEntry(@Valid @ModelAttribute EntryDto entry,
                            BindingResult result,
                            Model model,
                            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllProducts());
            return "entry/entry-form";
        }
        entryService.save(entry, currentUser);
        return "redirect:/shops/" + entry.getShopId();
    }

    @GetMapping("/edit/{entryId}")
    public String showEditEntryForm(@PathVariable Long entryId,
                                    Model model,
                                    @AuthenticationPrincipal CustomUserDetails currentUser) {
        EntryDto entry = entryService.getById(entryId, currentUser);
        model.addAttribute("entry", entry);
        model.addAttribute("shop", shopService.getById(entry.getShopId()));
        model.addAttribute("products", productService.getAllProducts());
        return "entry/entry-form";
    }

    @PostMapping("/update/{id}")
    public String updateEntry(@PathVariable Long id,
                              @Valid @ModelAttribute EntryDto updatedEntry,
                              BindingResult result,
                              Model model,
                              @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllProducts());
            return "entry/entry-form";
        }
        entryService.update(id, updatedEntry, currentUser);
        return "redirect:/shops/" + updatedEntry.getShopId();
    }

    @PostMapping("/delete/{id}")
    public String deleteEntry(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        Long shopId = entryService.getById(id, currentUser).getShopId();
        entryService.deleteById(id, currentUser);
        return "redirect:/shops/" + shopId;
    }
}
