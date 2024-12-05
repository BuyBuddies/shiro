package com.buybuddies.shiro.repository;

import com.buybuddies.shiro.entity.GroceryList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroceryListRepository extends JpaRepository<GroceryList, Long> {
    List<GroceryList> findByOwnerId(Long ownerId);
    List<GroceryList> findByMembersId(Long userId);
    List<GroceryList> findByOwnerIdOrMembersId(Long ownerId, Long userId);
}