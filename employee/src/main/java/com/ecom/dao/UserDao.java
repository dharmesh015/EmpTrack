package com.ecom.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.entity.User;
import com.ecom.proxy.UserProxy;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

	Optional<User> findByUserName(String usrname);


	Optional<User> findByEmail(String toEmail);

	public Page findAll(Pageable pageable);
	
	boolean existsByUserName(String userName);
	


	 
	@Query("SELECT DISTINCT u FROM User u JOIN u.role r WHERE r.roleName = 'USER' AND (" +
		       "LOWER(u.userName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
		       "LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
		       "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
		       "u.contactNumber LIKE CONCAT('%', :query, '%') OR " +
		       "LOWER(u.gender) LIKE LOWER(CONCAT('%', :query, '%')))")
		Page<User> searchUsers(@Param("query") String query, Pageable pageable);
	
	 Page<User> findByRole_RoleName(String roleName, Pageable pageable);
	 
	
}
	
	

