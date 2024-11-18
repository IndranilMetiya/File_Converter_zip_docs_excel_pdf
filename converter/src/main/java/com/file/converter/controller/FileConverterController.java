package com.file.converter.controller;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.file.converter.entity.Employee;
import com.file.converter.repo.EmployeeRepo;
import com.file.converter.service.ConverterService;

import jakarta.transaction.Transactional;

@RestController
public class FileConverterController {
	
	
	@RestController
	@RequestMapping("/employees")
	public class EmployeeController {

	    @Autowired
	    private EmployeeRepo employeeRepo;

	    @Autowired
	    private ConverterService converterService;

	    

	    @GetMapping("/search")
	    public ResponseEntity<List<Employee>> searchEmployeesByName(@RequestParam String keyword) {
	        List<Employee> employees = employeeRepo.findByNameContaining(keyword);
	        if (employees.isEmpty()) {
	            return ResponseEntity.noContent().build();
	        }
	        return ResponseEntity.ok(employees);
	    }

	    @Transactional
	    @PutMapping("/update-department")
	    public ResponseEntity<String> updateDepartmentByName(@RequestParam String name, @RequestParam String department) {
	        int updatedCount = employeeRepo.updateDepartmentByName(name, department);
	        if (updatedCount > 0) {
	            return ResponseEntity.ok("Updated " + updatedCount + " employees' department successfully.");
	        }
	        return ResponseEntity.badRequest().body("No employees found with the given name.");
	    }

	    @Transactional
	    @PutMapping("/update-salary")
	    public ResponseEntity<String> updateEmployeeSalary(@RequestParam Long id, @RequestParam BigDecimal salary) {
	        int updatedCount = employeeRepo.updateEmployeeSalary(id, salary);
	        if (updatedCount > 0) {
	            return ResponseEntity.ok("Updated salary for employee with ID: " + id);
	        }
	        return ResponseEntity.badRequest().body("Employee not found with ID: " + id);
	    }
	    
	    @GetMapping("/download")
	    public ResponseEntity<?> downloadFile(@RequestParam String format,
				@RequestParam(required = false) String nameContains) throws Exception {
			List<Employee> employeeList;
			if (!nameContains.isBlank()) {

				employeeList = employeeRepo.findByNameContaining(nameContains);

			} else {

				employeeList = employeeRepo.findAll();
			}

	        if (employeeList.isEmpty()) {
	            return ResponseEntity.noContent().build(); // No content to download
	        }

	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	        switch (format.toLowerCase()) {
	            case "pdf":
	                converterService.generatePDF(outputStream, employeeList);
	                return createDownloadResponse(outputStream, "application/pdf", "employees.pdf");

	            case "excel":
	                converterService.generateExcel(outputStream, employeeList);
	                return createDownloadResponse(outputStream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "employees.xlsx");

	            case "csv":
	                converterService.generateCSV(outputStream, employeeList);
	                return createDownloadResponse(outputStream, "text/csv", "employees.csv");

	            case "all":
	                ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
	                converterService.createZipWithAllFormats(zipStream, employeeList);
	                return createDownloadResponse(zipStream, "application/zip", "employees.zip");

	            default:
	                return ResponseEntity.badRequest().body("Invalid format specified. Use pdf, excel, csv, or all.");
	        }
	    }

	    private ResponseEntity<ByteArrayResource> createDownloadResponse(ByteArrayOutputStream outputStream, String contentType, String fileName) {
	        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
	        return ResponseEntity.ok()
	                .header("Content-Disposition", "attachment; filename=" + fileName)
	                .contentType(MediaType.parseMediaType(contentType))
	                .contentLength(outputStream.size())
	                .body(resource);
	    }
	    
	}


}
