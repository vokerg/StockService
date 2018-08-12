package com.stock.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.stock.entity.CategoryAttributeProduct;
import com.stock.entity.Product;
import com.stock.entity.SharedUser;
import com.stock.entity.StockRest;
import com.stock.repository.CategoryAttributeProductRepository;
import com.stock.repository.CategoryAttributeRepository;
import com.stock.repository.ProductRepository;
import com.stock.repository.ProductTreeRepository;
import com.stock.repository.StockRestRepository;
import com.stock.service.BadRequestException;
import com.stock.service.ProductService;
import com.stock.service.UserService;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	StockRestRepository stockRestRepository;

	@Autowired
	UserService userService;

	@Autowired
	CategoryAttributeRepository categoryAttributeRepository;
	
	@Autowired
	CategoryAttributeProductRepository categoryAttributeProductRepository;
	
	@Autowired
	CategoryAttributeProductRepository catAttrProdRepository;
	
	@Autowired
	ProductTreeRepository productTreeRepository;
	
	@Autowired
	ProductService productService;
	
	@GetMapping("")
	public ResponseEntity<List<Product>> getAll(@RequestParam(required=false) String parentId) {
		try {
			return ResponseEntity.ok((parentId == null) ? productRepository.findAll() : productService.getProductsByParentId(parentId));
		} catch (BadRequestException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@GetMapping("/{id}")
	public Product getProduct(@PathVariable String id) {
		return this.productRepository.findById(Long.valueOf(id)).orElse(null);
	}

	@GetMapping("/{id}/productrest")
	public List<StockRest> getProductRest(@RequestHeader(value = "idUser", required = true) String idUser,
			@PathVariable String id) throws JsonParseException, JsonMappingException, IOException {
		if (idUser != null) {
			SharedUser user = userService.getSharedUser(idUser);
			Product product = productRepository.findById(Long.valueOf(id)).orElse(null);
			return user.isAdmin() 
					? stockRestRepository.findByProduct(product) 
					: stockRestRepository.findByProductAndStockIdIn(product, user.getViewstocks().stream().map(stockId -> Long.valueOf(stockId)).collect(Collectors.toList()));
		}
		return stockRestRepository.findByProduct(productRepository.findById(Long.valueOf(id)).orElse(null));
	}

	@PutMapping("")
	public ResponseEntity<Product> createProduct(@RequestHeader(value = "idUser", required = true) String idUser,
			@RequestBody Product product) throws JsonParseException, JsonMappingException, IOException {
		if (!userService.isAllowedToChangeProduct(idUser)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
		productRepository.save(product);
		return ResponseEntity.ok(product);
	}

	@PostMapping("/{id}")
	public ResponseEntity<?> updateProduct(@RequestHeader(value = "idUser", required = true) String idUser,
			@RequestBody Product product, @PathVariable String id)
			throws JsonParseException, JsonMappingException, IOException {
		if (!userService.isAllowedToChangeProduct(idUser)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
		if (product.getId() == Long.valueOf(id)) {
			productRepository.save(product);
			return ResponseEntity.ok(null);
		}
		return ResponseEntity.badRequest().body(null);
	}
	
	@GetMapping("/{id}/attributes")
	public List<CategoryAttributeProduct> getProductAttributes(@PathVariable String id) {
		Product product = productRepository.findById(Long.valueOf(id)).orElse(null);
		return categoryAttributeProductRepository.findByProduct(product);
	}
	
	@Transactional
	@PostMapping("/{id}/attributes")
	public ResponseEntity<?> updateProductAttributes(@PathVariable String id, @RequestBody List<String> attributesIds) {
		try {
			productService.updateProductAttributes(id, attributesIds);
			return ResponseEntity.ok(null);
		} catch (BadRequestException e) {
			return ResponseEntity.badRequest().body(null);
		}
		
	}
	
	@GetMapping("/attributes")
	public List<Product> getAllProductsForAttributes(@RequestParam List<String> ids) {
		List<Long> longIds = ids.stream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
		List<Long>productIds = catAttrProdRepository.findProductIdsByInclAttributeList(longIds, Long.valueOf(longIds.size()));
		return productIds.stream().map(id -> productRepository.findById(id).orElse(null)).collect(Collectors.toList());
	}

	@GetMapping("/{id}/orders")
	public Object getStockOrders(@RequestHeader(value = "idUser", required = true) String idUser, @PathVariable String id) {
		return (idUser != null) 
				? restTemplate.getForObject("http://order-api/orders?productId=" + id + "&paramUserId=" + idUser, Object.class)
				: restTemplate.getForObject("http://order-api/orders?productId=" + id, Object.class);
	}

}
