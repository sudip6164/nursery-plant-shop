package com.nursery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.nursery.model.Product;
import com.nursery.repository.ProductRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired
    private ProductRepository productRepository;
	
	@GetMapping("/")
	public String firstPage(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		
		List<Product> latestProducts = productRepository.findLatestProducts();
	    model.addAttribute("latestProducts", latestProducts);
	    
		return "index.html";
	}
	@GetMapping("/404")
	public String ErrorPage() {
		return "404.html";
	}
	@GetMapping("/aboutus")
	public String AboutUsPage() {
		return "aboutus.html";
	}
}
