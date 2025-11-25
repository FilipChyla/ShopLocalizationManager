package edu.chylaozgaoldakowski.location_manager.entry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByProduct_Id(Long productId);
    List<Entry> findByShop_Id(Long shopId);
}
