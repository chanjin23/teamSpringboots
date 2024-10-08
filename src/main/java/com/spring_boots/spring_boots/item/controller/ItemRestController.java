package com.spring_boots.spring_boots.item.controller;

import com.spring_boots.spring_boots.item.dto.CreateItemDto;
import com.spring_boots.spring_boots.item.dto.ResponseItemDto;
import com.spring_boots.spring_boots.item.dto.UpdateItemDto;
import com.spring_boots.spring_boots.item.mapper.ItemMapper;
import com.spring_boots.spring_boots.item.service.ItemRestService;
import com.spring_boots.spring_boots.s3Bucket.service.S3BucketService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@NoArgsConstructor
@RequestMapping("/api")
public class ItemRestController {
    private  ItemRestService itemRestService;
    private ItemMapper mapper;
    private S3BucketService s3BucketService;
    

    // Item 만들기
    @PostMapping("/admin/items")
    public ResponseEntity<ResponseItemDto> createItem(@Valid @RequestBody CreateItemDto requestItemDto,
                                                      @RequestParam("file") MultipartFile file) {
        ResponseItemDto responseDto = itemRestService.createItem(requestItemDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Items 전체 보기
    @GetMapping("/items")
    public ResponseEntity<List<ResponseItemDto>> getItems() {
        List<ResponseItemDto> result = itemRestService.getAllItems();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    // Item 상세 보기
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ResponseItemDto> getItem(@PathVariable("itemId") Long id) {
        try {
            ResponseItemDto responseDto = itemRestService.getItem(id);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Item 삭제하기
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ResponseItemDto> deleteItem(@PathVariable("itemId") Long id) {
        try {
            itemRestService.deleteItem(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Item 수정하기
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ResponseItemDto> updateItem (@PathVariable("itemId") Long id, @RequestBody UpdateItemDto updateItemDto) {
        try {
            ResponseItemDto responseDto = itemRestService.updateItem(id, updateItemDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
