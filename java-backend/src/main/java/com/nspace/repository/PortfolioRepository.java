package com.nspace.repository;

import com.nspace.model.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository-Interface für den Datenbankzugriff auf {@link PortfolioItem}-Entitäten.
 *
 * <p>Die geerbten Standard-CRUD-Operationen von {@link JpaRepository} sind für
 * die Portfolio-Verwaltung ausreichend. Es sind keine benutzerdefinierten
 * Abfragemethoden erforderlich.</p>
 */
@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioItem, Long> {
    // Basic CRUD is enough
}
