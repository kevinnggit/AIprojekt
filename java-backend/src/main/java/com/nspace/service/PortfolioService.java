package com.nspace.service;

import com.nspace.model.PortfolioItem;
import com.nspace.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public List<PortfolioItem> getAllItems() {
        return portfolioRepository.findAll();
    }

    public PortfolioItem createItem(PortfolioItem item) {
        return portfolioRepository.save(item);
    }

    public void deleteItem(Long id) {
        portfolioRepository.deleteById(id);
    }
}
