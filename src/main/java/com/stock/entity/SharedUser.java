package com.stock.entity;

import java.util.List;

public class SharedUser {
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public List<String> getViewstocks() {
		return viewstocks;
	}
	public void setViewstocks(List<String> viewstocks) {
		this.viewstocks = viewstocks;
	}
	public boolean isProductCreator() {
		return productCreator;
	}
	public void setProductCreator(boolean productCreator) {
		this.productCreator = productCreator;
	}

	private String id;
	private String username;
	private boolean admin;
	private List<String> viewstocks;
	private boolean productCreator;
}
