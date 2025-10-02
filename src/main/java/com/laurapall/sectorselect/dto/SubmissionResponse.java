package com.laurapall.sectorselect.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmissionResponse {
    private boolean valid;
    private Long id;
    private String name;
    private List<Long> sectorIds;
}
