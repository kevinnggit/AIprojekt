package com.nspace.service;

import com.nspace.model.PortfolioItem;
import com.nspace.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviceklasse für die Verwaltung von Portfolio-Einträgen.
 *
 * <p>Stellt einfache CRUD-Operationen für {@link PortfolioItem}-Entitäten bereit.
 * Die Geschäftslogik ist hier minimal, da Portfolio-Einträge keine komplexen
 * Validierungsregeln erfordern.</p>
 */
@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    /**
     * Gibt alle Portfolio-Einträge aus der Datenbank zurück.
     *
     * @return Liste aller {@link PortfolioItem}-Objekte
     */
    public List<PortfolioItem> getAllItems() {
        return portfolioRepository.findAll();
    }

    /**
     * Speichert einen neuen oder aktualisierten Portfolio-Eintrag.
     *
     * @param item das zu speichernde {@link PortfolioItem}
     * @return der gespeicherte Eintrag mit generierter ID
     */
    public PortfolioItem createItem(PortfolioItem item) {
        return portfolioRepository.save(item);
    }

    /**
     * Löscht einen Portfolio-Eintrag anhand seiner ID.
     * Existiert kein Eintrag mit der ID, wird keine Ausnahme geworfen (JPA-Verhalten).
     *
     * @param id die ID des zu löschenden Eintrags
     */
    public void deleteItem(Long id) {
        portfolioRepository.deleteById(id);
    }
}
