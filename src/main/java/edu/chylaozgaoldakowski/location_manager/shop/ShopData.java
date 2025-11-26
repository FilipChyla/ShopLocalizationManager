package edu.chylaozgaoldakowski.location_manager.shop;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class ShopData {
    private String name;
    private String address;
    private String city;
    private List<EntryData> entries;

    @Getter
    @Setter
    @Builder
    public static class EntryData {
        private ProductData product;
        private int amount;
        private BigDecimal totalPrice;
    }
    @Getter
    @Setter
    @Builder
    public static class ProductData {
        private String name;
        private String manufacturer;
        private String category;
        private String productCode;
        private String description;
    }
}
