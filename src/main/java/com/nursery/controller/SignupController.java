package com.nursery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nursery.model.ContactForm;
import com.nursery.model.Role;
import com.nursery.model.User;
import com.nursery.repository.ContactFormRepository;
import com.nursery.repository.RoleRepository;
import com.nursery.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.mindrot.jbcrypt.BCrypt;

@Controller
public class SignupController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private ContactFormRepository contactFormRepository;

    @Autowired
    private HttpSession session;
    
    @Autowired
    private JavaMailSender emailSender;

    private Map<String, String> otpMap = new HashMap<>();
    
    @GetMapping("/signup")
    public String signupPage() {
        return "signup.html";
    }
    
    @PostMapping("/register")
    public String signup(@ModelAttribute("user") User user, Model model) {
        // Check if the user already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("errorEmailExists", "User with email already exists!");
            return "signup.html"; // Stay as the signup modal
        }

        // Hash the password
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        
        // Perform user registration logic
        userRepository.save(user);
        
        // Assign CUSTOMER role to the user
        Role customerRole = roleRepository.findByName("CUSTOMER");
        user.getRoles().add(customerRole);
        userRepository.save(user);

        return "index.html";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login.html";
    }
    
    @PostMapping("/postlogin")
    public String postLogin(@ModelAttribute User u, Model model) {
        User user = userRepository.findByEmail(u.getEmail());
        if (user != null && BCrypt.checkpw(u.getPassword(), user.getPassword())) {
            // Extract and store roles as a List or Set
            Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
            session.setMaxInactiveInterval(3600);
            session.setAttribute("userRoles", roles);
            session.setAttribute("activeUser", user.getEmail());
            session.setAttribute("fullname", user.getFullname());
            session.setAttribute("loggedIn", true);
            if (roles.contains("ADMIN")||roles.contains("STAFF")) {
                return "redirect:/admin";
            } else {
                return "index.html";
            }
        }
        session.setAttribute("loggedIn", false);
        model.addAttribute("loginError", true);
        return "login.html";
    }

    
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalidate the session to clear all session attributes
        }
        return "redirect:/"; // Redirect to the home page
    }
   
    @GetMapping("/forgotPasswordPage")
    public String forgotPasswordPage() {
        return "forgotPassword.html";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            model.addAttribute("invalidEmail", true);
            return "forgotPassword.html"; // Stay on the forgot password page
        }

        String otp = generateOTP();
        System.out.println("Generated OTP for email " + email + ": " + otp);
        otpMap.put(email, otp); // Store the OTP in the map with the email as the key
        sendOTPEmail(email, otp);
        
        model.addAttribute("email", email);
        // Redirect to the verifyOTP page with the email parameter
       return "verifyOTP.html";
    }


    private String generateOTP() {
        // Generate a 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendOTPEmail(String email, String otp) {
        // Get current timestamp
        long timestamp = System.currentTimeMillis();
        // Combine OTP value and timestamp into a single string separated by ':'
        String otpWithTimestamp = otp + ":" + timestamp;
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(email);
            helper.setSubject("Password Reset OTP");
            helper.setText("Your OTP for password reset is: " + otp+"\n\nYour OTP will expire in 5 minutes.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        emailSender.send(message);
        // Store OTP with timestamp in the map
        otpMap.put(email, otpWithTimestamp);
    }


    @PostMapping("/verifyOTP")
    public String verifyOTP(@RequestParam("email") String email, @RequestParam("otp") String otp, Model model) {
        String storedOTP = otpMap.get(email); // Retrieve the OTP using the email as the key
        System.out.println("Email received in verifyOTP: " + email);
        System.out.println("OTP received in verifyOTP: " + otp);
        System.out.println("Retrieved OTP for email " + email + ": " + storedOTP);
        if (storedOTP != null) {
            // Split the storedOTP to extract OTP value and timestamp
            String[] storedOTPParts = storedOTP.split(":");
            String otpValue = storedOTPParts[0];
            long otpTimestamp = Long.parseLong(storedOTPParts[1]);
            long currentTimestamp = System.currentTimeMillis();

            // Check if OTP is expired (5 minutes)
            if (currentTimestamp - otpTimestamp > 300000) {
                // OTP is expired
                model.addAttribute("expiredOTP", true);
                return "forgotPassword.html";
            }

            if (otpValue.equals(otp)) {
            	model.addAttribute("email", email);
                // OTP is valid, allow the user to reset password
                return "resetPassword.html";
            } else {
                // OTP is invalid
                model.addAttribute("invalidOTP", true);
                return "forgotPassword.html";
            }
        } else {
            // No OTP found for the email
            model.addAttribute("invalidOTP", true);
            return "forgotPassword.html";
        }
    }

    
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("email") String email, @RequestParam("password") String password, Model model) {
       
        User user = userRepository.findByEmail(email);
        System.out.println(email);
        System.out.println(user);
        // Hash the new password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // Clear OTP from map after password reset
        otpMap.remove(email);

        model.addAttribute("passwordResetSuccess", true);
        return "login.html"; 
    }
    
    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        String email = (String) session.getAttribute("activeUser");
        if (email != null) {
            User user = userRepository.findByEmail(email);
            model.addAttribute("user", user);
            return "profile.html"; 
        }
        return "redirect:/login"; 
    }

    @GetMapping("/editProfile")
    public String editProfileForm(Model model, HttpSession session) {
        String email = (String) session.getAttribute("activeUser");
        if (email != null) {
            User user = userRepository.findByEmail(email);
            model.addAttribute("user", user);
            return "editProfile.html"; 
        }
        return "redirect:/login";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute("user") User user, HttpSession session) {
        String sessionEmail = (String) session.getAttribute("activeUser");
        if (sessionEmail != null) {
            User existingUser = userRepository.findByEmail(sessionEmail);
            existingUser.setFullname(user.getFullname());
            existingUser.setPhonenumber(user.getPhonenumber());
            userRepository.save(existingUser);
            return "redirect:/profile";
        }
        return "redirect:/login";
    }
    
	@GetMapping("/contactus")
	public String ContactUsPage(Model model) {
		model.addAttribute("contactForm", new ContactForm());
		return "contactus.html";
	}

	@PostMapping("/sendContactEmail")
	public String sendContactEmail(@ModelAttribute ContactForm contactForm, Model model) {
	    try {
	        MimeMessage message = emailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);
	
	        helper.setFrom("sudippradhanadgj@gmail.com"); 
	        helper.setTo("sudippradhanadgj@gmail.com");
	        helper.setCc(InternetAddress.parse(contactForm.getEmail()));
	        helper.setSubject("Contact Us Request from: " + contactForm.getName());
	        String content = "Name: " + contactForm.getName() + "\n" +
	                         "Email: " + contactForm.getEmail() + "\n" +
	                         "Subject: " + contactForm.getSubject() + "\n" +
	                         "Message: " + contactForm.getMessage();
	        helper.setText(content);
	
	     // Save contact in the database
	        ContactForm contact = new ContactForm();
	        contact.setName(contactForm.getName());
	        contact.setEmail(contactForm.getEmail());
	        contact.setSubject(contactForm.getSubject());
	        contact.setMessage(contactForm.getMessage());
	        contactFormRepository.save(contact);
	        
	        emailSender.send(message);
	        model.addAttribute("message", "Your message has been sent successfully!");
	    } catch (MessagingException e) {
	        model.addAttribute("error", "Error while sending email: " + e.getMessage());
	        e.printStackTrace();
	        return "contactus";
	    }
	
	    return "redirect:/contact-success";
	}
	
	@GetMapping("/contact-success")
	public String contactSuccess() {
	    return "contactSuccess.html";
	}


}
