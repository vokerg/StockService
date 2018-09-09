package com.stock.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.stock.entity.Category;
import com.stock.entity.CategoryAttribute;
import com.stock.entity.CategoryAttributeProduct;
import com.stock.entity.Product;
import com.stock.repository.CategoryAttributeProductRepository;
import com.stock.repository.CategoryAttributeRepository;
import com.stock.repository.CategoryRepository;
import com.stock.repository.ProductRepository;
import com.stock.service.UserService;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {
	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	CategoryAttributeRepository categoryAttributeRepository;

	@Autowired
	UserService userService;

	@Autowired
	CategoryAttributeProductRepository catAttrProdRepository;
	
	@Autowired
	ProductRepository productRepository;

	@GetMapping("")
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	@PutMapping("")
	public ResponseEntity<?> insertCategory(@RequestBody Category category) {
		categoryRepository.save(category);
		return ResponseEntity.ok(null);
	}

	@GetMapping("/attributes")
	public List<CategoryAttribute> getAllAttributes() {
		return categoryAttributeRepository.findAll();
	}

	@PutMapping("/{id}/attributes")
	public ResponseEntity<?> insertAttribute(@PathVariable String id, @RequestBody CategoryAttribute attribute,
			@RequestHeader(value = "idUser", required = true) String idUser)
			throws JsonParseException, JsonMappingException, IOException {

		if (!userService.isAllowedToChangeProduct(idUser)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}

		Category category = categoryRepository.findById(Long.valueOf(id)).orElse(null);
		if (category == null) {
			return ResponseEntity.badRequest().body(null);
		}
		attribute.setCategory(category);
		categoryAttributeRepository.save(attribute);
		return ResponseEntity.ok(null);
	}

	@GetMapping("/{id}/attributes")
	public List<CategoryAttribute> getAttributesForCategory(@PathVariable String id) {
		return categoryAttributeRepository.findByCategoryId(Long.valueOf(id));
	}

	@GetMapping("/attributes/{id}/products")
	public List<Product> getAllProductsForAttribute(@PathVariable String id) {
		List<CategoryAttributeProduct> list = catAttrProdRepository.findByCategoryAttributeId(Long.valueOf(id));
		return productRepository.findByCategoryAttributeProductsIn(list);
	}
}
