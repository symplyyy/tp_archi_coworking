package com.hotel.member_service.controller;

import com.hotel.member_service.model.Member;
import com.hotel.member_service.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@Tag(name = "Membres", description = "Gestion des membres et de leurs abonnements")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "Lister tous les membres")
    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @Operation(summary = "Récupérer un membre par son id")
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @Operation(summary = "Créer un membre")
    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(member));
    }

    @Operation(summary = "Modifier un membre")
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member member) {
        return ResponseEntity.ok(memberService.updateMember(id, member));
    }

    @Operation(summary = "Supprimer un membre")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mettre à jour la suspension d'un membre")
    @PatchMapping("/{id}/suspension")
    public ResponseEntity<Member> updateSuspension(@PathVariable Long id, @RequestParam boolean suspended) {
        return ResponseEntity.ok(memberService.updateSuspension(id, suspended));
    }
}
