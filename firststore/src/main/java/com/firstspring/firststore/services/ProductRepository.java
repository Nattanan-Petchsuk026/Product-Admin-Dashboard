package com.firstspring.firststore.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firstspring.firststore.models.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{

    
}