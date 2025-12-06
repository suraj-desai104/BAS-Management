package com.bas.model;


import jakarta.persistence.*;

@Entity
@Table(
	    name = "item",
	    uniqueConstraints = @UniqueConstraint(columnNames = {"item_name", "work_id"})
	)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sr_no")
    private Integer srNo;

    @Column(name = "item_name", nullable = false)
    private String name;

    @Column(name = "rate")
    private Double rate;

    @Column(name = "length")
    private Double length;

    @Column(name = "width")
    private Double width;

    @Column(name = "depth")
    private Double depth;

    @ManyToOne
    @JoinColumn(name = "work_id")
    private Work work;
    
    @ManyToOne
    @JoinColumn(name = "ebill_id") // new column in item table
    private EBill bill;

    public EBill getBill() {
		return bill;
	}

	public void setBill(EBill bill) {
		this.bill = bill;
	}

	// Computed fields (not stored in DB unless needed)
    @Transient
    public Double getQuantity() {
        if (length != null && width != null && depth != null) {
            return length * width * depth;
        }
        return 0.0;
    }

    @Transient
    public Double getTotalRate() {
        if (getQuantity() != null && rate != null) {
            return getQuantity() * rate;
        }
        return 0.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getSrNo() { return srNo; }
    public void setSrNo(Integer srNo) { this.srNo = srNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }
    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }
    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }
    public Double getDepth() { return depth; }
    public void setDepth(Double depth) { this.depth = depth; }
    public Work getWork() { return work; }
    public void setWork(Work work) { this.work = work; }
}
