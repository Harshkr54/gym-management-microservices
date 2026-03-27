package com.authservice.service;

import com.authservice.entity.MemberCredential;
import com.authservice.repository.MemberCredentialRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    private final MemberCredentialRepository memberCredentialRepository;

    public CustomerUserDetailsService(MemberCredentialRepository memberCredentialRepository) {
        this.memberCredentialRepository = memberCredentialRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Fetch the user from the authdb database
        MemberCredential credential = memberCredentialRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with email: " + email));

        // 2. Convert your UserCredential into a Spring Security "User" object
        return new org.springframework.security.core.userdetails.User(
                credential.getEmail(),
                credential.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(credential.getRole()))
        );
    }
}
