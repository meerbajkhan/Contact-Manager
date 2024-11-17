package com.smartcontact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.entities.User;
import com.smartcontact.repository.UserRepository;
import com.smartcontact.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	
//	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	//email id form open handler
	@GetMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_form";
	}
	
	@PostMapping("/send_otp")
	public String sendOtp(@RequestParam("email") String email, HttpSession session) {
		
		System.out.println("Email : " + email);
		
		//generating OTP of 6 digit
//		int otp = random.nextInt(999999);
		
		int min = 1000;
		int max = 999999;
		int otp = (int) (Math.random() * (max - min + 1) + min);
		
		System.out.println("OTP : " + otp);
		
		// write code for send OTP to email
		String subject = "OTP Form Smart Contcat Manager";
		String toEmail = email;
		String body = "OTP = " + otp;
		
		boolean flag = this.emailService.sendEmail(toEmail, body, subject);
		User user = this.userRepository.getUserByUserEmail(email);
		
		if(user == null) {
			session.setAttribute("message", "User does not exits with this email");
			return "forgot_email_form";
		}
		
		if(flag) {
			session.setAttribute("myOtp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}else {
			session.setAttribute("message", "Check your email id");
			
			return "forgot_email_form";
		}
	}
	
	//verify OTP
	@PostMapping("/verify_otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
		
		int myOtp = (int) session.getAttribute("myOtp");
		String email = (String) session.getAttribute("email");
		
		if(myOtp == otp) {
			//password change form
			User user = this.userRepository.getUserByUserEmail(email);
			
			if(user == null) {
				//send error message
				session.setAttribute("message", "User does not exits with this email");
				return "forgot_email_form";
			}
			return "password_change_form";
		}else {
			
			session.setAttribute("message", "You have entered wrong otp");
			
			return "verify_otp";
		}
	}
	
	//change password
	@PostMapping("/change_password")
	public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
		
		String email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserEmail(email);
		
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		
		return "redirect:/singin?change=password Changed Successfully";
		
	}
}
