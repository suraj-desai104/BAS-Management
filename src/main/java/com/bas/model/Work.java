package com.bas.model;


import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "work")
public class Work {

	   @Id
	    @Column(name = "work_id") // use WORK ID from excel
	    private String workId; // Not auto-generated
	   
	   
    @Column(name = "work_name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "contractor_id")
    private Contractor contractor;

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
    private List<Item> items;

    // Getters and Setters
    
    public String getName() { return name; }
    public String getWorkId() {
		return workId;
	}
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	public void setName(String name) { this.name = name; }
    public Contractor getContractor() { return contractor; }
    public void setContractor(Contractor contractor) { this.contractor = contractor; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}

