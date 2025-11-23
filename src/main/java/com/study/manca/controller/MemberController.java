package com.study.manca.controller;

import com.study.manca.dto.MemberResponse;
import com.study.manca.dto.MemberRequest;
import com.study.manca.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 전체 사용자 목록 조회
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllUsers() {
        List<MemberResponse> users = memberService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * 특정 사용자 조회
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getUserById(@PathVariable Long id) {
        MemberResponse user = memberService.findById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 사용자 생성
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<MemberResponse> createUser(@RequestBody MemberRequest request) {
        MemberResponse createdUser = memberService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * 사용자 전체 수정
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateUser(
            @PathVariable Long id,
            @RequestBody MemberRequest request) {
        MemberResponse updatedUser = memberService.update(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 사용자 부분 수정
     * PATCH /api/users/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<MemberResponse> updateUserPartial(
            @PathVariable Long id,
            @RequestBody MemberRequest request) {
        MemberResponse updatedUser = memberService.updatePartial(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 사용자 삭제
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
