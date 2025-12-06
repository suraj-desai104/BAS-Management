package com.bas.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import com.bas.model.Item;
import com.bas.model.Work;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByWork(Work work);
}
