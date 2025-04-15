package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.ContactDTO;
import com.dw.artgallery.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    // 문의 생성 - 모든 사용자 접근 가능
    @PostMapping
    public ResponseEntity<ContactDTO> createContact(@RequestBody ContactDTO contactDTO) {
        return new ResponseEntity<>(contactService.createContact(contactDTO), HttpStatus.CREATED);
    }

    // 특정 문의 조회 - 관리자 또는 작성자만 접근 가능
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @contactService.isOwner(#id, principal.username)")
    public ResponseEntity<ContactDTO> getContact(@PathVariable Long id) {
        return new ResponseEntity<>(contactService.getContact(id), HttpStatus.OK);
    }

    // 모든 문의 조회 - 관리자만 접근 가능
    @GetMapping
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ContactDTO>> getAllContacts() {
        return new ResponseEntity<>(contactService.getAllContacts(), HttpStatus.OK);
    }

    // 이메일로 문의 조회 - 관리자 또는 본인만 접근 가능
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #email == principal.username")
    public ResponseEntity<List<ContactDTO>> getContactsByEmail(@PathVariable String email) {
        return new ResponseEntity<>(contactService.getContactsByEmail(email), HttpStatus.OK);
    }

    // 사용자 ID로 문의 조회 - 관리자 또는 본인만 접근 가능
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #userId == principal.username")
    public ResponseEntity<List<ContactDTO>> getContactsByUserId(@PathVariable String userId) {
        return new ResponseEntity<>(contactService.getContactsByUserId(userId), HttpStatus.OK);
    }

    // 제목으로 문의 검색 - 관리자만 접근 가능
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ContactDTO>> searchContactsByTitle(@RequestParam String title) {
        return new ResponseEntity<>(contactService.searchContactsByTitle(title), HttpStatus.OK);
    }
}
