package com.nursery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nursery.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

	List<Product> findByCategory(String category);
	@Query("SELECT p FROM Product p ORDER BY p.productId DESC LIMIT 6")
	List<Product> findLatestProducts();
}
