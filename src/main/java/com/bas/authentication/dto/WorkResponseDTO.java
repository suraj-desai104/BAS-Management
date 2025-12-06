package com.bas.authentication.dto;



import java.util.List;

public class WorkResponseDTO {

    private String workId;
    private String workName;
    private String contractorName;
    private List<ItemResponseDTO> items;
    private Double totalQuantity;
    private Double totalAmount;
	public String getWorkId() {
		return workId;
	}
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	public String getWorkName() {
		return workName;
	}
	public void setWorkName(String workName) {
		this.workName = workName;
	}
	public String getContractorName() {
		return contractorName;
	}
	public void setContractorName(String contractorName) {
		this.contractorName = contractorName;
	}
	public List<ItemResponseDTO> getItems() {
		return items;
	}
	public void setItems(List<ItemResponseDTO> items) {
		this.items = items;
	}
	public Double getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(Double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

    // Getters & Setters
}
