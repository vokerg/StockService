package com.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stock.entity.Product;
import com.stock.entity.Stock;
import com.stock.entity.StockRest;

public interface StockRestRepository extends JpaRepository<StockRest, Long> {
	@Query("select r from StockRest r where r.stock=:stock and r.qty!=0")
	List<StockRest> findByStockExcludeEmpty(@Param("stock") Stock stock);
	List<StockRest> findByProduct(Product product);
	List<StockRest> findByProductAndStockIdIn(Product product, List<Long> ids);
	StockRest findFirstByProductAndStock(Product product, Stock stock);
}
