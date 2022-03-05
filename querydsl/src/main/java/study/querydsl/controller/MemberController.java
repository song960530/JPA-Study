package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.common.ResultMessage;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.repository.MemberJpaRepository;
import study.querydsl.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/v1/members")
    public ResponseEntity<ResultMessage> searchMemberV1(MemberSearchCondition condition) {
        return new ResponseEntity<>(ResultMessage.of(memberJpaRepository.searchByWhere(condition), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/v2/members")
    public ResponseEntity<ResultMessage> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
        return new ResponseEntity<>(ResultMessage.of(memberRepository.searchPageSimple(condition, pageable), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/v3/members")
    public ResponseEntity<ResultMessage> searchMemberV3(MemberSearchCondition condition, Pageable pageable) {
        return new ResponseEntity<>(ResultMessage.of(memberRepository.searchPageComplex(condition, pageable), HttpStatus.OK), HttpStatus.OK);
    }
}
