package com.authservice.repository;

import com.authservice.entity.MemberCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberCredentialRepository extends JpaRepository<MemberCredential, Long> {
    Optional<MemberCredential> findByEmail(String email);

    boolean existsByEmail(String email);
}