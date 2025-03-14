package org.example.swaggerexam.service;

import lombok.RequiredArgsConstructor;
import org.example.swaggerexam.dto.LoginRequestDto;
import org.example.swaggerexam.dto.LoginResponseDto;
import org.example.swaggerexam.dto.RegisterRequestDto;
import org.example.swaggerexam.domain.User;
import org.example.swaggerexam.dto.UserDto;
import org.example.swaggerexam.repository.UserRepository;
import org.example.swaggerexam.jwt.utill.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public String register(RegisterRequestDto registerRequestDto) {
        String email = registerRequestDto.getEmail();
        String password = registerRequestDto.getPassword();


        if (userRepository.findByEmail(email).isPresent()) {
            return "Email already exists";
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return "User registered successfully";
    }

    @Transactional
    public LoginResponseDto  login(LoginRequestDto loginRequestDto) {
        Optional<User> user = userRepository.findByEmail(loginRequestDto.getEmail());
        if (user.isPresent() && passwordEncoder.matches(loginRequestDto.getPassword(), user.get().getPassword())) {
            // JWT 토큰 생성 로직
            String accessToken = jwtUtil.generateAccessToken(user.get().getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(user.get().getEmail());

            // REFRESH 토큰을 데이터베이스에 저장
            user.get().setRefreshToken(refreshToken);
            userRepository.save(user.get());

            return new LoginResponseDto(accessToken, refreshToken);
        }
        throw new RuntimeException("Invalid credentials");

    }

    @Transactional
    public String logout(String accessToken, String refreshToken) {
        // ACCESS 토큰 무효화
        jwtUtil.invalidateToken(accessToken);

        // REFRESH 토큰 삭제
        if (refreshToken != null) {
            Optional<User> user = userRepository.findByRefreshToken(refreshToken);
            if (user.isPresent()) {
                user.get().setRefreshToken(null); // REFRESH 토큰을 null로 설정하여 삭제
                userRepository.save(user.get());
            }
        }

        return "Logout successful";
    }



    // 사용자 목록 조회
    @Transactional(readOnly = true)
    public Page<UserDto> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(UserDto::fromEntity);

    }

    // 사용자 정보 조회
    @Transactional(readOnly = true)
    public  UserDto  getUser(Long id) {
        return userRepository.findById(id).stream().map(UserDto::fromEntity).findFirst().orElse(null);
    }

    public String refreshAccessToken(String refreshToken) {
        Optional<User> user = userRepository.findByRefreshToken(refreshToken);
        if (user.isPresent()) {
            String newAccessToken = jwtUtil.generateAccessToken(user.get().getEmail());
            return newAccessToken;
        }
        throw new RuntimeException("Invalid refresh token");
    }
}
