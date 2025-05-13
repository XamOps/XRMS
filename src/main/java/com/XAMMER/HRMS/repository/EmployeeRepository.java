package com.XAMMER.HRMS.repository;
//package com.xammer.hrms.repository;

import com.XAMMER.HRMS.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
