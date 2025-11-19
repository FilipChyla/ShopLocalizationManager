package edu.chylaozgaoldakowski.location_manager.product;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service("ProductService")
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toProductDetailsDto).toList();
    }

    public ProductDto getProductDetailsById(Long id) throws NoSuchElementException {
        Product product = productRepository.findById(id).orElseThrow();
        return productMapper.toProductDetailsDto(product);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void saveProduct(ProductDto productDto) {
        Product productEntity = new Product();
        productMapper.updateEntityFromDto(productEntity, productDto);
        productRepository.save(productEntity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateProduct(Long id, ProductDto updatedProduct) {
        Product existingProduct = productRepository.findById(id).orElseThrow();
        productMapper.updateEntityFromDto(existingProduct, updatedProduct);
        productRepository.save(existingProduct);
    }
}
