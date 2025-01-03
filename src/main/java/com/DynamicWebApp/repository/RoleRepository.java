package com.DynamicWebApp.repository;

import com.DynamicWebApp.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.DynamicWebApp.entity.Role;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleCodeEng(String roleCodeEng);
    Optional<Role> findByRoleCodePer(String roleCodePer);  // اضافه کردن این خط
}

