package com.stock.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Product {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@ManyToOne
    @JoinColumn(name = "product_tree_id")
	private ProductTree productTree;
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="product", orphanRemoval = true)
	protected List<CategoryAttributeProduct> categoryAttributeProducts;
	
	public List<CategoryAttributeProduct> getCategoryAttributeProducts() {
		return categoryAttributeProducts;
	}

	private float price;
	
	public Product() {
		super();
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public ProductTree getProductTree() {
		return productTree;
	}

	public void setProductTree(ProductTree productTree) {
		this.productTree = productTree;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
}
