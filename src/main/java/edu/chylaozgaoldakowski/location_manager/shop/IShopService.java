package edu.chylaozgaoldakowski.location_manager.shop;

import java.util.List;

public interface IShopService {
    List<ShopDto> getAll();
    void save(ShopDto shopDto);
    ShopDto getById(Long id);
    void deleteById(Long id);
    void update(Long id, ShopDto updatedDto);
}
