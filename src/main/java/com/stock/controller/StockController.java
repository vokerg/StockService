package com.stock.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.stock.entity.Product;
import com.stock.entity.SharedUser;
import com.stock.entity.Stock;
import com.stock.entity.StockRest;
import com.stock.repository.ProductRepository;
import com.stock.repository.StockRepository;
import com.stock.repository.StockRestRepository;
import com.stock.service.UserService;

@RestController
@RequestMapping(value = "/stocks")
public class StockController {

	@Autowired
	StockRepository stockRepository;

	@Autowired
	StockRestRepository stockRestRepository;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	UserService userService;

	@Autowired
	ProductRepository productRepository;

	@GetMapping("")
	public List<Stock> allStocks(@RequestHeader(value = "idUser", required = false) String idUser) {
		if (idUser != null) {
			SharedUser user = getSharedUser(idUser);
			return user.isAdmin() ? this.stockRepository.findAll()
					: this.stockRepository.findByIdIn(
							user.getViewstocks().stream().map(id -> Long.valueOf(id)).collect(Collectors.toList()));
		}
		return this.stockRepository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Stock> getStock(@RequestHeader(value = "idUser", required = false) String idUser,
			@PathVariable String id) {
		return userService.isAllowedToSeeStock(idUser, id)
				? ResponseEntity.ok(this.stockRepository.findById(Long.valueOf(id)).orElse(null))
				: ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);

	}

	@GetMapping("/{id}/stockrest")
	public ResponseEntity<List<StockRest>> getStockRest(
			@RequestHeader(value = "idUser", required = false) String idUser, @PathVariable String id) {
		return userService.isAllowedToSeeStock(idUser, id)
				? ResponseEntity.ok(this.stockRestRepository
						.findByStockExcludeEmpty(this.stockRepository.findById(Long.valueOf(id)).orElse(null)))
				: ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	}

	@GetMapping("/{id}/stockrest/{productId}")
	public ResponseEntity<Float> getStockRestForProduct(@PathVariable String id, @PathVariable String productId) {
		Stock stock = this.stockRepository.findById(Long.valueOf(id)).orElse(null);
		Product product = this.productRepository.findById(Long.valueOf(productId)).orElse(null);
		if (stock == null || product == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		StockRest rest = this.stockRestRepository.findFirstByProductAndStock(product, stock);
		return ResponseEntity.ok((rest == null) ? 0 : rest.getQty());
	}

	@GetMapping("/{id}/orders")
	public ResponseEntity<?> getStockOrders(@RequestHeader(value = "idUser", required = false) String idUser,
			@PathVariable String id) {
		return userService.isAllowedToSeeStock(idUser, id)
				? ResponseEntity.ok()
						.body(restTemplate.getForObject("http://order-api/orders?stockId=" + id, Object.class))
				: ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	}

	@GetMapping("/{id}/orders/{orderId}")
	public ResponseEntity<?> getStockOrder(@RequestHeader(value = "idUser", required = false) String idUser,
			@PathVariable String id, @PathVariable String orderId) {
		return userService.isAllowedToSeeStock(idUser, id)
				? ResponseEntity.ok()
						.body(restTemplate.getForObject("http://order-api/orders/" + orderId, Object.class))
				: ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	}

	@PutMapping("/")
	public Stock addStock(@RequestBody Stock stock) {
		stockRepository.save(stock);
		return stock;
	}

	@PostMapping("/{id}")
	public ResponseEntity<?> updateStock(@RequestHeader(value = "idUser", required = true) String idUser,
			@RequestBody Stock stock, @PathVariable String id) {
		if (!userService.isAllowedToUpdateStock(idUser, id)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
		if (stock.getId() == Long.valueOf(id)) {
			stockRepository.save(stock);
			return ResponseEntity.ok(null);
		}
		return ResponseEntity.badRequest().body(null);
	}

	private SharedUser getSharedUser(String idUser) {
		return userService.getSharedUser(idUser);
	}
}
