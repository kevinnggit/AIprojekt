package com.nspace.repository;

import com.nspace.model.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioItem, Long> {
    // Basic CRUD is enough
}
