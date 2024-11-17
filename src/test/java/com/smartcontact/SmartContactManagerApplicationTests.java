package com.smartcontact;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.smartcontact.entities.User;
import com.smartcontact.repository.UserRepository;

@SpringBootTest
class SmartContactManagerApplicationTests {
	
	@Autowired
	private UserRepository userRepository;

	@Test
	void contextLoads() {
	}
	
	@Test
	void validSmartContactManager() {
		
		String username = "sachin@gmail.com";
		
		User user = this.userRepository.getUserByUserEmail(username);
		
		assertEquals(username, user.getEmail());
	}

}
