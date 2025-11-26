package edu.chylaozgaoldakowski.location_manager.shop;

import edu.chylaozgaoldakowski.location_manager.entry.EntryDto;

import java.util.List;

public interface IShopService {
    List<ShopDto> getAll();
    void save(ShopDto shopDto);
    ShopDto getById(Long id);
    void deleteById(Long id);
    void update(Long id, ShopDto updatedDto);
    List<EntryDto> getEntriesById(Long id);
    ShopData getShopDataById(Long id);
}
