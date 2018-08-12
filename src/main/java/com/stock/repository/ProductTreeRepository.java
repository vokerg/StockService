package com.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.entity.ProductTree;

public interface ProductTreeRepository extends JpaRepository<ProductTree, Long> {

	List<ProductTree> findByParentId(long parentId);

}
