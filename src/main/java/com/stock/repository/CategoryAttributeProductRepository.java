package com.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stock.entity.CategoryAttribute;
import com.stock.entity.CategoryAttributeProduct;
import com.stock.entity.Product;

public interface CategoryAttributeProductRepository extends JpaRepository<CategoryAttributeProduct, Long>{
	List<CategoryAttributeProduct> findByProduct(Product findById);
	List<CategoryAttributeProduct> findByCategoryAttribute(CategoryAttribute categoryAttribute);
	List<CategoryAttributeProduct> findByCategoryAttributeId(Long categoryAttributeId);
	List<CategoryAttributeProduct> findByCategoryAttributeIdIn(List<Long> categoryAttributeIds);
	@Query("select product.id from CategoryAttributeProduct cap where cap.categoryAttribute.id in (:ids) group by product.id having count(*) >= :qty")
	List<Long> findProductIdsByInclAttributeList(@Param("ids") List<Long> ids, @Param("qty") Long qty);
}
