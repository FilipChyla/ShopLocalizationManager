package edu.chylaozgaoldakowski.location_manager.product;


import edu.chylaozgaoldakowski.location_manager.entry.EntryMapper;
import edu.chylaozgaoldakowski.location_manager.entry.EntryRepository;
import edu.chylaozgaoldakowski.location_manager.user.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service("ProductService")
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final EntryRepository entryRepository;
    private final ProductMapper productMapper;
    private final EntryMapper entryMapper;

    public ProductService(ProductRepository productRepository, EntryRepository entryRepository, ProductMapper productMapper, EntryMapper entryMapper) {
        this.productRepository = productRepository;
        this.entryRepository = entryRepository;
        this.productMapper = productMapper;
        this.entryMapper = entryMapper;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toProductDetailsDto).toList();
    }

    public ProductDto getProductDetailsById(Long id) throws NoSuchElementException {
        Product product = productRepository.findById(id).orElseThrow();
        return productMapper.toProductDetailsDto(product);
    }

    @Override
    public List<ProductLocalizationDto> getLocalizationsForCurrentUser(Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<ProductLocalizationDto> localizations = entryRepository.findByProduct_Id(id)
                .stream()
                .map(entryMapper::toProductLocalizationDto)
                .toList();

        if (currentUser == null) {
            return List.of();
        }

        if (currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return localizations;
        }

        return localizations.stream()
                .filter(
                        productLocalizationDto ->
                                Objects.equals(productLocalizationDto.getShop().getId(), currentUser.getShopId()))
                .toList();

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
