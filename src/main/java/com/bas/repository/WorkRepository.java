package com.bas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bas.model.Work;

public interface WorkRepository extends JpaRepository<Work, String> {
    Work findByWorkIdAndName(String workId, String name);
    Optional<Work> findByWorkId(String workId);
    
    
    
    // Alternative: Just return work IDs where username matches contractor name
    @Query(value = "SELECT w.work_id " +
            "FROM work w " +
            "JOIN contractor c ON w.contractor_id = c.id " +
            "JOIN user u ON u.username = c.contractor_name " +
            "WHERE u.username = c.contractor_name AND u.username = :username", nativeQuery = true)
    List<String> findWorkIdsWhereUsernameMatchesContractorName(@Param("username") String username);
}

