package edu.chylaozgaoldakowski.location_manager.shop;

import edu.chylaozgaoldakowski.location_manager.entry.EntryDto;
import edu.chylaozgaoldakowski.location_manager.entry.EntryMapper;
import edu.chylaozgaoldakowski.location_manager.entry.EntryRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ShopService")
public class ShopService implements IShopService {

    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;
    private final EntryMapper entryMapper;
    private final EntryRepository entryRepository;

    public ShopService(ShopRepository shopRepository, ShopMapper shopMapper, EntryMapper entryMapper, EntryRepository entryRepository) {
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
        this.entryMapper = entryMapper;
        this.entryRepository = entryRepository;
    }

    @Override
    public List<ShopDto> getAll() {
        return shopRepository.findAll().stream().map(shopMapper::toDto).toList();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void save(ShopDto shopDto) {
        Shop shopEntity = new Shop();
        shopMapper.updateEntityFromDto(shopEntity, shopDto);
        shopRepository.save(shopEntity);
    }

    @Override
    public ShopDto getById(Long id) {
        Shop shopEntity = shopRepository.findById(id).orElseThrow();
        return shopMapper.toDto(shopEntity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void deleteById(Long id) {
        shopRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void update(Long id, ShopDto updatedDto) {
        Shop existingEntity = shopRepository.findById(id).orElseThrow();
        shopMapper.updateEntityFromDto(existingEntity, updatedDto);
        shopRepository.save(existingEntity);
    }

    public List<EntryDto> getEntriesById(Long id) {
        return entryRepository.findByShop_Id(id).stream().map(entryMapper::toDto).toList();
    }
}
