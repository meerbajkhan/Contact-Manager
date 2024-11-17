package com.smartcontact.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontact.entities.ContactDetails;
import com.smartcontact.entities.User;
import com.smartcontact.helper.Message;
import com.smartcontact.repository.ContactRepository;
import com.smartcontact.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();
//		System.out.println("userName : " + userName);

		User user = this.userRepository.getUserByUserEmail(userName);
//		System.out.println("USER : " + user);

		// get the user using userName(email)
		model.addAttribute("user", user);

	}

	// dashboard home
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/addcontact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new ContactDetails());

		return "normal/add_contact_form";
	}

	// Processing add contact form

	@PostMapping("/process_contact")
	public String processContact(@ModelAttribute ContactDetails contactDetails,
			@RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session) {

		try {

			String name = principal.getName();
			User user = this.userRepository.getUserByUserEmail(name);

			// processing and uploading file
			if (file.isEmpty()) {
				// file is empty then database save default image

				System.out.println("File is Empty");
				contactDetails.setImage("contact.png");
			} else {
				// upload the file to folder and update the name to contact

				contactDetails.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded");
			}

			contactDetails.setUser(user);
			user.getContactDetails().add(contactDetails);
			this.userRepository.save(user);

			System.out.println("Contact Data : " + contactDetails);
			System.out.println("Added to data base");

			// message success
			session.setAttribute("message", new Message("Your Contact added successfully", "success"));

		} catch (Exception e) {

			System.out.println("Error : " + e.getMessage());
			e.printStackTrace();
			// error massage
			session.setAttribute("message", new Message("Something went worng Try again", "danger"));
		}

		return "normal/add_contact_form";
	}

	// show contacts handler

	@GetMapping("/showcontacts/{page}")
	public String showContacts(@PathVariable("page") int page, Model model, Principal principal) {

		model.addAttribute("title", "Show User Contacts");

		// contact List get
		// fetch user email
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserEmail(userName);
		Pageable pageable = PageRequest.of(page, 5);

		Page<ContactDetails> contacts = this.contactRepository.findContactByUser(user.getUserId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalpage", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	// showing particular contact details
	@GetMapping("/contactDetalis/{contactId}")
	public String showContactDetail(@PathVariable("contactId") Integer contactId, Model model, Principal principal) {

		System.out.println("Contact Id : " + contactId);

		Optional<ContactDetails> contactFindById = this.contactRepository.findById(contactId);
		ContactDetails contact = contactFindById.get();

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserEmail(userName);

		// check given contact is user not find other user contact
		if (user.getUserId() == contact.getUser().getUserId()) {

			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getContactName());
		}

		return "normal/contact_detail";
	}

	// delete contact handler
	@Transactional
	@GetMapping("/deletcontact/{contactId}")
	public String deleteContact(@PathVariable("contactId") int contactId, Model model, HttpSession session) {

		System.out.println("ContactId : " + contactId);

		ContactDetails contactDetails = this.contactRepository.findById(contactId).get();

		if (contactDetails != null) {

			String imageFile = contactDetails.getImage();
			System.out.println("DATA BASE STOR PATH : " + imageFile);

			if (imageFile != null && !imageFile.isEmpty()) {

				// Delete the image file if it exists
				if (!imageFile.equals("contact.png")) {
					try {

						File saveFile = new ClassPathResource("static/image").getFile();
						Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + imageFile);

						if (Files.exists(path)) {
							Files.delete(path);
							System.out.println("Image Successfully Deleted");
						} else {
							System.out.println("Image does not exist");
						}
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
		}

//		System.out.println(contactRepository.findById(contactId).toString());
		this.contactRepository.contactDeleteById(contactId);

		System.out.println("Delete Successfully");

		session.setAttribute("message", new Message("Contact Deleted Successfully", "success"));

		return "redirect:/user/showcontacts/0";
	}
	
	//open update form handler
	@PostMapping("/updatecontact/{contactId}")
	public String updateForm(@PathVariable("contactId") Integer contactId,Model model) {
		
		model.addAttribute("title", "Update Contact");
		
		ContactDetails contactDetails = this.contactRepository.findById(contactId).get();
		
		model.addAttribute("contactDetails", contactDetails);
		
		return "normal/update_contact_form";
	}
	
	//update contact handler
	@PostMapping("/process_updates")
	public String updatehandler(@ModelAttribute ContactDetails contactDetails,
			@RequestParam("profileImage") MultipartFile file,
			Principal principal,
			Model model, HttpSession session) {
		
		ContactDetails oldContactDetails = this.contactRepository.findById(contactDetails.getContactId()).get();
		
		try {
			
			if(!file.isEmpty()) {
				
				//delete old photo
				
				File deleteFile = new ClassPathResource("static/image").getFile();
				File fileDelete = new File(deleteFile, oldContactDetails.getImage());
				fileDelete.delete();
				
				//update new image
				
				File saveFile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contactDetails.setImage(file.getOriginalFilename());
				
			}else {
				
				contactDetails.setImage(oldContactDetails.getImage());
			}
			
			User user = this.userRepository.getUserByUserEmail(principal.getName());
			contactDetails.setUser(user);
			
			this.contactRepository.save(contactDetails);
			
			session.setAttribute("message", new Message("Your contact is update", "success"));
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		System.out.println("Contact Name ");
		
		return "redirect:/user/contactDetalis/"+contactDetails.getContactId();
	}
	
	//your profile handler
	@GetMapping("/profile")
	public String userProfile(Model model) {
		
		model.addAttribute("title", "Profile Page");
		
		return "normal/profile";
	}
	
	
	//open settings handler
	@GetMapping("/settings")
	public String openSettings(Model model) {
		
		model.addAttribute("title", "Change password");
		
		return "normal/settings";	
	}
	
	//change password handler
	@PostMapping("/change_passwwor")
	public String changePassword(@RequestParam("oldpassword") String oldpassword ,
			@RequestParam("newpassword") String newpassword,
			@RequestParam("cpassword") String cpassword,
			Principal principal,
			HttpSession session) {
		
		System.out.println("OLD Password : " + oldpassword);
		System.out.println("New Password : " + newpassword);
		
		User currentUser = this.userRepository.getUserByUserEmail(principal.getName());
		System.out.println(currentUser.getPassword());
		
		// Check if new password and confirm password match
	    if (!newpassword.equals(cpassword)) {
	        session.setAttribute("message", new Message("New password and confirm password do not match.", "danger"));
	        return "redirect:/user/settings";
	    }
		
		if(this.bCryptPasswordEncoder.matches(oldpassword, currentUser.getPassword())) {
			
			//change the password
			
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your password is successfully changed", "success"));
			
			//logout the user
			 SecurityContextHolder.clearContext();  // Clear the security context (logout)
		     session.invalidate();  // Invalidate the session
			return "redirect:/singin?logout";
			
		}else {
			//error
			session.setAttribute("message", new Message("Please Enter correct old password", "danger"));
			return "redirect:/user/settings";
		}
		
//		return "redirect:/user/index";
	}
	
}
