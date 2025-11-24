package edu.chylaozgaoldakowski.location_manager.shop;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ShopService")
public class ShopService implements IShopService {

    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;

    public ShopService(ShopRepository shopRepository, ShopMapper shopMapper) {
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
    }

    @Override
    public List<ShopDto> getAll() {
        return shopRepository.findAll().stream().map(shopMapper::toDto).toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void save(ShopDto shopDto) {
        Shop shopEntity = new Shop();
        shopMapper.updateEntityFromDto(shopEntity, shopDto);
        shopRepository.save(shopEntity);
    }

    public ShopDto getById(Long id) {
        Shop shopEntity = shopRepository.findById(id).orElseThrow();
        return shopMapper.toDto(shopEntity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        shopRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void update(Long id, ShopDto updatedDto) {
        Shop existingEntity = shopRepository.findById(id).orElseThrow();
        shopMapper.updateEntityFromDto(existingEntity, updatedDto);
        shopRepository.save(existingEntity);
    }
}
