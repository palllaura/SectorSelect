package com.laurapall.sectorselect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
    private Sector parent;

    /**
     * Hierarchy level, starts at 0.
     */
    private Integer level;

}
