package com.study.manca.controller;

import com.study.manca.dto.RentalRequest;
import com.study.manca.dto.RentalResponse;
import com.study.manca.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping
    public ResponseEntity<List<RentalResponse>> getAllRentals() {
        return ResponseEntity.ok(rentalService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> getRental(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.findById(id));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<RentalResponse>> getRentalsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(rentalService.findByMemberId(memberId));
    }

    @PostMapping
    public ResponseEntity<RentalResponse> createRental(@RequestBody RentalRequest request) {
        RentalResponse response = rentalService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<RentalResponse> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.returnBook(id));
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        rentalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
