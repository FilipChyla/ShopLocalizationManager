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
        ShopData shopData = new ShopData();
        shopData.setName(shop.getName());
        shopData.setAddress(shop.getAddress());
        shopData.setCity(shop.getCity());

        shopData.setEntries(
                entries.stream()
                        .map(ShopMapper::toEntryData)
                        .toList()
        );

        return shopData;
    }
    private static ShopData.EntryData toEntryData(Entry entry) {
        ShopData.EntryData e = new ShopData.EntryData();

        e.setProduct(toProductData(entry.getProduct()));
        e.setAmount(entry.getAmount());
        e.setTotalPrice(entry.getTotalPrice());
        return e;
    }

    private static ShopData.ProductData toProductData(Product product){
        ShopData.ProductData p = new ShopData.ProductData();
        p.setName(product.getName());
        p.setManufacturer(product.getManufacturer());
        p.setCategory(product.getCategory().toString().toLowerCase());
        p.setProductCode(product.getProductCode());
        p.setDescription(product.getDescription() != null ? product.getDescription() : "");
        return p;
    }
}
