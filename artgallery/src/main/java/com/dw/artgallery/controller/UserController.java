package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.LoginDTO;
import com.dw.artgallery.DTO.UserDTO;
import com.dw.artgallery.DTO.UserGetDTO;
import com.dw.artgallery.jwt.TokenProvider;
import com.dw.artgallery.model.User;
import com.dw.artgallery.service.UserService;
import com.dw.exception.UnauthorizedUserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    //  회원가입
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(userService.registerUser(userDTO), HttpStatus.CREATED);
    }

    //  로그인 (JWT 반환)
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUserId(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 🔥 JWT 생성
        String jwt = tokenProvider.createToken(authentication);

        return ResponseEntity.ok(jwt);
    }

    // 로그아웃 (세션 기반, JWT 사용 시 서버에서 처리 필요 없음)
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        return ResponseEntity.ok("로그아웃 성공");
    }

    //  모든 회원 조회 (관리자만 가능)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserGetDTO>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // realname으로 회원 조회 (관리자만 가능)
    @GetMapping("/realname/{realname}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getRealNameUser(@PathVariable String realname) {
        return ResponseEntity.ok(userService.getRealNameUser(realname)); // 여기서도 변수명 맞추기
    }

    // 최근 가입한 유저순으로 조회 (관리지만 가능)
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getRecentUsers() {
        return ResponseEntity.ok(userService.getRecentUsers());
    }

    // 포인트가 많은 유저순으로 조회 (관리자만 가능)
    @GetMapping("/top-points")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getTopUsersByPoints() {
        return ResponseEntity.ok(userService.getTopUsersByPoints());
    }

}
