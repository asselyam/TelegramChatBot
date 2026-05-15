package com.example.kazstorebot;


import com.example.kazstorebot.repositories.MenuRepository;
import org.jvnet.hk2.annotations.Service;

import java.util.List;

@Service
public class MenuService {
    private final MenuRepository repo;

    public MenuService(MenuRepository repo) {
        this.repo = repo;
    }

    public List<MenuItem> getByCategory(MenuItem.Category category) {
        return repo.findByCategory(category);
    }

    public MenuItem getById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
