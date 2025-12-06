package com.bas.ao.services;

import com.bas.ao.repository.EBillRepository;
import com.bas.authentication.dto.EBillDTO;
import com.bas.authentication.dto.ItemResponseDTO;
import com.bas.authentication.dto.WorkResponseDTO;
import com.bas.model.EBill;
import com.bas.model.EBillStatus;
import com.bas.model.Item;
import com.bas.model.Work;
import com.bas.model.Contractor;
import com.bas.repository.WorkRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AOService {

    private final WorkRepository workRepository;

    @Autowired
    private EBillRepository eBillRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public AOService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    public String saveEBill(WorkResponseDTO dto) {
        try {
            // 1️⃣ Fetch Work entity
            Work work = workRepository.findById(dto.getWorkId())
                    .orElseThrow(() -> new RuntimeException("Work not found"));

            // 2️⃣ Create EBill entity
            EBill bill = new EBill();
            bill.setWork(work);                  // Map Work
            bill.setContractor(work.getContractor()); // Map Contractor

            // 3️⃣ Calculate totals if not provided
            double totalQty = work.getItems().stream()
                    .mapToDouble(Item::getQuantity)
                    .sum();
            double totalAmount = work.getItems().stream()
                    .mapToDouble(Item::getTotalRate)
                    .sum();
            bill.setTotalQuantity(totalQty);
            bill.setTotalAmount(totalAmount);

            // 4️⃣ Convert items list to JSON
            String itemsJson = objectMapper.writeValueAsString(dto.getItems());
            bill.setItemsJson(itemsJson);

            // 5️⃣ Set status
            bill.setStatus(EBillStatus.PENDING);

            // 6️⃣ Save EBill
            eBillRepository.save(bill);

            return "E-Bill saved successfully";

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while saving bill: " + e.getMessage());
        }
    }

    public WorkResponseDTO getWorkDetails(String workId) {

        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new RuntimeException("Work not found"));

        WorkResponseDTO dto = new WorkResponseDTO();
        dto.setWorkId(work.getWorkId());
        dto.setWorkName(work.getName());
        dto.setContractorName(work.getContractor().getName());

        // Map items correctly
        dto.setItems(
                work.getItems()
                        .stream()
                        .map(this::mapItem)
                        .collect(Collectors.toList())
        );

        // Total quantity
        double totalQty = work.getItems()
                .stream()
                .mapToDouble(Item::getQuantity)
                .sum();

        // Total bill amount
        double totalAmount = work.getItems()
                .stream()
                .mapToDouble(Item::getTotalRate)
                .sum();

        dto.setTotalQuantity(totalQty);
        dto.setTotalAmount(totalAmount);

        return dto;
    }

    private ItemResponseDTO mapItem(Item item) {
        ItemResponseDTO dto = new ItemResponseDTO();
        dto.setSrNo(item.getSrNo());
        dto.setItemName(item.getName());
        dto.setRate(item.getRate());
        dto.setLength(item.getLength());
        dto.setWidth(item.getWidth());
        dto.setDepth(item.getDepth());
        dto.setQuantity(item.getQuantity());
        dto.setTotalRate(item.getTotalRate());
        return dto;
    }

    public EBillDTO getEBillByWorkId(String workId) {
        try {
            // Fetch EBill
            EBill eBill = eBillRepository.findByWork_WorkId(workId)
                    .orElseThrow(() -> new RuntimeException("E-Bill not found for workId: " + workId));

            Work work = eBill.getWork();
            Contractor contractor = eBill.getContractor();

            EBillDTO dto = new EBillDTO();
            dto.setBillId(eBill.getId());
            dto.setWorkId(work.getWorkId());
            dto.setWorkName(work.getName());
            dto.setContractorName(contractor != null ? contractor.getName() : null);

            dto.setTotalQuantity(eBill.getTotalQuantity());
            dto.setTotalAmount(eBill.getTotalAmount());

            dto.setStatus(eBill.getStatus().name());
            dto.setCreatedAt(eBill.getCreatedAt().toString());

            // Convert JSON → Items DTO
            List<ItemResponseDTO> itemsList = objectMapper.readValue(
                    eBill.getItemsJson(),
                    new TypeReference<List<ItemResponseDTO>>() {}
            );

            dto.setItems(itemsList);

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Error while fetching E-Bill: " + e.getMessage());
        }
    }
    
    public List<EBillDTO> getAllEbill() {
        List<EBill> bills = eBillRepository.findAll();

        return bills.stream().map(bill -> {
            EBillDTO dto = new EBillDTO();

            dto.setBillId(bill.getId());
            dto.setWorkId(bill.getWork().getWorkId());
            dto.setWorkName(bill.getWork().getName());
            dto.setContractorName(bill.getContractor() != null ? bill.getContractor().getName() : null);

            dto.setTotalQuantity(bill.getTotalQuantity());
            dto.setTotalAmount(bill.getTotalAmount());
            dto.setStatus(bill.getStatus().name());
            dto.setCreatedAt(bill.getCreatedAt().toString());

            try {
                List<ItemResponseDTO> itemsList = objectMapper.readValue(
                    bill.getItemsJson(),
                    new TypeReference<List<ItemResponseDTO>>() {}
                );
                dto.setItems(itemsList);
            } catch (Exception e) {
                dto.setItems(List.of()); // fallback empty list
            }
            dto.setCgst(bill.getCgst());
            dto.setSgst(bill.getSgst());
            dto.setRoyalty(bill.getRoyalty());
            dto.setNetAmount(bill.getNetAmount());
            dto.setReductionIsApplied(bill.isAppliedReduction());
            return dto; 
        }).collect(Collectors.toList());
    }


    public EBillDTO getApproved(String workid) {

        // 1. Fetch approved bill
        EBill bill = eBillRepository.findApprovedBillByWorkId(workid)
                .orElseThrow(() -> new RuntimeException("Approved Bill Not Found"));

        // 2. Create DTO
        EBillDTO dto = new EBillDTO();

        dto.setBillId(bill.getId());
        dto.setWorkId(bill.getWork().getWorkId());
        dto.setWorkName(bill.getWork().getName());
        dto.setContractorName(bill.getContractor().getName());

        dto.setTotalQuantity(bill.getTotalQuantity());
        dto.setTotalAmount(bill.getTotalAmount());

        dto.setReductionIsApplied(bill.isAppliedReduction());
        dto.setTotalReduction(bill.getTotalReduction());
        dto.setCgst(bill.getCgst());
        dto.setSgst(bill.getSgst());
        dto.setRoyalty(bill.getRoyalty());
        dto.setNetAmount(bill.getNetAmount());

        dto.setStatus(bill.getStatus().toString());
        dto.setCreatedAt(bill.getCreatedAt().toString());

        // 3. Parse items JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ItemResponseDTO> items =
                    mapper.readValue(bill.getItemsJson(),
                            mapper.getTypeFactory().constructCollectionType(List.class, ItemResponseDTO.class));

            dto.setItems(items);
        } catch (Exception ex) {
            ex.printStackTrace();
            dto.setItems(null);
        }

        return dto;
    }


    public ResponseEntity<?> approveBill(@PathVariable String Workid) {
    	
    	EBill e= eBillRepository.findByWork_WorkId(Workid).orElseThrow();
    	e.setStatus(EBillStatus.APPROVED);
        return ResponseEntity.ok(eBillRepository.save(e));
    }
    
    public ResponseEntity<EBill> rejectBill(
            @PathVariable String workId) {

    	EBill e= eBillRepository.findByWork_WorkId(workId).orElseThrow();
    	e.setStatus(EBillStatus.REJECTED);
    	
        return ResponseEntity.ok(eBillRepository.save(e));
    }
    
    

    
}
