package com.member_service.service;

import com.member_service.dto.MemberRegistrationDto;
import com.member_service.entity.Member;
import com.member_service.exception.ResourceNotFoundException;
import com.member_service.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // --- REGISTRATION LOGIC (Has the duplicate check) ---
    public Member saveMember(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new RuntimeException("Profile creation failed: A member profile with email " + member.getEmail() + " already exists.");
        }
        return memberRepository.save(member);
    }

    // --- NEW: UPDATE PICTURE LOGIC (Bypasses the duplicate check) ---
    public void updateProfilePicture(String email, String imageUrl) {
        Member member = getMemberByEmail(email);
        member.setProfilePictureUrl(imageUrl);
        memberRepository.save(member); // Uses raw JPA save to just run an SQL UPDATE
    }

    // --- UTILITY METHODS ---
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with email: " + email));
    }

    public List<Member> getAllMembers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Member> members = memberRepository.findAll(pageable);
        return members.getContent();
    }

    public void deleteMemberById(long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        memberRepository.delete(member);
    }

    public void updateRegistrationbyId(long id, MemberRegistrationDto registrationDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        member.setFirstName(registrationDto.getFirstName());
        member.setLastName(registrationDto.getLastName());
        member.setPhoneNumber(registrationDto.getPhoneNumber());
        member.setDateOfBirth(registrationDto.getDateOfBirth());

        memberRepository.save(member);
    }
}