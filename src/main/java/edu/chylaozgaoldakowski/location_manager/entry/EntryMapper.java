package edu.chylaozgaoldakowski.location_manager.entry;

import edu.chylaozgaoldakowski.location_manager.product.ProductLocalizationDto;
import edu.chylaozgaoldakowski.location_manager.shop.ShopMapper;
import org.springframework.stereotype.Component;

@Component
public class EntryMapper {
    private final ShopMapper shopMapper;

    public EntryMapper(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    public EntryDto toDto(Entry entry) {
        return new EntryDto(
                entry.getId(),
                entry.getShop().getId(),
                entry.getProduct().getId(),
                entry.getProduct().getName(),
                entry.getAmount(),
                entry.getTotalPrice());
    }

    public ProductLocalizationDto toProductLocalizationDto(Entry entry) {
        return new ProductLocalizationDto(
                shopMapper.toDto(entry.getShop()),
                entry.getAmount(),
                entry.getTotalPrice());
    }
}
