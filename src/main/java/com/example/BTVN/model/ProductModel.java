package com.example.BTVN.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {

    private Long productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private String images;
    private String description;
    private double discount;
    private short status;
    private Long categoryId;
    private MultipartFile imageFile;
    private Boolean isEdit = false;

}
