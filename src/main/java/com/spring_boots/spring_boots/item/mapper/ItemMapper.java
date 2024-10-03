package com.spring_boots.spring_boots.item.mapper;

import com.spring_boots.spring_boots.item.dto.CreateItemDto;
import com.spring_boots.spring_boots.item.dto.ResponseItemDto;
import com.spring_boots.spring_boots.item.dto.UpdateItemDto;
import com.spring_boots.spring_boots.item.entity.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ResponseItemDto toResponseDto(Item item);

    Item toEntity(CreateItemDto dto);

    Item toEntity(UpdateItemDto dto);
}