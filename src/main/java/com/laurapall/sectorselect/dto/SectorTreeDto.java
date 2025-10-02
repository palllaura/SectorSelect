package com.laurapall.sectorselect.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SectorTreeDto {
    private Long id;
    private String name;
    private Integer level;
    private List<SectorTreeDto> children = new ArrayList<>();

    public SectorTreeDto(Long id, String name, Integer level) {
        this.id = id; this.name = name; this.level = level;
    }
}
