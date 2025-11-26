package edu.chylaozgaoldakowski.location_manager.entry;

import edu.chylaozgaoldakowski.location_manager.user.CustomUserDetails;

public interface IEntryService {
    void save(EntryDto entry, CustomUserDetails currentUser);
    EntryDto getById(Long entryId, CustomUserDetails currentUser);
    void update(Long id, EntryDto updatedEntry, CustomUserDetails currentUser);
    void deleteById(Long id, CustomUserDetails currentUser);
}
