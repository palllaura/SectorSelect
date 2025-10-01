package com.laurapall.sectorselect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
public class Submission {
    /**
     * ID of submission.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of user.
     */
    @Column(nullable = false)
    @NotBlank
    private String name;

    /**
     * Agree to terms.
     */
    @Column(nullable = false)
    private boolean agree;

    /**
     * Sectors selected in submission.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "submission_sectors",
            joinColumns = @JoinColumn(name = "submission_id"),
            inverseJoinColumns = @JoinColumn(name = "sector_id"))
    private Set<Sector> sectors = new LinkedHashSet<>();

}
