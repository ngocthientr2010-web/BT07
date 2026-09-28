package com.example.BTVN.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.BTVN.entity.Product;

public interface IProductService {
    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(Long id);

    Optional<Product> findByProductName(String name);

    Optional<Product> findByCreateDate(Date createDate);

    List<Product> findByProductNameContaining(String name);

    Page<Product> findByProductNameContaining(String name, Pageable pageable);

    Product save(Product entity);

    void deleteById(Long id);

    void delete(Product entity);
}
