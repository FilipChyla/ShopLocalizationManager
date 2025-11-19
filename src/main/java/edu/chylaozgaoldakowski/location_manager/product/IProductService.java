package edu.chylaozgaoldakowski.location_manager.product;

import java.util.List;

public interface IProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductDetailsById(Long id);
    void saveProduct(ProductDto productDto);
    void deleteProductById(Long id);
    void updateProduct(Long id, ProductDto updatedProduct);
}
