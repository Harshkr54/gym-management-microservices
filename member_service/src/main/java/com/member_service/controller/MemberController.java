package com.member_service.controller;

import com.member_service.dto.MemberRegistrationDto;
import com.member_service.entity.Member;
import com.member_service.response.ApiResponse;
import com.member_service.service.EmailSender;
import com.member_service.service.FileStorageService;
import com.member_service.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RefreshScope
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final FileStorageService fileStorageService;

    @Autowired
    private EmailSender emailSender;

    public MemberController(MemberService memberService, FileStorageService fileStorageService) {
        this.memberService = memberService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_TRAINER', 'ROLE_MEMBER')")
    public ResponseEntity<ApiResponse<Member>> registerMemberProfile(
            @Valid @RequestBody MemberRegistrationDto request,
            @RequestHeader("loggedInUserEmail") String loggedInEmail) {

        Member newMember = new Member();
        newMember.setFirstName(request.getFirstName());
        newMember.setLastName(request.getLastName());
        newMember.setPhoneNumber(request.getPhoneNumber());
        newMember.setDateOfBirth(request.getDateOfBirth());
        newMember.setJoinDate(LocalDate.now());
        newMember.setEmail(loggedInEmail);

        Member savedMember = memberService.saveMember(newMember);

        String subject = "Welcome to our gym! Your profile is complete.";
        String message = "Thank you, " + request.getFirstName() + " " + request.getLastName() + ". You are now fully registered!";

        emailSender.sendEmail(loggedInEmail, subject, message);

        ApiResponse<Member> response = new ApiResponse<>(true, "Member profile created successfully!", savedMember);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- UPDATED AWS S3 UPLOAD ENDPOINT ---
    @PostMapping(value = "/upload-profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_TRAINER', 'ROLE_OWNER')")
    public ResponseEntity<ApiResponse<String>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("loggedInUserEmail") String email) {

        try {
            // 1. Upload to AWS S3 using our V1 Service
            String imageUrl = fileStorageService.uploadProfilePicture(file, email);

            // 2. Call the new bypass method to update the database silently!
            memberService.updateProfilePicture(email, imageUrl);

            ApiResponse<String> response = new ApiResponse<>(true, "Profile picture uploaded successfully!", imageUrl);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to upload image: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- ENDPOINT USED BY WORKOUT SERVICE (FEIGN CLIENT) ---
    @GetMapping("/check-profile")
    public ResponseEntity<Boolean> checkProfileExists(@RequestParam String email) {
        boolean exists = memberService.existsByEmail(email);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_TRAINER')")
    public ResponseEntity<ApiResponse<List<Member>>> getAllMembers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        List<Member> members = memberService.getAllMembers(pageNo, pageSize, sortBy, sortDir);
        ApiResponse<List<Member>> response = new ApiResponse<>(true, "Successfully fetched all members", members);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/details")
    @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_TRAINER', 'ROLE_MEMBER')")
    public ResponseEntity<String> getMemberProfile(
            @RequestHeader("loggedInUserEmail") String email,
            @RequestHeader("loggedInUserRole") String role) {

        String responseMessage = "Welcome to your profile, " + email + "! Your role is: " + role;
        return ResponseEntity.ok(responseMessage);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ResponseEntity<ApiResponse<String>> deleteMemberById(@PathVariable long id) {
        memberService.deleteMemberById(id);
        ApiResponse<String> response = new ApiResponse<>(true, "Member deleted successfully!", "Member Removed");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateRegistration(
            @PathVariable long id,
            @Valid @RequestBody MemberRegistrationDto registrationDto){
        memberService.updateRegistrationbyId(id, registrationDto);
        ApiResponse<String> response = new ApiResponse<>(true, "Member updated successfully!", "Member details updated");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}