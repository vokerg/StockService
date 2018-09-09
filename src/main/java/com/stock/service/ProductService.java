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

import com.stock.entity.ProductTree;
import com.stock.repository.CategoryAttributeProductRepository;
import com.stock.repository.CategoryAttributeRepository;
import com.stock.repository.ProductRepository;
import com.stock.repository.ProductTreeRepository;

@Service
public class ProductService {

	@Autowired
	CategoryAttributeProductRepository catAttrProdRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CategoryAttributeRepository catAttrRepository;

	@Autowired
	ProductTreeRepository productTreeRepository;

	@Autowired
	CategoryAttributeProductRepository categoryAttributeProductRepository;

	@Autowired
	CategoryAttributeRepository categoryAttributeRepository;

	public List<Product> getProductsByParentId(String parentId) throws BadRequestException {
		ProductTree productTree = productTreeRepository.findById(Long.valueOf(parentId)).orElse(null);
		if (productTree == null) {
			throw new BadRequestException("product tree does not exist");
		} else {
			return productRepository.findByProductTree(productTree);
		}
	}

	@Transactional
	//TODO review
	public void updateProductAttributes(String productId, List<String> attributesIds) throws BadRequestException {
		Product product = productRepository.findById(Long.valueOf(productId)).orElse(null);
		if (product == null) {
			throw new BadRequestException("product does not exist");
		}
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

	public void addProductTree(String parentIdStr, ProductTree productTree) throws BadRequestException {
		Long parentId = Long.valueOf(parentIdStr);
		if (parentId != 0) {
			productTreeRepository.findById(Long.valueOf(parentId))
					.orElseThrow(() -> new BadRequestException("product tree does not exist"));
		}
		productTree.setParentId(parentId);
		productTreeRepository.save(productTree);
	}

	public void addProduct(Product product) throws BadRequestException {
		if (product.getProductTree() == null) {
			throw new BadRequestException("product tree does not exist");
		}
		productRepository.save(product);
	}

	@Transactional
	public void deepSave(Product product) {
		if (product.getCategoryAttributeProducts() != null) {
			List<CategoryAttribute> catAttrs = product.getCategoryAttributeProducts().stream()
					.map(catAttrProd -> catAttrProd.getCategoryAttribute()).collect(Collectors.toList());
			categoryAttributeProductRepository.deleteByProductAndCategoryAttributeNotIn(product, catAttrs);
			List<Long> remainingCatAttrIds = categoryAttributeProductRepository.findByProduct(product).stream()
					.map(catAttr -> catAttr.getCategoryAttribute().getId()).collect(Collectors.toList());
			product.getCategoryAttributeProducts().forEach(catAttrProd -> {
				if (!remainingCatAttrIds.contains(catAttrProd.getCategoryAttribute().getId())) {
					catAttrProd.setProduct(product);
					categoryAttributeProductRepository.save(catAttrProd);
				}
			});
			product.setCategoryAttributeProducts(null);
		}
		productRepository.save(product);

	}
}
