package com.mvpmatch.vending.repository;

import com.mvpmatch.vending.entity.Product;
import com.mvpmatch.vending.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

}