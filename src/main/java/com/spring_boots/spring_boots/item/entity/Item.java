package com.spring_boots.spring_boots.item.entity;

import com.spring_boots.spring_boots.category.entity.Category;
import com.spring_boots.spring_boots.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_price")
    private Integer itemPrice;

    @Column(name = "item_description")
    private String itemDescription;

    @Column(name = "item_maker")
    private String itemMaker;

    @Column(name = "item_color")
    private String itemColor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "item_size")
    private Integer itemSize;
}