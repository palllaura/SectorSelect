package com.laurapall.sectorselect.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laurapall.sectorselect.dto.SectorJson;
import com.laurapall.sectorselect.entity.Sector;
import com.laurapall.sectorselect.repository.SectorRepository;
import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data loader class to read sector data from file and add sectors to database.
 */
@Service
public class SectorDataLoader implements CommandLineRunner {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(SectorDataLoader.class);
    private final SectorRepository repository;
    private final EntityManager entityManager;

    /**
     * Sector data loader constructor.
     *
     * @param repository SectorRepository.
     */
    public SectorDataLoader(SectorRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        loadIfEmpty();
    }

    @Transactional
    public void loadIfEmpty() throws IOException {
        if (repository.count() > 0) {
            LOGGER.info("Sectors already present; skipping load.");
            return;
        }

        ClassPathResource resource = new ClassPathResource("data/sectors.json");
        if (!resource.exists()) {
            LOGGER.error("sectors.json not found on classpath: {}", resource.getPath());
            return;
        }

        try (InputStream is = resource.getInputStream()) {
            List<SectorJson> list = MAPPER.readValue(is, new TypeReference<List<SectorJson>>() {
            });

            Map<Long, Sector> sectorMap = new LinkedHashMap<>();

            for (SectorJson sectorJson : list) {
                Sector sector = new Sector();
                sector.setId(sectorJson.getId());
                sector.setName(sectorJson.getName());
                sector.setParent(null);
                sectorMap.put(sector.getId(), sector);
            }

            for (SectorJson sectorJson : list) {
                Long parentId = sectorJson.getParentId();
                if (parentId != null) {
                    Sector child = sectorMap.get(sectorJson.getId());
                    Sector parent = sectorMap.get(sectorJson.getParentId());
                    if (parent == null) {
                        LOGGER.warn("Parent id {} not found for child {}", sectorJson.getParentId(), sectorJson.getId());
                    } else {
                        child.setParent(parent);
                        parent.getChildren().add(child);
                    }
                }
            }

            for (Sector sector : sectorMap.values()) {
                int lvl = 0;
                Sector current = sector.getParent();
                while (current != null) {
                    lvl++;
                    current = current.getParent();
                }
                sector.setLevel(lvl);
            }

            for (Sector sector : sectorMap.values()) {
                entityManager.persist(sector);
            }
            entityManager.flush();
            LOGGER.info("Loaded {} sectors from JSON.", sectorMap.size());
        }
    }
}
