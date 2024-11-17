package com.smartcontact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.smartcontact.entities.User;
import com.smartcontact.helper.Message;
import com.smartcontact.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/home")
	public String home(Model model) {

		model.addAttribute("title", "Home-Smart Contact Manager");

		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {

		model.addAttribute("title", "About-Smart Contact Manager");

		return "about";
	}

	@RequestMapping("/singup")
	public String singup(Model model) {

		model.addAttribute("title", "singup-Smart Contact Manager");
		model.addAttribute("user", new User());

		return "singup";
	}

	// handler for register user

//	@PostMapping("/registerForm")
//	public String registerUSer(@Valid @ModelAttribute("user") User user ,BindingResult bindingResult, Model model,HttpSession session) {
//		
//		try {
//			
//			if(bindingResult.hasErrors()) {
//				
//				System.out.println("");
//				model.addAttribute("user", user);
//				return "singup";
//			}
//			
//			user.setRole("ROLE_USER");
//			user.setEnabled(true);
//			
//			System.out.println("User : " + user);
//			
//			User result = this.userRepository.save(user);
//			
//			model.addAttribute("user", new User());
//			
//			session.setAttribute("message", new Message("Successfully Registered", "alert-success"));
//			
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//			model.addAttribute("user", user);
//			session.setAttribute("message", new Message("Something Went Wrong!"+e.getMessage(), "alert-danger"));
//		}
//		
//		return "singup";
//	}

	@PostMapping("/registerForm")
	public String registerUSer(@ModelAttribute("user") User user,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {

			if (!agreement) {

				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("User : " + user);

			this.userRepository.save(user);

			model.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Registered", "alert-success"));

		} catch (Exception e) {

			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Wrong!" + e.getMessage(), "alert-danger"));
		}

		return "singup";
	}

	// handler for custom login

	@GetMapping("/singin")
	public String customLogin(Model model) {

		model.addAttribute("title", "Login Page");

		return "login";
	}

	// open update profile handler
	@PostMapping("/user/profile_update/{userId}")
	public String updateProfile(@PathVariable("userId") Integer userId, Model model) {

		model.addAttribute("title", "Profile Update");

		User user = this.userRepository.findById(userId).get();

		model.addAttribute("user", user);

		return "normal/update_profile_form";
	}

	// update profile handler
	@PostMapping("/user/process_updates_profile")
	public String updateProfileHandler(@ModelAttribute User user,Model model) {

		System.out.println("Enter....");
		User userFindById = this.userRepository.findById(user.getUserId()).get();
		
		userFindById.setRole("ROLE_USER");
		userFindById.setEnabled(true);
		userFindById.setImageUrl("default.png");
		userFindById.setName(user.getName());
		userFindById.setEmail(user.getEmail());
		userFindById.setAbout(user.getAbout());
		
		 this.userRepository.save(userFindById);

		 return "redirect:/user/profile";
	}
}
