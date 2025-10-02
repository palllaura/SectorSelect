package com.laurapall.sectorselect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Sector {
    /**
     * Unique sector ID.
     */
    @Id
    private Long id;

    /**
     * Sector name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Parent sector for hierarchy (optional).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Sector parent;

    /**
     * Hierarchy level, starts at 0.
     */
    @Column(nullable = false)
    private Integer level;

    /**
     * List with relevant children sectors.
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Sector> children = new ArrayList<>();
}
