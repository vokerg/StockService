package com.stock.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.entity.CategoryAttribute;
import com.stock.entity.CategoryAttributeProduct;
import com.stock.entity.Product;
import com.stock.repository.CategoryAttributeProductRepository;
import com.stock.repository.CategoryAttributeRepository;
import com.stock.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	CategoryAttributeProductRepository catAttrProdRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CategoryAttributeRepository catAttrRepository;

	@Transactional
	public void updateProductAttributes(Product product, List<String> attributesIds) {
		List<CategoryAttributeProduct> productAttributes = catAttrProdRepository.findByProduct(product);
		Map<Long, CategoryAttributeProduct> attributesMap = productAttributes.stream()
				.collect(Collectors.toMap(element -> element.getId(), element -> element));
		attributesIds.stream().forEach(attributeId -> {
			Long idLong = Long.valueOf(attributeId);
			if (!attributesMap.containsKey(idLong)) {
				CategoryAttribute attr = catAttrRepository.findById(idLong).orElse(null);
				if (attr != null) {
					CategoryAttributeProduct attrProduct = new CategoryAttributeProduct();
					attrProduct.setCategoryAttribute(attr);
					attrProduct.setProduct(product);
					catAttrProdRepository.save(attrProduct);
				}
			}
		});
	}

	public List<Product> getAllProductsForAttributes(List<String> ids) {
		List<Long> longIds = ids.stream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
		List<Long> productIds = catAttrProdRepository.findProductIdsByInclAttributeList(longIds,
				Long.valueOf(longIds.size()));
		return productIds.stream().map(id -> productRepository.findById(id).orElse(null)).collect(Collectors.toList());
	}

}
