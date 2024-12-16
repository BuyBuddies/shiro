package com.buybuddies.shiro.repository;

import com.buybuddies.shiro.entity.GroceryList;
import com.buybuddies.shiro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroceryListRepository extends JpaRepository<GroceryList, Long> {
    List<GroceryList> findByOwnerId(Long ownerId);
    List<GroceryList> findByMembersId(Long userId);
    List<GroceryList> findByOwnerIdOrMembersId(Long ownerId, Long userId);
    Optional<GroceryList> findByNameAndOwnerId(String name, Long ownerId);
    void deleteByNameAndOwnerId(String name, Long ownerId);
    boolean existsByNameAndOwnerId(String name, Long ownerId);
    Optional<GroceryList> findByNameAndOwner(String name, User owner);
    void deleteByNameAndOwner(String name, User owner);
}