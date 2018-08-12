package com.stock.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.entity.CategoryAttribute;
import com.stock.entity.CategoryAttributeProduct;
import com.stock.entity.Product;
import com.stock.entity.ProductTree;
import com.stock.repository.CategoryAttributeProductRepository;
import com.stock.repository.CategoryAttributeRepository;
import com.stock.repository.ProductRepository;
import com.stock.repository.ProductTreeRepository;

@Service
public class ProductService {
	@Autowired
	ProductTreeRepository productTreeRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	CategoryAttributeProductRepository categoryAttributeProductRepository;
	
	@Autowired
	CategoryAttributeRepository categoryAttributeRepository;
	
	public List<Product> getProductsByParentId(String parentId) throws BadRequestException {
		ProductTree productTree = productTreeRepository.findById(Long.valueOf(parentId)).orElse(null);
		if (productTree == null) {
			throw new BadRequestException();
		} else {
			return productRepository.findByProductTree(productTree);
		}
	}

	public void updateProductAttributes(String productId, List<String> attributesIds) throws BadRequestException {
		Product product = productRepository.findById(Long.valueOf(productId)).orElse(null);
		if (product == null) {
			throw new BadRequestException();
		}
		List<CategoryAttributeProduct> productAttributes = categoryAttributeProductRepository.findByProduct(product);
		Map<Long, CategoryAttributeProduct> attributesMap = productAttributes.stream().collect(Collectors.toMap(element -> element.getId(), element -> element));
		attributesIds.stream().forEach(attributeId -> {
			Long idLong = Long.valueOf(attributeId);
			if (!attributesMap.containsKey(idLong)) {
				CategoryAttribute attr = categoryAttributeRepository.findById(idLong).orElse(null);
				if (attr != null) {
					CategoryAttributeProduct attrProduct = new CategoryAttributeProduct();
					attrProduct.setCategoryAttribute(attr);
					attrProduct.setProduct(product);
					categoryAttributeProductRepository.save(attrProduct);
				}
			}
		});
	}
}
