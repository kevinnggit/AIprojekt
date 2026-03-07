package com.nspace.controller;

import com.nspace.model.PortfolioItem;
import com.nspace.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für Portfolio-Einträge.
 *
 * <p>Das Lesen des Portfolios ist für alle Besucher der Seite öffentlich zugänglich.
 * Das Erstellen und Löschen von Einträgen ist über den Admin-Pfad ({@code /api/admin/portfolio})
 * geschützt und erfordert eine gültige JWT-Authentifizierung mit Admin-Berechtigung.</p>
 */
@RestController
@RequestMapping("/api")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * Gibt alle Portfolio-Einträge zurück.
     *
     * <p>Dieser Endpunkt ist öffentlich zugänglich – in der {@code SecurityConfig}
     * wurde {@code /api/portfolio} explizit whitegelistet ({@code permitAll()}).</p>
     *
     * @return Liste aller {@link PortfolioItem}-Objekte
     */
    // Öffentlicher Endpunkt: Jeder darf auf GET /api/portfolio zugreifen.
    // Dies wurde in SecurityConfig.java speziell gewhitelistet
    // (.requestMatchers("/api/portfolio").permitAll())
    @GetMapping("/portfolio")
    public List<PortfolioItem> getAllItems() {
        return portfolioService.getAllItems();
    }

    /**
     * Erstellt einen neuen Portfolio-Eintrag.
     *
     * <p>Nur Admins und eingeloggte Benutzer dürfen diesen Endpunkt aufrufen.
     * Der JWT-Filter prüft Token und Rollen vorab.</p>
     *
     * @param item das zu erstellende {@link PortfolioItem}
     * @return das gespeicherte Item mit generierter ID
     */
    // Gesicherter Endpunkt: Nur Admins und eingeloggte User dürfen POST/DELETE machen.
    // Der JWT Filter prüft hier vorab Token und Rollen.
    @PostMapping("/admin/portfolio")
    public PortfolioItem createItem(@RequestBody PortfolioItem item) {
        return portfolioService.createItem(item);
    }

    /**
     * Löscht einen Portfolio-Eintrag anhand seiner ID.
     *
     * <p>Ausschließlich für Admins zugänglich.</p>
     *
     * @param id die ID des zu löschenden Eintrags
     * @return HTTP 204 No Content
     */
    // Nur für Admins: Löschen
    @DeleteMapping("/admin/portfolio/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        portfolioService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
