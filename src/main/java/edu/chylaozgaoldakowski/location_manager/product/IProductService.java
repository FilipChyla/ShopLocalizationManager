package edu.chylaozgaoldakowski.location_manager.product;

import edu.chylaozgaoldakowski.location_manager.user.CustomUserDetails;

import java.util.List;

public interface IProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductDetailsById(Long id);
    void saveProduct(ProductDto productDto);
    void deleteProductById(Long id);
    void updateProduct(Long id, ProductDto updatedProduct);
    List<ProductLocalizationDto> getLocalizationsForCurrentUser(Long id, CustomUserDetails currentUser);
}
