package com.DynamicWebApp.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.DynamicWebApp.entity.OurUsers;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<OurUsers, Integer> {

    Optional<OurUsers> findByEmail(String email);
}