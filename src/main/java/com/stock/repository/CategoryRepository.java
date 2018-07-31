package com.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

}
