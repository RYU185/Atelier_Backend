package com.dw.artgallery.controller;

import com.dw.artgallery.DTO.*;
import com.dw.artgallery.jwt.TokenProvider;
import com.dw.artgallery.model.User;
import com.dw.artgallery.repository.UserRepository;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, TokenProvider tokenProvider, UserRepository userRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    //  회원가입
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(userService.registerUser(userDTO), HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUserId(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER")
                .replace("ROLE_", "");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userId = userDetails.getUsername();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("role", role);
        response.put("isArtist", user.isArtist());
        response.put("nickname", user.getNickName());

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

    // 이메일로 아이디 찾기 API
    @PostMapping("/findid")
    public ResponseEntity<FindIdDTO.ResponseDTO> findIdByEmail(@RequestBody FindIdDTO.RequestDTO request) {
        // 이메일로 아이디를 찾고 결과 반환
        FindIdDTO.ResponseDTO response = userService.findIdByEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-authcode")
    public ResponseEntity<FindPwDTO.ResponseDTO> sendAuthCode(@RequestBody FindPwDTO.SendCodeRequest request) {
        return ResponseEntity.ok(userService.sendDummyAuthCode(request));
    }

    @PostMapping("/verify-authcode")
    public ResponseEntity<FindPwDTO.ResponseDTO> verifyAuthCode(@RequestBody FindPwDTO.VerifyCodeRequest request) {
        return ResponseEntity.ok(userService.verifyDummyAuthCode(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<FindPwDTO.ResponseDTO> resetPassword(@RequestBody FindPwDTO.ResetPwRequest request) {
        return ResponseEntity.ok(userService.resetPassword(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserGetDTO> getMyInfo(Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateMyInfo(@RequestBody UserUpdateDTO userUpdateDTO, Authentication authentication) {
        String userId = authentication.getName();

        // 비밀번호 변경을 시도하는 경우에만 현재 비밀번호 확인
        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            if (userUpdateDTO.getCurrentPassword() == null || userUpdateDTO.getCurrentPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("새 비밀번호를 설정하려면 현재 비밀번호를 입력해야 합니다.");
            }
            if (!userService.verifyCurrentPassword(userId, userUpdateDTO.getCurrentPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("현재 비밀번호가 일치하지 않습니다.");
            }
        }

        try {
            userService.updateUser(userId, userUpdateDTO);
            return ResponseEntity.ok("회원 정보 수정 완료");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 수정 실패");
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Boolean>> checkId(@RequestParam String userId) {
        boolean exists = userService.isUserIdExists(userId);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
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

    @GetMapping("/no-artist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersWithoutArtist() {
        return ResponseEntity.ok(userService.getUsersWithoutArtist());
    }
}

