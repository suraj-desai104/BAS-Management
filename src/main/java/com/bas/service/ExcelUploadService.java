package com.bas.service;

import com.bas.ao.repository.EBillRepository;
import com.bas.model.Contractor;
import com.bas.model.EBill;
import com.bas.model.EBillStatus;
import com.bas.model.Work;
import com.bas.model.Item;
import com.bas.repository.ContractorRepository;
import com.bas.repository.WorkRepository;

import tools.jackson.databind.ObjectMapper;

import com.bas.repository.ItemRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelUploadService {

    private final ContractorRepository contractorRepository;
    private final WorkRepository workRepository;
    private final ItemRepository itemRepository;
    private final EBillRepository eBillRepository;
    private final ObjectMapper objectMapper;

    public ExcelUploadService(ContractorRepository contractorRepository,
                              WorkRepository workRepository,
                              ItemRepository itemRepository,
                              EBillRepository eBillRepository,
                              ObjectMapper objectMapper) {
        this.contractorRepository = contractorRepository;
        this.workRepository = workRepository;
        this.itemRepository = itemRepository;
        this.eBillRepository = eBillRepository;
        this.objectMapper = objectMapper;
    }


    @Transactional
    public void uploadExcel(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;
            
            // Track works processed in this upload session
            Set<String> processedWorkIds = new HashSet<>();
            Set<Long> processeditemsIds = new HashSet<>();

            // Step 1: Process Excel rows and save data to database
            for (Row row : sheet) {
                if (firstRow) { 
                    firstRow = false; 
                    continue; // Skip header row
                }

                // Skip empty rows
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
                    continue;
                }

                String contractorName = getCellValueAsString(row.getCell(0));
                String workName = getCellValueAsString(row.getCell(1));
                String workId = getCellValueAsString(row.getCell(2)); 
                int srNo = (int) getCellValueAsNumeric(row.getCell(3));
                String itemName = getCellValueAsString(row.getCell(4));
                double rate = getCellValueAsNumeric(row.getCell(5));
                double length = getCellValueAsNumeric(row.getCell(6));
                double width = getCellValueAsNumeric(row.getCell(7));
                double depth = getCellValueAsNumeric(row.getCell(8));

                // 1️⃣ Get or create Contractor
                Contractor contractor = contractorRepository.findByName(contractorName);
                if (contractor == null) {
                    contractor = new Contractor();
                    contractor.setName(contractorName);
                    contractor = contractorRepository.save(contractor);
                }

                // 2️⃣ Get or create Work by workId + workName
                Work work = workRepository.findByWorkIdAndName(workId, workName);
                if (work == null) {
                    work = new Work();
                    work.setWorkId(workId);
                    work.setName(workName);
                    work.setContractor(contractor);
                    work = workRepository.save(work);
                }
                
                // Track this work for bill generation
                processedWorkIds.add(work.getWorkId());

                // 3️⃣ Create Item for the Work
                Item item = new Item();
                item.setSrNo(srNo);
                item.setName(itemName);
                item.setRate(rate);
                item.setLength(length);
                item.setWidth(width);
                item.setDepth(depth);
                item.setWork(work);
                
                
                	
                	processeditemsIds.add( itemRepository.save(item).getId());
               

            }

            // Step 2: Generate bills only for works processed in this upload session
            for (String workId : processedWorkIds) {
                Work work = workRepository.findById(workId)
                        .orElseThrow(() -> new RuntimeException("Work not found: " + workId));
                
                // Refresh work to load items
                workRepository.flush();
                
                // Check if bill already exists for this work using workId directly
                Optional<EBill> existingBill = eBillRepository.findByWorkId(workId);
                if (existingBill.isPresent()) {
                    // Update existing bill instead of creating duplicate
                    EBill bill = existingBill.get();
                    for(Long id:processeditemsIds) {
                		Item i = itemRepository.findById(id).orElseThrow();
                		if(i.getWork().getWorkId().equals(bill.getWork().getWorkId())) {
                		i.setBill(bill);
                		 updateBill(bill, work);
                         eBillRepository.save(bill);
                		}
                	}
                    
                   
                } else {
                    // Create new bill
                     
                    EBill bill = eBillRepository.save(createBill(work));
                    System.out.println("+++++++++++++++++");
                    if(bill!=null) {
                    	System.out.println("---------------------");
                    	 
                    	
                    	for(Long id:processeditemsIds) {
                    		Item i = itemRepository.findById(id).orElseThrow();
                    		if(i.getWork().getWorkId().equals(bill.getWork().getWorkId())) {
                    		i.setBill(bill);
                    		System.out.println(i.getName());
                    		itemRepository.save(i);
                    		}
                    	}
                    	
                    }
                }
            }
        }
    }
    
    private EBill createBill(Work work) {
        // Fetch items for this work
        List<Item> items = itemRepository.findByWork(work);
        
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("No items found for work: " + work.getWorkId());
        }

        // Calculate totals
        double totalQty = items.stream()
                .mapToDouble(Item::getQuantity)
                .sum();
        double totalAmount = items.stream()
                .mapToDouble(Item::getTotalRate)
                .sum();

        // Convert items to JSON
        String itemsJson = objectMapper.writeValueAsString(
                items.stream().map(item -> {
                    return new Object() {
                        public int srNo = item.getSrNo();
                        public String itemName = item.getName();
                        public double rate = item.getRate();
                        public double length = item.getLength();
                        public double width = item.getWidth();
                        public double depth = item.getDepth();
                        public double quantity = item.getQuantity();
                        public double totalRate = item.getTotalRate();
                    };
                }).collect(Collectors.toList())
        );

        // Create and return E-Bill
        EBill bill = new EBill();
        bill.setWork(work);
        bill.setContractor(work.getContractor());
        bill.setTotalQuantity(totalQty);
        bill.setTotalAmount(totalAmount);
        bill.setItemsJson(itemsJson);
        bill.setStatus(EBillStatus.PENDING);

        return bill;
    }
    
    private void updateBill(EBill bill, Work work) {
        // Fetch items for this work
        List<Item> items = itemRepository.findByWork(work);
        
        if (items == null || items.isEmpty()) {
            return;
        }

        // Recalculate totals
        double totalQty = items.stream()
                .mapToDouble(Item::getQuantity)
                .sum();
        double totalAmount = items.stream()
                .mapToDouble(Item::getTotalRate)
                .sum();

        // Convert items to JSON
        String itemsJson = objectMapper.writeValueAsString(
                items.stream().map(item -> {
                    return new Object() {
                        public int srNo = item.getSrNo();
                        public String itemName = item.getName();
                        public double rate = item.getRate();
                        public double length = item.getLength();
                        public double width = item.getWidth();
                        public double depth = item.getDepth();
                        public double quantity = item.getQuantity();
                        public double totalRate = item.getTotalRate();
                    };
                }).collect(Collectors.toList())
        );

        // Update bill
        bill.setTotalQuantity(totalQty);
        bill.setTotalAmount(totalAmount);
        bill.setItemsJson(itemsJson);
    }
    
    // Helper methods to safely read Excel cells
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    private double getCellValueAsNumeric(Cell cell) {
        if (cell == null) return 0.0;
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            case FORMULA:
                return cell.getNumericCellValue();
            default:
                return 0.0;
        }
    }

}
