package edu.chylaozgaoldakowski.location_manager.entry;

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
public class EntryDto {
    private Long id;
    @NotNull(message = "Shop cannot be null")
    private Long shopId;
    @NotNull(message = "You must choose a product")
    private Long productId;
    private String productName;
    @Positive(message = "Entry amount must be positive")
    private int amount;
    private BigDecimal totalPrice;
}
