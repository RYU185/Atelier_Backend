package com.dw.artgallery.repository;

import com.dw.artgallery.model.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByTitleContaining(String title); // ✅ 필드명 변경에 맞춰 수정
    Page<Notice> findAllByOrderByCreatedDateDesc(Pageable pageable);
}