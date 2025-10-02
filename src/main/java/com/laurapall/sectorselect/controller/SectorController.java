package com.laurapall.sectorselect.controller;

import com.laurapall.sectorselect.dto.SectorTreeDto;
import com.laurapall.sectorselect.service.SectorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sectors")
public class SectorController {
    private final SectorService service;

    /**
     * Controller constructor.
     * @param service sector service.
     */
    public SectorController(SectorService service) {
        this.service = service;
    }

    /**
     * Get sectors hierarchy tree.
     * @return all sectors as SectorTreeDtos in a list.
     */
    @GetMapping("/tree")
    public List<SectorTreeDto> getSectorTree() {
        return service.getSectorTree();
    }
}

