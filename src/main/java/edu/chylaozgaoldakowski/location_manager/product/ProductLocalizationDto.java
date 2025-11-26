package edu.chylaozgaoldakowski.location_manager.product;

import edu.chylaozgaoldakowski.location_manager.shop.ShopDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductLocalizationDto {
    private ShopDto shop;
    private int amount;
    private BigDecimal totalPrice;
}
