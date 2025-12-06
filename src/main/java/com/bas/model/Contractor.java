package com.bas.model;



import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "contractor")
public class Contractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contractor_name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL)
    private List<Work> works;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Work> getWorks() { return works; }
    public void setWorks(List<Work> works) { this.works = works; }
}
