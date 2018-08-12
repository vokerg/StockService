package com.stock.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.entity.SharedUser;

@Service
public class UserService {
	@Autowired
	RestTemplate restTemplate;

	public SharedUser getSharedUser(String idUser) throws JsonParseException, JsonMappingException, IOException {
		String obj = restTemplate.getForObject("http://STOCK-AUTH/users/" + idUser, String.class);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(obj, SharedUser.class);
	}

	public boolean isAllowedToSeeStock(String idUser, String id)
			throws JsonParseException, JsonMappingException, IOException {
		if (idUser == null) {
			return true;
		}
		SharedUser user = this.getSharedUser(idUser);
		return (user.isAdmin() || user.getViewstocks().contains(id));
	}

	public boolean isAllowedToUpdateStock(String idUser, String stockId)
			throws JsonParseException, JsonMappingException, IOException {
		return this.isAllowedToSeeStock(idUser, stockId);
	}

	public boolean isAllowedToChangeProduct(String idUser)
			throws JsonParseException, JsonMappingException, IOException {
		if (idUser == null) {
			return false;
		}
		SharedUser user = this.getSharedUser(idUser);
		return (user.isAdmin() || user.isProductCreator());
	}
}
