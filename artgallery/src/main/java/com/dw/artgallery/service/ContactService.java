package com.dw.artgallery.service;

import com.dw.artgallery.DTO.ContactDTO;
import com.dw.artgallery.model.Contact;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.ContactRepository;
import com.dw.artgallery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 문의 생성
    @Transactional
    public ContactDTO createContact(ContactDTO contactDTO) {
        try {
            log.info("문의 생성 시도 - 이름: {}, 이메일: {}", contactDTO.getName(), contactDTO.getEmail());

            // 1. DTO → Entity
            Contact contact = contactDTO.toEntity();
            contact.setCreatedDate(LocalDateTime.now());
            contact.setStatus("대기중");

            // 2. 유저 연결
            if (contactDTO.getUserId() != null && !contactDTO.getUserId().isEmpty()) {
                log.debug("🔗 userId 전달됨: {}", contactDTO.getUserId());
                User user = userRepository.findByUserId(contactDTO.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                contact.setUser(user); // ✅ 핵심 연결 지점
            } else {
                log.debug("⚠️ userId가 null 또는 빈 문자열임 (비회원 문의)");
            }

            // 3. 저장
            Contact savedContact = contactRepository.save(contact);
            log.info("문의 생성 완료 - ID: {}", savedContact.getId());

            // 4. 웹소켓 알림 전송
            notificationService.sendContactNotification(contact.getName(), contact.getTitle());

            // 5. 응답 DTO 반환
            return ContactDTO.toDTO(savedContact);

        } catch (Exception e) {
            log.error("문의 생성 실패 - 이름: {}, 이메일: {}, 에러: {}", contactDTO.getName(), contactDTO.getEmail(), e.getMessage());
            throw e;
        }
    }

    // 문의 조회
    public ContactDTO getContact(Long id) {
        try {
            log.info("문의 조회 시도 - ID: {}", id);
            Contact contact = contactRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("문의를 찾을 수 없음 - ID: {}", id);
                        return new RuntimeException("Contact not found");
                    });
            log.info("문의 조회 완료 - ID: {}, 제목: {}", id, contact.getTitle());
            return ContactDTO.toDTO(contact);
        } catch (Exception e) {
            log.error("문의 조회 실패 - ID: {}, 에러: {}", id, e.getMessage());
            throw e;
        }
    }
    // 모든 문의 조회 (관리자용)
    public List<ContactDTO> getAllContacts() {
        try {
            log.info("모든 문의 조회 시도");
            List<Contact> contacts = contactRepository.findAll();
            log.info("모든 문의 조회 완료 - 총 문의 수: {}", contacts.size());
            return contacts.stream()
                    .map(ContactDTO::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("모든 문의 조회 실패 - 에러: {}", e.getMessage());
            throw e;
        }
    }
    // 이메일로 문의 조회
    public List<ContactDTO> getContactsByEmail(String email) {
        try {
            log.info("이메일로 문의 조회 시도 - 이메일: {}", email);
            List<Contact> contacts = contactRepository.findByEmail(email);
            log.info("이메일로 문의 조회 완료 - 이메일: {}, 조회된 문의 수: {}", email, contacts.size());
            return contacts.stream()
                    .map(ContactDTO::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("이메일로 문의 조회 실패 - 이메일: {}, 에러: {}", email, e.getMessage());
            throw e;
        }
    }
    // 사용자 ID로 문의 조회
    public List<ContactDTO> getContactsByUserId(String userId) {
        try {
            log.info("사용자 문의 조회 시도 - userId: {}", userId);
            List<Contact> contacts = contactRepository.findByUser_UserId(userId);
            log.info("사용자 문의 조회 완료 - userId: {}, 조회된 문의 수: {}", userId, contacts.size());
            return contacts.stream()
                    .map(ContactDTO::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("사용자 문의 조회 실패 - userId: {}, 에러: {}", userId, e.getMessage());
            throw e;
        }
    }
    // 제목으로 문의 검색
    public List<ContactDTO> searchContactsByTitle(String title) {
        try {
            log.info("제목으로 문의 검색 시도 - 제목: {}", title);
            List<Contact> contacts = contactRepository.findByTitleContaining(title);
            log.info("제목으로 문의 검색 완료 - 제목: {}, 검색된 문의 수: {}", title, contacts.size());
            return contacts.stream()
                    .map(ContactDTO::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("제목으로 문의 검색 실패 - 제목: {}, 에러: {}", title, e.getMessage());
            throw e;
        }
    }
    // 문의글 소유자 확인
    public boolean isOwner(Long contactId, String userId) {
        try {
            log.info("문의글 소유자 확인 시도 - contactId: {}, userId: {}", contactId, userId);
            Contact contact = contactRepository.findById(contactId)
                    .orElseThrow(() -> {
                        log.error("문의를 찾을 수 없음 - ID: {}", contactId);
                        return new RuntimeException("Contact not found");
                    });
            if (contact.getUser() == null) {
                log.info("문의글 소유자 없음 - contactId: {}", contactId);
                return false;
            }
            boolean isOwner = contact.getUser().getUserId().equals(userId);
            log.info("문의글 소유자 확인 완료 - contactId: {}, userId: {}, 결과: {}",
                    contactId, userId, isOwner);
            return isOwner;
        } catch (Exception e) {
            log.error("문의글 소유자 확인 실패 - contactId: {}, userId: {}, 에러: {}",
                    contactId, userId, e.getMessage());
            return false;
        }
    }
}