package com.nursery.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nursery.model.Cart;
import com.nursery.model.Product;
import com.nursery.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private HttpSession session;

    @GetMapping("/addToCart/{productId}")
    public String addToCart(@PathVariable int productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            Cart cartItem = new Cart();
            // Log the product information being added to the cart            
            cartItem.setProduct(product);

            List<Cart> cart = (List<Cart>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
            }
            cart.add(cartItem);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
    	if (session.getAttribute("activeUser") == null) {
			return "redirect:/login?cartnotlogged=true";  // Redirect to login page if not logged in
        }
        List<Cart> cart = (List<Cart>) session.getAttribute("cart");
        model.addAttribute("cart", cart);
        return "cart.html";
    }

    @GetMapping("/removeFromCart/{number}")
    public String removeFromCart(@PathVariable int number) {
        List<Cart> cart = (List<Cart>) session.getAttribute("cart");
        if (cart != null && number >= 0 && number < cart.size()) {
            cart.remove(number);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }
    
//    @GetMapping("/checkout")
//    public String checkoutPage(Model model) {
//    	if (session.getAttribute("activeUser") == null) {
//			return "redirect:/login?cartnotlogged=true";  // Redirect to login page if not logged in
//        }
//    	List<Cart> cart = (List<Cart>) session.getAttribute("cart");
//        model.addAttribute("cart", cart);
//        return "checkout.html";
//    }
}
