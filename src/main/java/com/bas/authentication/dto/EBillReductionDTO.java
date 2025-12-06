package com.bas.authentication.dto;


public class EBillReductionDTO {
    private Long billId;
    private Double totalReduction;
    private Double cgst;
    private Double sgst;
    private Double royalty;

    // Getters and Setters
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public Double getTotalReduction() { return totalReduction; }
    public void setTotalReduction(Double totalReduction) { this.totalReduction = totalReduction; }
    public Double getCgst() { return cgst; }
    public void setCgst(Double cgst) { this.cgst = cgst; }
    public Double getSgst() { return sgst; }
    public void setSgst(Double sgst) { this.sgst = sgst; }
    public Double getRoyalty() { return royalty; }
    public void setRoyalty(Double royalty) { this.royalty = royalty; }
}
