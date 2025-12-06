package com.bas.ao.repository;

import com.bas.model.EBill;
import com.bas.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EBillRepository extends JpaRepository<EBill, Long> {
    Optional<EBill> findByWork(Work work);
    Optional<EBill> findByWork_WorkId(String workId);
    // Find EBill by workId directly
    @Query("SELECT e FROM EBill e WHERE e.work.workId = :workId")
    Optional<EBill> findByWorkId(@Param("workId") String workId);
    
    @Query("SELECT e FROM EBill e WHERE e.work.workId = :workId AND e.status = 'APPROVED'")
    Optional<EBill> findApprovedBillByWorkId(@Param("workId") String workId);

}
