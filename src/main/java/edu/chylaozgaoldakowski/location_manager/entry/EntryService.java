package edu.chylaozgaoldakowski.location_manager.entry;

import edu.chylaozgaoldakowski.location_manager.product.Product;
import edu.chylaozgaoldakowski.location_manager.product.ProductRepository;
import edu.chylaozgaoldakowski.location_manager.shop.Shop;
import edu.chylaozgaoldakowski.location_manager.shop.ShopRepository;
import edu.chylaozgaoldakowski.location_manager.user.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("EntryService")
public class EntryService implements IEntryService{
    private final EntryRepository entryRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final EntryMapper entryMapper;

    EntryService(EntryRepository entryRepository, ShopRepository shopRepository, ProductRepository productRepository, EntryMapper entryMapper) {
        this.entryRepository = entryRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.entryMapper = entryMapper;
    }

    @Override
    public void save(EntryDto entryDto, CustomUserDetails currentUser) {
        Shop currentShop = shopRepository.findById(entryDto.getShopId()).orElseThrow();

        if (isUserHasAccessToShop(currentUser, currentShop)){
            Entry newEntry = new Entry();

            newEntry.setShop(currentShop);
            newEntry.setProduct(productRepository.findById(entryDto.getProductId()).orElseThrow());
            newEntry.setAmount(entryDto.getAmount());

            var totalPrice = newEntry.getProduct().getPrice().multiply(BigDecimal.valueOf(newEntry.getAmount()));
            newEntry.setTotalPrice(totalPrice);
            entryRepository.save(newEntry);
        }else {
            throw new AccessDeniedException("Cannot create entry");
        }
    }

    public void deleteById(Long entryId, CustomUserDetails currentUser) {
        Entry entry = entryRepository.findById(entryId).orElseThrow();

        if (isUserHasAccessToShop(currentUser, entry.getShop())){
            entryRepository.deleteById(entryId);
        }else {
            throw new AccessDeniedException("Cannot delete entry");
        }
    }

    @Override
    public EntryDto getById(Long entryId, CustomUserDetails currentUser) {
        Entry entry = entryRepository.findById(entryId).orElseThrow();
        if (isUserHasAccessToShop(currentUser, entry.getShop())) {
            return entryMapper.toDto(entry);
        } else {
            throw new AccessDeniedException("Cannot access entry with id: " + entryId);
        }

    }

    @Override
    public void update(Long id, EntryDto updatedEntry, CustomUserDetails currentUser) {
        Entry entryToUpdate = entryRepository.findById(id).orElseThrow();

        if (isUserHasAccessToShop(currentUser, entryToUpdate.getShop())){
            Product product = productRepository.findById(updatedEntry.getProductId()).orElseThrow();
            entryToUpdate.setProduct(product);

            entryToUpdate.setAmount(updatedEntry.getAmount());

            var totalPrice = entryToUpdate.getProduct().getPrice().multiply(BigDecimal.valueOf(entryToUpdate.getAmount()));
            entryToUpdate.setTotalPrice(totalPrice);

            entryRepository.save(entryToUpdate);
        }else {
            throw new AccessDeniedException("Cannot update entry with id: " + updatedEntry.getId());
        }

    }
    private boolean isUserHasAccessToShop(CustomUserDetails user, Shop shop){
        if (user == null) {
            return false;
        }
        return user.getShopId().equals(shop.getId());
    }
}
