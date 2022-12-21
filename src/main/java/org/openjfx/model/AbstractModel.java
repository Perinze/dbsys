package org.openjfx.model;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractModel {

    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
