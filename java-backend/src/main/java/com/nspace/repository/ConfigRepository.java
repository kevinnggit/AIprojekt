package com.nspace.repository;

import com.nspace.model.GlobalConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<GlobalConfig, String> {
}
