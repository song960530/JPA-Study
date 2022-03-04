package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.common.ResultMessage;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.repository.MemberJpaRepository;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberJpaRepository memberJpaRepository;

    @GetMapping("/v1/members")
    public ResponseEntity<ResultMessage> searchMemberV1(MemberSearchCondition condition) {
        return new ResponseEntity<>(ResultMessage.of(memberJpaRepository.searchByWhere(condition), HttpStatus.OK), HttpStatus.OK);
    }
}
