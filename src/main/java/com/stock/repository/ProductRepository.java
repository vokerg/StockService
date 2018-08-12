package com.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.entity.Product;
import com.stock.entity.ProductTree;

public interface ProductRepository extends JpaRepository<Product, Long> {
	public List<Product> findByProductTree(ProductTree productTree);
}
