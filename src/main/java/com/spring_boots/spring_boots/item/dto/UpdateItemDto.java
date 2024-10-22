package com.spring_boots.spring_boots.item.dto;

import com.spring_boots.spring_boots.category.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class UpdateItemDto {
    @NotBlank(message = "상품명은 필수입니다.")
    @Length(max = 200)
    private String itemName;

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    @NotBlank(message = "가격은 필수입니다.")
    @Positive(message = "가격은 0보다 커야 합니다.")
    private Integer itemPrice;

    @Length(max = 10000, message = "설명란의 최대 글자수는 10000입니다.")
    private String itemDescription;

    private String itemMaker;

    @NotBlank(message = "상품 색상은 필수입니다.")
    private List<String> itemColor;

    private Integer itemSize;

    private String imageUrl;

    private List<String> keywords;

    private MultipartFile file;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
