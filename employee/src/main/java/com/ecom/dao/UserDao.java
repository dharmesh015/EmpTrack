package com.ecom.dao;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.entity.User;
@Repository
public interface UserDao extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
    
    // Updated queries to include active status
    Page<User> findByRole_RoleNameAndActiveTrue(String roleName, Pageable pageable);
    
    
    @Query("SELECT u FROM User u JOIN u.role r WHERE r.roleName = :role AND u.active = true AND (" +
    	    "LOWER(u.userName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
    	    "LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
    	    "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
    	    "LOWER(u.contactNumber) LIKE LOWER(CONCAT('%', :query, '%')))")
    	Page<User> searchUsersByRoleAndActiveTrue(@Param("role") String role, @Param("query") String query, Pageable pageable);

}