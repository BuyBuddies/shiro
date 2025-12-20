package com.buybuddies.shiro.data.grocery_item;


import com.buybuddies.shiro.data.BaseEntity;
import com.buybuddies.shiro.data.enums.ItemCategory;
import com.buybuddies.shiro.data.enums.MeasurementUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grocery_items", indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_barcode", columnList = "barcode")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class GroceryItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(unique = true, length = 50)
    private String barcode;

    @Column()
    @Enumerated(EnumType.STRING)
    private ItemCategory category;

    @Column(nullable = false, name = "unit")
    @Enumerated(EnumType.STRING)
    private MeasurementUnit unit;

    @Column(name = "image_url")
    private String imageUrl;
}
