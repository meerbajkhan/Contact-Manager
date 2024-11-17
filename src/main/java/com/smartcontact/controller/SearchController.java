package com.smartcontact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontact.entities.ContactDetails;
import com.smartcontact.entities.User;
import com.smartcontact.repository.ContactRepository;
import com.smartcontact.repository.UserRepository;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//search handler
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
		
		System.out.println(query);
		
		User user = this.userRepository.getUserByUserEmail(principal.getName());
		
		List<ContactDetails> contactDetails = this.contactRepository.findByContactNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contactDetails);
	}

}
