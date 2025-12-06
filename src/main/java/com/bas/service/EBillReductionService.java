package com.bas.service;

import com.bas.ao.repository.EBillRepository;
import com.bas.authentication.dto.EBillDTO;
import com.bas.authentication.dto.ItemResponseDTO;
import com.bas.model.EBill;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EBillReductionService {

	   private final EBillRepository eBillRepository;
	    private final ObjectMapper objectMapper; // inject ObjectMapper

	    public EBillReductionService(EBillRepository eBillRepository, ObjectMapper objectMapper) {
	        this.eBillRepository = eBillRepository;
	        this.objectMapper = objectMapper;
	    }
    @Transactional
    public EBillDTO applyReduction(Long billId, Double totalReduction, Double cgst, Double sgst, Double royalty) throws JsonMappingException, JsonProcessingException {
        // 1️⃣ Fetch the existing bill
        EBill bill = eBillRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("EBill not found with id: " + billId));

        // 2️⃣ Update reduction/tax fields
        bill.setAppliedReduction(true);
        bill.setTotalReduction(totalReduction != null ? totalReduction : 0.0);
        bill.setCgst(cgst != null ? cgst : 0.0);
        bill.setSgst(sgst != null ? sgst : 0.0);
        bill.setRoyalty(royalty != null ? royalty : 0.0);

        double netAmount = bill.getTotalAmount() - bill.getTotalReduction();
        bill.setNetAmount(netAmount);
        
        // 3️⃣ Optionally, recalculate final amount if you want to store it
        // Double finalAmount = bill.getFinalAmount();
        // bill.setTotalAmount(finalAmount); // optional, if you want to overwrite

        // 4️⃣ Save the bill
        bill.setAppliedReduction(true);
         eBillRepository.save(bill);
         
      // Convert items JSON to list
         List<ItemResponseDTO> items = objectMapper.readValue(
                 bill.getItemsJson(),
                 objectMapper.getTypeFactory().constructCollectionType(List.class, ItemResponseDTO.class)
         );

         
         // Map bill to DTO
         EBillDTO dto = new EBillDTO();
         dto.setBillId(bill.getId());
         dto.setWorkId(bill.getWork().getWorkId());
         dto.setWorkName(bill.getWork().getName());
         dto.setContractorName(bill.getContractor().getName());
         dto.setTotalQuantity(bill.getTotalQuantity());
         dto.setTotalAmount(bill.getTotalAmount());
         dto.setTotalReduction(bill.getTotalReduction());
         dto.setCgst(bill.getCgst());
         dto.setSgst(bill.getSgst());
         dto.setRoyalty(bill.getRoyalty());
         dto.setNetAmount(bill.getNetAmount());
         dto.setStatus(bill.getStatus().name());
         dto.setCreatedAt(bill.getCreatedAt().toString());
         dto.setItems(items);
         dto.setReductionIsApplied(bill.isAppliedReduction());

         return dto;
    }
}
