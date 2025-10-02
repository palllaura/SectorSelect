package com.laurapall.sectorselect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmissionRequest {
    @NotBlank
    private String name;

    @NotEmpty
    private List<Long> sectorIds;

    @NotNull
    private Boolean agree;
}
