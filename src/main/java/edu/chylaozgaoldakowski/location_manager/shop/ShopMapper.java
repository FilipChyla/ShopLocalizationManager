package edu.chylaozgaoldakowski.location_manager.shop;

import org.springframework.stereotype.Component;

@Component
public class ShopMapper {
    public ShopDto toDto(Shop shop) {
        return new ShopDto(shop.getId(), shop.getName(),shop.getAddress(),shop.getCity());
    }
    public void updateEntityFromDto(Shop shop, ShopDto shopDto) {
        shop.setName(shopDto.getName());
        shop.setAddress(shopDto.getAddress());
        shop.setCity(shopDto.getCity());
    }
}
