package com.buybuddies.shiro.entity;

import com.buybuddies.shiro.enums.MeasurementUnit;
import com.buybuddies.shiro.enums.PurchaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "grocery_list_items")
@Data
public class GroceryListItem extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grocery_list_id", nullable = false)
    private GroceryList groceryList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grocery_item_id", nullable = false)
    private GroceryItem groceryItem;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeasurementUnit unit;

    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseStatus status = PurchaseStatus.PENDING;
}
