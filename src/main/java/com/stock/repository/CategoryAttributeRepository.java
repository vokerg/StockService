package com.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.entity.Category;
import com.stock.entity.CategoryAttribute;

public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long>{
	List<CategoryAttribute> findByCategory(Category category);
	List<CategoryAttribute> findByCategoryId(Long categoryId);
}
