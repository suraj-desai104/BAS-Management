package com.bas.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.bas.model.Contractor;


public interface ContractorRepository extends JpaRepository<Contractor, Long> {
    Contractor findByName(String name);
}
