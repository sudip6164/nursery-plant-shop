package com.nursery.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.nursery.model.User;
import com.nursery.repository.ProductRepository;
import com.nursery.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	private boolean hasAdminRole() {
	    @SuppressWarnings("unchecked")
	    Set<String> roles = (Set<String>) session.getAttribute("userRoles");
	    return roles != null && roles.contains("ADMIN");
	}
	
	private boolean hasStaffRole() {
	    @SuppressWarnings("unchecked")
	    Set<String> roles = (Set<String>) session.getAttribute("userRoles");
	    return roles != null && roles.contains("STAFF");
	}

	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private ProductRepository productRepository;
	
    @Autowired
    private HttpSession session; 
    
	@GetMapping("/admin")
	public String firstPage(@ModelAttribute User user,Model model) {
		if (session.getAttribute("activeUser") == null) {
			return "redirect:/login?sessionExpired=true";  // Redirect to login page if not logged in
        }
		if (!hasAdminRole()&& !hasStaffRole()) {
            return "redirect:/noPermission";
        }
		 
		List<User> allUsers = userRepository.findAll();
        
        // Filter users to include only those with role "CUSTOMER" (assuming role ID is 3)
        List<User> customers = allUsers.stream()
                                        .filter(u -> u.getRoles().stream().anyMatch(role -> role.getId() == 3))
                                        .collect(Collectors.toList());

        long totalCustomers = allUsers.stream()
                .filter(u -> u.getRoles().stream().anyMatch(role -> role.getId() == 3))
                .count();
        
        // Count total products
	    long totalProducts = productRepository.count();
	    
        model.addAttribute("userList", customers);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalProducts", totalProducts);
		return "dashboardTemplates/dashboardIndex.html";
	}
	
	@GetMapping("/userTable")
	public String UserTable(@ModelAttribute User u, Model model) {
		if (session.getAttribute("activeUser") == null) {
            return "redirect:/login?sessionExpired=true"; // Redirect to login page if not logged in
        }
		if (!hasAdminRole()) {
            return "redirect:/noPermission";
        }
		model.addAttribute("userList", userRepository.findAll());
		return "dashboardTemplates/userTable.html";
	}
	
	@GetMapping("/editUser/{id}")
	public String editUserData(@PathVariable int id, Model model) {
		if (session.getAttribute("activeUser") == null) {
            return "redirect:/login?sessionExpired=true"; // Redirect to login page if not logged in
        }
		if (!hasAdminRole()) {
            return "redirect:/noPermission";
        }
	    User user = userRepository.getById(id);
	    model.addAttribute("userObject", user);
	    return "dashboardTemplates/editUser.html";
	}
	
	@PostMapping("/updateUser")
	public String updateUserData(@ModelAttribute User user,Model model) {
		if (session.getAttribute("activeUser") == null) {
            return "redirect:/login?sessionExpired=true"; // Redirect to login page if not logged in
        }
		if (!hasAdminRole()) {
            return "redirect:/noPermission";
        }
	    userRepository.save(user);
	    return "redirect:/userTable";
	}

	
	@GetMapping("/deleteUser/{id}")
	public String deleteAData(@PathVariable int id,	Model model)
	{
		if (session.getAttribute("activeUser") == null) {
            return "redirect:/login?sessionExpired=true"; // Redirect to login page if not logged in
        }
		if (!hasAdminRole()) {
            return "redirect:/noPermission";
        }
		userRepository.deleteById(id);
		model.addAttribute("userList",userRepository.findAll());
		return "redirect:/userTable";
	}
	
	@GetMapping("/dashboardlogout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalidate the session to clear all session attributes
        }
        return "redirect:/"; // Redirect to the home page or any other appropriate page after logout
    }
	
	@GetMapping("/noPermission")
	public String noPermission() {
	    return "noPermission.html"; // Make sure this template is informative and user-friendly
	}

}
