package com.file.converter.service;



import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.file.converter.entity.Employee;
import com.file.converter.repo.EmployeeRepo;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;

@Service
public class ConverterService {
	
	@Autowired
	EmployeeRepo employeeRepo;
	
	public void createZipWithAllFormats(OutputStream outputStream, List<Employee> employeeList) throws Exception{
		
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
			
			zipOutputStream.putNextEntry(new ZipEntry("file.pdf"));
			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
			generatePDF(pdfStream, employeeList);
			zipOutputStream.write(pdfStream.toByteArray());
			
			zipOutputStream.putNextEntry(new ZipEntry("file.xlsx"));	
			ByteArrayOutputStream excelStream = new ByteArrayOutputStream();
			generateExcel(zipOutputStream, employeeList);
			zipOutputStream.write(excelStream.toByteArray());
			
			zipOutputStream.putNextEntry(new ZipEntry("file.csv"));
			ByteArrayOutputStream csvStream = new ByteArrayOutputStream();
			generateCSV(csvStream, employeeList);
			zipOutputStream.write(csvStream.toByteArray());
			
			zipOutputStream.closeEntry();
			
			
			
			
		} catch (Exception e) {
			System.err.println("Caught exception in service");
		}
		
	}
	
	
	public void generatePDF(OutputStream outputStream, List<Employee> employeeList) throws Exception {

		Document document = new Document();
		PdfWriter.getInstance(document, outputStream);
		document.open();
		for (Employee employee : employeeList) {

			document.add(new Paragraph("Employee ID : " + employee.getId()));
			document.add(new Paragraph("Employee name : " + employee.getName()));
			document.add(new Paragraph("Employee age : " + employee.getAge()));
			document.add(new Paragraph("Employee department : " + employee.getDepartment()));
			document.add(new Paragraph("Employee salary : " + employee.getSalary()));
			document.add(new Paragraph("Employee hire_date : " + employee.getHireDate()));

		}
		document.close();

	}
	
	
	public void generateExcel(OutputStream outputStream, List<Employee> employeeList) throws Exception {
		
		XSSFWorkbook workbook=new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Employees Data");
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Employee ID");
		header.createCell(1).setCellValue("Employee name");
		header.createCell(2).setCellValue("Employee age");
		header.createCell(3).setCellValue("Employee department");
		header.createCell(4).setCellValue("Employee salary");
		header.createCell(5).setCellValue("Employee hire_date");
		
		int rowIdx=1;
		for (Employee employee : employeeList) {
			Row dataRow=sheet.createRow(rowIdx++);
			dataRow.createCell(0).setCellValue(employee.getId());
			dataRow.createCell(1).setCellValue(employee.getName());
			dataRow.createCell(2).setCellValue(employee.getAge());
			dataRow.createCell(3).setCellValue(employee.getDepartment());
			dataRow.createCell(4).setCellValue(employee.getSalary().toString());
			dataRow.createCell(5).setCellValue(employee.getHireDate());
			
		}
		workbook.write(outputStream);
		workbook.close();
	}
	
	public void generateCSV(OutputStream outputStream, List<Employee> employeeList) throws Exception {
		
		StringWriter writer= new StringWriter();
		CSVWriter csvWriter=new CSVWriter(writer);
		csvWriter.writeNext(new String[] {"Employee ID", "Employee name", "Employee age", "Employee department", "Employee salary", "Employee hire_date"});
		for (Employee employee : employeeList) {
			csvWriter.writeNext(new String[] {String.valueOf(employee.getId()), 
											  String.valueOf(employee.getName()),
											  String.valueOf(employee.getAge()),
											  String.valueOf(employee.getDepartment()),
											  String.valueOf(employee.getSalary()),
											  String.valueOf(employee.getHireDate())});
			csvWriter.close();
			outputStream.write(writer.toString().getBytes());
		}
		
	}
	
	
	

}
