package edu.chylaozgaoldakowski.location_manager.product;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final IProductService productService;

    public ProductController(@Qualifier("ProductService") ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String getProducts(Model model) {
        List<ProductDto> products = productService.getAllProducts();
        Map<Category, List<ProductDto>> productsByCategory = products.stream()
                .collect(Collectors.groupingBy(ProductDto::getCategory));

        model.addAttribute("productsByCategory", productsByCategory);
        return "product/product-list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String newProductForm(Model model) {
        model.addAttribute("product", new ProductDto());
        model.addAttribute("categories", Category.values());
        return "product/product-form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addProduct(@Valid @ModelAttribute("product") ProductDto product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", Category.values());
            return "product/product-form";
        }
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public String getProduct(Model model, @PathVariable Long id) {
        model.addAttribute("product", productService.getProductDetailsById(id));

        return "product/product-details";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editProducts(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductDetailsById(id));
        model.addAttribute("categories", Category.values());
        return "product/product-form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute("product") ProductDto product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", Category.values());
            return "product/product-form";
        }
        productService.updateProduct(id, product);
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
}
