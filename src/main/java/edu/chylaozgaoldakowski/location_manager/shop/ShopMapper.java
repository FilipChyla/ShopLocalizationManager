package edu.chylaozgaoldakowski.location_manager.shop;

import edu.chylaozgaoldakowski.location_manager.entry.Entry;
import edu.chylaozgaoldakowski.location_manager.product.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShopMapper {
    public ShopDto toDto(Shop shop) {
        return new ShopDto(shop.getId(), shop.getName(), shop.getAddress(), shop.getCity());
    }

    public void updateEntityFromDto(Shop shop, ShopDto shopDto) {
        shop.setName(shopDto.getName());
        shop.setAddress(shopDto.getAddress());
        shop.setCity(shopDto.getCity());
    }

    public ShopData toShopData(Shop shop, List<Entry> entries) {
        return ShopData.builder()
                .name(shop.getName())
                .address(shop.getAddress())
                .city(shop.getCity())
                .entries(entries.stream()
                        .map(ShopMapper::toEntryData)
                        .toList()).build();
    }

    private static ShopData.EntryData toEntryData(Entry e) {
        return ShopData.EntryData.builder()
                .amount(e.getAmount())
                .totalPrice(e.getTotalPrice())
                .product(toProductData(e.getProduct()))
                .build();
    }

    private static ShopData.ProductData toProductData(Product p) {
        return ShopData.ProductData.builder()
                .name(p.getName())
                .manufacturer(p.getManufacturer())
                .category(p.getCategory().toString().toLowerCase())
                .productCode(p.getProductCode())
                .description(p.getDescription() != null ? p.getDescription() : "")
                .build();
    }
}
