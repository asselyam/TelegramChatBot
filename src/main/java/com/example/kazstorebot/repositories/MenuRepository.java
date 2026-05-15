package com.example.kazstorebot.repositories;

import com.example.kazstorebot.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategory(MenuItem.Category category);
    Optional<MenuItem> findById(Long id);
}
