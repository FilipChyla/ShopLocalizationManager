package edu.chylaozgaoldakowski.location_manager.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    @NotBlank(message = "Product name cannot be blank")
    private String name;
    @NotBlank(message = "Product manufacturer cannot be blank")
    private String manufacturer;
    @NotNull(message = "Product category cannot be blank")
    private Category category;
    @NotBlank(message = "Product code cannot be blank")
    private String productCode;
    private String description;
    @Positive(message = "Product price must be positive")
    private BigDecimal price;
}
