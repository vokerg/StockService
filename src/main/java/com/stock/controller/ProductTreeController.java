package com.stock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.entity.Product;
import com.stock.entity.ProductTree;
import com.stock.repository.ProductTreeRepository;
import com.stock.service.BadRequestException;
import com.stock.service.ProductService;

@RestController
@RequestMapping(value = "/productTree")
public class ProductTreeController {
	@Autowired
	ProductTreeRepository productTreeRepository;
	
	@Autowired
	ProductService productService;
	
	@GetMapping("/{parentId}")
	List<ProductTree> getProductTrees(@PathVariable String parentId) {
		return productTreeRepository.findByParentId(Long.valueOf(parentId));
	}
	
	@GetMapping("/{parentId}/products")
	ResponseEntity<List<Product>> getProducts(@PathVariable String parentId) {
		try {
			return ResponseEntity.ok(productService.getProductsByParentId(parentId));
		} catch (BadRequestException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
	
	@PutMapping("/{parentId}")
	ResponseEntity<?> addProductTree(@PathVariable String pathVariable, @RequestBody ProductTree productTree) {
		productTreeRepository.save(productTree);
		return ResponseEntity.ok(null);
	}
}
