package com.dw.artgallery.repository;

import com.dw.artgallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String > {
        Optional<User> findByRealName(String realName);
        List<User> findAllByOrderByEnrolmentDateDesc();
        List<User> findAllByOrderByPointDesc();
        List<User> findByNickName(String nickName);
        Optional<User> findByEmail(String email);
        List<User> findByAddress(String address);
        Optional<User> findByUserId(String userId);
        List<User> findByIsArtistFalse();
        List<User> findAllByAuthority_AuthorityName(String roleName);


}
