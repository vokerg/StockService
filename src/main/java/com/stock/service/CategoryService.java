package com.stock.service;

import org.springframework.stereotype.Service;

@Service
public class CategoryService {

	public void deleteAttribute(String id, String attributeId) throws BadRequestException {
		if (!validRequest(id, attributeId)) {
			throw new BadRequestException("Request is not valid");
		}
		// TODO Auto-generated method stub
		
	}

	private boolean validRequest(String id, String attributeId) {
		// TODO Auto-generated method stub
		return false;
	}

}
