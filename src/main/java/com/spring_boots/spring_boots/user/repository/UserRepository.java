package com.spring_boots.spring_boots.user.repository;

import com.spring_boots.spring_boots.user.domain.Users;
import com.spring_boots.spring_boots.user.dto.response.AdminUserResponseDto;
import com.spring_boots.spring_boots.user.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    boolean existsByUserRealId(String userRealId);
    Optional<Users> findByUserRealId(String userRealId);

    @Query("""
        SELECT new com.spring_boots.spring_boots.user.dto.response.AdminUserResponseDto(u.userId, u.username, u.userRealId, u.email, u.role, u.provider, u.createdAt, u.isDeleted)
        FROM Users u
    """)
    Page<AdminUserResponseDto> findUsers(Pageable pageable);

    @Query("""
            SELECT COUNT(u)
            FROM Users u
            WHERE u.role="ADMIN"
            """)
    long countAdmin();
}
