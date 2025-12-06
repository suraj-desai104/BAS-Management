package com.bas.authentication.dto;

import java.util.List;

public class EBillDTO {

    private Long billId;
    private String workId;
    private String workName;
    private String contractorName;

    private Double totalQuantity;
    private Double totalAmount;
    
    

    private boolean reductionIsApplied;
    private Double totalReduction;
    private Double cgst;
    private Double sgst;
    private Double royalty;
    private Double netAmount;

    private List<ItemResponseDTO> items;

    private String status;
    private String createdAt;

    // Getters & Setters

    public Long getBillId() { return billId; }
    public boolean isReductionIsApplied() {
		return reductionIsApplied;
	}
	public void setReductionIsApplied(boolean reductionIsApplied) {
		this.reductionIsApplied = reductionIsApplied;
	}
	public void setBillId(Long billId) { this.billId = billId; }

    public String getWorkId() { return workId; }
    public void setWorkId(String workId) { this.workId = workId; }

    public String getWorkName() { return workName; }
    public void setWorkName(String workName) { this.workName = workName; }

    public String getContractorName() { return contractorName; }
    public void setContractorName(String contractorName) { this.contractorName = contractorName; }

    public Double getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Double totalQuantity) { this.totalQuantity = totalQuantity; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getTotalReduction() { return totalReduction; }
    public void setTotalReduction(Double totalReduction) { this.totalReduction = totalReduction; }

    public Double getCgst() { return cgst; }
    public void setCgst(Double cgst) { this.cgst = cgst; }

    public Double getSgst() { return sgst; }
    public void setSgst(Double sgst) { this.sgst = sgst; }

    public Double getRoyalty() { return royalty; }
    public void setRoyalty(Double royalty) { this.royalty = royalty; }

    public Double getNetAmount() { return netAmount; }
    public void setNetAmount(Double netAmount) { this.netAmount = netAmount; }

    public List<ItemResponseDTO> getItems() { return items; }
    public void setItems(List<ItemResponseDTO> items) { this.items = items; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
