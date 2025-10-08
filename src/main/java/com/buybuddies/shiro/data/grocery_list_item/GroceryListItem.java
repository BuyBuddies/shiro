package com.buybuddies.shiro.data.grocery_list_item;

import com.buybuddies.shiro.data.grocery_list.GroceryList;
import com.buybuddies.shiro.data.enums.MeasurementUnit;
import com.buybuddies.shiro.data.enums.PurchaseStatus;
import com.buybuddies.shiro.data.grocery_item.GroceryItem;
import com.buybuddies.shiro.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grocery_list_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class GroceryListItem extends BaseEntity {
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseStatus status = PurchaseStatus.PENDING;
}
