package com.dw.artgallery.DTO;

import com.dw.artgallery.model.Contact;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContactDTO {
    private Long id;
    private String name;
    private String title;
    private String email;
    private String message;
    private LocalDateTime createdDate;
    private String status;
    private String response;
    private String userId;

    private Boolean isMember;

    public static ContactDTO toDTO(Contact contact) {
        ContactDTO dto = new ContactDTO();
        dto.setId(contact.getId());
        dto.setName(contact.getName());
        dto.setTitle(contact.getTitle());
        dto.setEmail(contact.getEmail());
        dto.setMessage(contact.getMessage());
        dto.setCreatedDate(contact.getCreatedDate());
        dto.setStatus(contact.getStatus());
        dto.setResponse(contact.getResponse());
        dto.setIsMember(contact.getIsMember());
        if (contact.getUser() != null) {
            dto.setUserId(contact.getUser().getUserId());
        }
        return dto;
    }

    public Contact toEntity() {
        Contact contact = new Contact();
        contact.setName(this.name);
        contact.setTitle(this.title);
        contact.setEmail(this.email);
        contact.setMessage(this.message);
        contact.setIsMember(this.isMember);
        return contact;
    }
}