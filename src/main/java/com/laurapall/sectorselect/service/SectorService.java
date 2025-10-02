package com.laurapall.sectorselect.service;

import com.laurapall.sectorselect.dto.SectorTreeDto;
import com.laurapall.sectorselect.entity.Sector;
import com.laurapall.sectorselect.repository.SectorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class responsible for all sector-related actions.
 */
@Service
public class SectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SectorService.class);
    private final SectorRepository sectorRepository;

    /**
     * Service constructor.
     * @param sectorRepository sector repository.
     */
    public SectorService(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    /**
     * Construct a hierarchy tree of all sectors in a database.
     * @return all sectors as SectorTreeDtos in a list.
     */
    @Transactional(readOnly = true)
    public List<SectorTreeDto> getSectorTree() {
        List<Sector> allSectors = sectorRepository.findAll();
        List<Sector> roots = allSectors.stream().filter(s -> s.getParent() == null).toList();
        Map<Long, SectorTreeDto> sectorTreeDtos = new HashMap<>();

        for (Sector sector : allSectors) {
            SectorTreeDto dto = new SectorTreeDto(sector.getId(), sector.getName(), sector.getLevel());
            sectorTreeDtos.put(sector.getId(), dto);
        }

        for (Sector sector : allSectors) {
            SectorTreeDto dto = sectorTreeDtos.get(sector.getId());
            if (!sector.getChildren().isEmpty()) {
                for (Sector child : sector.getChildren()) {
                    SectorTreeDto childDto = sectorTreeDtos.get(child.getId());
                    if (childDto != null) dto.getChildren().add(childDto);
                }
            }
        }
        LOGGER.info("Built sector tree with {} root(s) and {} total sectors", roots.size(), allSectors.size());
        return roots.stream().map(r -> sectorTreeDtos.get(r.getId())).toList();
    }
}
