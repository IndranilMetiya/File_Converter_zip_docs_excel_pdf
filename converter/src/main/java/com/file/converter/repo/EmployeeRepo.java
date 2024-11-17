package com.file.converter.repo;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.file.converter.entity.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
	
	@Query("SELECT e FROM Employee e WHERE e.name LIKE %:keyword%")
	List<Employee> findByNameContaining(@Param("keyword") String keyword);
	
	@Modifying
	@Query("UPDATE Employee e SET e.department = :department WHERE e.name = :name")
	int updateDepartmentByName(@Param("name") String name, @Param("department") String department);

	
	@Modifying
	@Query("UPDATE Employee e SET e.salary = :salary WHERE e.id = :id")
	int updateEmployeeSalary(@Param("id") Long id, @Param("salary") BigDecimal salary);



}
