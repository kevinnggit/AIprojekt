package com.nspace.controller;

import com.nspace.model.PortfolioItem;
import com.nspace.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // 🌍 PUBLIC ENDPOINT: Jeder darf auf GET /api/portfolio zugreifen.
    // Dies wurde in SecurityConfig.java speziell gewhitelistet
    // (.requestMatchers("/api/portfolio").permitAll())
    @GetMapping("/portfolio")
    public List<PortfolioItem> getAllItems() {
        return portfolioService.getAllItems();
    }

    // 🔒 SECURTED ENDPOINT: Nur Admins und eingeloggte User dürfen POST/DELETE
    // machen.
    // Der JWT Filter prüft hier vorab Token und Rollen.
    @PostMapping("/admin/portfolio")
    public PortfolioItem createItem(@RequestBody PortfolioItem item) {
        return portfolioService.createItem(item);
    }

    // 🔒 ADMIN ONLY: Löschen
    @DeleteMapping("/admin/portfolio/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        portfolioService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
