package com.ecom.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ecom.entity.User;

@Repository
public interface UserDao extends CrudRepository<User, Long> {

	Optional<User> findByUserName(String usrname);



	User findByEmail(String toEmail);

	public Page findAll(Pageable pageable);
	
	boolean existsByUserName(String userName);
 
}
	
	

