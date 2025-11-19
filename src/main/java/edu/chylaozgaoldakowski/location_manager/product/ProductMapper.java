package edu.chylaozgaoldakowski.location_manager.product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductDto toProductDetailsDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getManufacturer(),
                product.getCategory(),
                product.getProductCode(),
                product.getDescription(),
                product.getPrice());
    }

    public void updateEntityFromDto(Product productEntity, ProductDto productDto) {
        productEntity.setName(productDto.getName());
        productEntity.setManufacturer(productDto.getManufacturer());
        productEntity.setCategory(productDto.getCategory());
        productEntity.setProductCode(productDto.getProductCode());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setPrice(productDto.getPrice());
    }
}
