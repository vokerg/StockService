package com.stock.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.entity.Category;
import com.stock.entity.CategoryAttribute;
import com.stock.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	CategoryRepository catRepository;

	@Transactional
	public void deleteAttribute(String categoryId, String attributeId) throws BadRequestException {
		Category category = getCategoryWithException(categoryId);
		deleteAttribute(category, attributeId);
	}

	private void deleteAttribute(Category category, String attributeId) throws BadRequestException {
		CategoryAttribute catAttr = getCategoryAttributeWithException(category, attributeId);
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

	private CategoryAttribute getCategoryAttributeWithException(Category category, String attributeId)
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
		catRepository.delete(category);
	}

	public Category updateCategory(String id, Category category) throws BadRequestException {
		if (!category.getId().toString().equals(id)) {
			throw new BadRequestException("ID from request is not matching provided category");
		}
		Category dbCat = catRepository.getOne(category.getId());
		if (dbCat == null) {
			throw new BadRequestException("Category does not exist");
		}
		dbCat.setName(category.getName());
		dbCat.setMultipleChoice(category.isMultipleChoice());
		return catRepository.save(dbCat);
	}
}
