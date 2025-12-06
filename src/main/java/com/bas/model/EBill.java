package com.bas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ebill")
public class EBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to Work
    @ManyToOne
    @JoinColumn(name = "work_id")
    private Work work;

    // Link to Contractor
    @ManyToOne
    @JoinColumn(name = "contractor_id")
    private Contractor contractor;

    private Double totalQuantity;
    private Double totalAmount;

    // Save items array as JSON
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String itemsJson;

    @Enumerated(EnumType.STRING)
    private EBillStatus status = EBillStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
    
    
    private boolean appliedReduction = false; // true if reduction applied
    private Double cgst = 0.0;               // CGST amount
    private Double sgst = 0.0;               // SGST amount
    private Double royalty = 0.0;            // Royalty or other fees
    private Double totalReduction = 0.0;     // Total reduction amount
    
    
    @Column(name = "net_amount")
    private Double netAmount = 0.0;

    // Add getter and setter
    public Double getNetAmount() { return netAmount; }
    public void setNetAmount(Double netAmount) { this.netAmount = netAmount; }

    

    // Getters & Setters

    public boolean isAppliedReduction() {
		return appliedReduction;
	}
	public void setAppliedReduction(boolean appliedReduction) {
		this.appliedReduction = appliedReduction;
	}
	public Double getCgst() {
		return cgst;
	}
	public void setCgst(Double cgst) {
		this.cgst = cgst;
	}
	public Double getSgst() {
		return sgst;
	}
	public void setSgst(Double sgst) {
		this.sgst = sgst;
	}
	public Double getRoyalty() {
		return royalty;
	}
	public void setRoyalty(Double royalty) {
		this.royalty = royalty;
	}
	public Double getTotalReduction() {
		return totalReduction;
	}
	public void setTotalReduction(Double totalReduction) {
		this.totalReduction = totalReduction;
	}
	
	
	
	
	 // Method to calculate final payable amount
    public Double getFinalAmount() {
        double finalAmt = (totalAmount != null ? totalAmount : 0.0)
                        - (totalReduction != null ? totalReduction : 0.0)
                        + (cgst != null ? cgst : 0.0)
                        + (sgst != null ? sgst : 0.0)
                        + (royalty != null ? royalty : 0.0);
        return finalAmt;
    }
	
	
	
	
	
	
	
	
	
	public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Work getWork() { return work; }
    public void setWork(Work work) { this.work = work; }

    public Contractor getContractor() { return contractor; }
    public void setContractor(Contractor contractor) { this.contractor = contractor; }

    public Double getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Double totalQuantity) { this.totalQuantity = totalQuantity; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getItemsJson() { return itemsJson; }
    public void setItemsJson(String itemsJson) { this.itemsJson = itemsJson; }

    public EBillStatus getStatus() { return status; }
    public void setStatus(EBillStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
