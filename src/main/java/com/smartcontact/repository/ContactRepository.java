package com.smartcontact.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontact.entities.ContactDetails;
import com.smartcontact.entities.User;

public interface ContactRepository extends JpaRepository<ContactDetails, Integer> {
	
	//pagination
	
	@Query("from ContactDetails as c where c.user.userId =:userId")
	// CurrentPage - page
	// Record per page -
	public Page<ContactDetails> findContactByUser(@Param("userId") int userId, Pageable pageable);
	
	@Modifying
	@Query(value = "delete from contact c where c.c_id= :contactId", nativeQuery = true)
	void contactDeleteById(@Param("contactId") int contactId);
	
	//search data query
	public List<ContactDetails> findByContactNameContainingAndUser(String contactName, User user);
} 
