package com.stock.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.entity.Category;
import com.stock.entity.CategoryAttribute;
import com.stock.repository.CategoryAttributeProductRepository;
import com.stock.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	CategoryRepository catRepository;

	@Autowired
	CategoryAttributeProductRepository catAttrProdRepo;

	@Transactional
	public void deleteAttribute(String categoryId, String attributeId) throws BadRequestException {
		Category category = getCategoryWithException(categoryId);
		deleteAttribute(attributeId, category);
	}

	private void deleteAttribute(String attributeId, Category category) throws BadRequestException {
		CategoryAttribute catAttr = getCategoryAttributeWithException(attributeId, category);
		deleteAttribute(category, catAttr);
	}

	private void deleteAttribute(Category category, CategoryAttribute catAttr) {
		category.getCategoryAttributes().remove(catAttr);
		catRepository.saveAndFlush(category);
	}

	private Category getCategoryWithException(String categoryId) throws BadRequestException {
		Category category = catRepository.findById(Long.valueOf(categoryId)).orElse(null);
		if (category == null) {
			throw new BadRequestException("CategoryId or AttributeId are incorrect");
		}
		return category;
	}

	private CategoryAttribute getCategoryAttributeWithException(String attributeId, Category category)
			throws BadRequestException {
		CategoryAttribute catAttr = category.getCategoryAttributes().stream()
				.filter(ca -> ca.getId() == Long.valueOf(attributeId)).findFirst().orElse(null);
		if (catAttr == null) {
			throw new BadRequestException("CategoryId or AttributeId are incorrect");
		}
		return catAttr;
	}

	@Transactional
	public void deleteCategory(String id) throws BadRequestException {
		Category category = getCategoryWithException(id);
		category.getCategoryAttributes().forEach(catAttr -> deleteAttribute(category, catAttr));
		catRepository.delete(category);
	}
}
