package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.NoticeDTO;
import com.dw.artgallery.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public List<NoticeDTO> getAllNotices() {
        return noticeService.getAllNotices();
    }

    @GetMapping("/{id}")
    public NoticeDTO getNoticeById(@PathVariable Long id) {
        return noticeService.getNoticeById(id);
    }

    @GetMapping("/search")
    public List<NoticeDTO> getNoticesByTitle(@RequestParam String title) {
        return noticeService.getNoticesByTitle(title);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public NoticeDTO createOrUpdateNotice(@RequestBody NoticeDTO dto) {
        return noticeService.saveNotice(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteNotice(@PathVariable Long id) {
        return noticeService.deleteNotice(id);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public NoticeDTO updateNotice(@PathVariable Long id, @RequestBody NoticeDTO dto) {
        return noticeService.updateNotice(id, dto);
    }
    @GetMapping("/paged")
    public Page<NoticeDTO> getPagedNotices(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "createdDate,desc") String sort
    ) {
        return noticeService.getPagedNotices(page, size, sort);
    }
}
