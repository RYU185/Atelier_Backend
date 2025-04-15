package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.LoginDTO;
import com.dw.artgallery.DTO.UserDTO;
import com.dw.artgallery.DTO.UserGetDTO;
import com.dw.artgallery.jwt.TokenProvider;
import com.dw.artgallery.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUserId(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        // 🔥 권한(ROLE_ADMIN / ROLE_USER 등) 가져오기
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        // 🔁 token + role 같이 보내기
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        response.put("role", role);

        return ResponseEntity.ok(response);
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

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserGetDTO> getMyInfo(Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> updateMyInfo(@RequestBody UserDTO userDTO, Authentication authentication) {
        String userId = authentication.getName();
        userService.updateUser(userId, userDTO);
        return ResponseEntity.ok("회원 정보 수정 완료");
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
    public ResponseEntity<UserGetDTO> getRealNameUser(@PathVariable String realname) {
        return ResponseEntity.ok(userService.getRealNameUser(realname)); // 여기서도 변수명 맞추기
    }

    // 최근 가입한 유저순으로 조회 (관리지만 가능)
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserGetDTO>> getRecentUsers() {
        return ResponseEntity.ok(userService.getRecentUsers());
    }

    // 포인트가 많은 유저순으로 조회 (관리자만 가능)
    @GetMapping("/top-points")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserGetDTO>> getTopUsersByPoints() {
        return ResponseEntity.ok(userService.getTopUsersByPoints());
    }
    //  UserID로 회원 조회
    @GetMapping("/id/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserGetDTO> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    //  Nickname으로 회원 조회
    @GetMapping("/nickname/{nickname}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserGetDTO>> getUsersByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.getUsersByNickname(nickname));
    }

    //  Email로 회원 조회
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserGetDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    //  Address로 회원 조회
    @GetMapping("/address/{address}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserGetDTO>> getUsersByAddress(@PathVariable String address) {
        return ResponseEntity.ok(userService.getUsersByAddress(address));
    }
}

