package com.dw.artgallery.service;

import com.dw.artgallery.DTO.NoticeDTO;
import com.dw.artgallery.model.Notice;
import com.dw.artgallery.repository.NoticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    private final UserService userService;

    public NoticeService(NoticeRepository noticeRepository, UserService userService) {
        this.noticeRepository = noticeRepository;
        this.userService = userService;
    }

    // 모든 공지 조회
    public List<NoticeDTO> getAllNotices() {
        return noticeRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 공지 조회
    public NoticeDTO getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 ID의 공지를 찾을 수 없습니다."));
        return convertToDTO(notice);
    }

    // 제목 조회
    public List<NoticeDTO> getNoticesByTitle(String title) {
        return noticeRepository.findByTitleContaining(title) // 💡 noticetitle → title
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public NoticeDTO saveNotice(NoticeDTO dto) {
        if (dto.getTitle() == null || dto.getContent() == null) { // ✅ 필드명 수정
            throw new IllegalArgumentException("제목과 내용을 모두 작성해주세요.");
        }

        Notice notice = new Notice();

        notice.setTitle(dto.getTitle());      // ✅ 필드명 수정
        notice.setContent(dto.getContent());
        notice.setCreatedDate(LocalDate.now());

        return convertToDTO(noticeRepository.save(notice));
    }

    // 삭제
    public String deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 공지를 찾을 수 없습니다."));

        noticeRepository.deleteById(id);
        return "공지 ID: " + id + " 삭제 완료";
    }

    private NoticeDTO convertToDTO(Notice notice) {
        return new NoticeDTO(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedDate() // ✅ 이 줄 추가
        );
    }
    public NoticeDTO updateNotice(Long id, NoticeDTO dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공지사항이 존재하지 않습니다."));

        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());

        return convertToDTO(noticeRepository.save(notice));
    }
    public Page<NoticeDTO> getPagedNotices(int page, int size, String sortDir) {
        Sort sort = sortDir.equals("createdDate,asc") ?
                Sort.by("createdDate").ascending() :
                Sort.by("createdDate").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return noticeRepository.findAll(pageable)
                .map(this::convertToDTO); // Page<Notice> → Page<NoticeDTO>
    }
}
