package com.buybuddies.shiro.repository;

import com.buybuddies.shiro.entity.Depot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepotRepository extends JpaRepository<Depot, Long> {
    List<Depot> findByHomeId(Long homeId);
    List<Depot> findByHomeIdAndNameContainingIgnoreCase(Long homeId, String name);
}
