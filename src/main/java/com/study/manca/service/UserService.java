package com.study.manca.service;

import com.study.manca.dto.UserRequest;
import com.study.manca.dto.UserResponse;
import com.study.manca.entity.User;
import com.study.manca.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 전체 사용자 조회 (GET)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 사용자 조회 (GET)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return UserResponse.from(user);
    }

    // 사용자 생성 (POST)
    @Transactional
    public UserResponse create(UserRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        User user = request.toEntity();
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    // 사용자 전체 수정 (PUT)
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.update(request.getName(), request.getEmail(), request.getPhone());
        return UserResponse.from(user);
    }

    // 사용자 부분 수정 (PATCH)
    @Transactional
    public UserResponse updatePartial(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.updatePartial(request.getName(), request.getEmail(), request.getPhone());
        return UserResponse.from(user);
    }

    // 사용자 삭제 (DELETE)
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
