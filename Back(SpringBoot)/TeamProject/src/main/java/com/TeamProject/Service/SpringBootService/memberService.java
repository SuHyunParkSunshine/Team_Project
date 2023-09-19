package com.TeamProject.Service.SpringBootService;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.TeamProject.Domain.members;
import com.TeamProject.Dto.membersDTO;
import com.TeamProject.Repository.membersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class memberService {

    private final membersRepository memberrepository;
    private final BCryptPasswordEncoder secret;

    public ResponseEntity<String> registerService(membersDTO memberdto) {

        

        if (!memberdto.getPassword().equals(memberdto.getConfirmPassword())) {
            return ResponseEntity.ok("비밀번호를 확인해주세요.");
        } else {
            members member = new members();

            member.setName(memberdto.getName());
            member.setLoginEmail(memberdto.getLoginEmail());
            member.setPassword(secret.encode(memberdto.getPassword())); // password 암호화
            member.setConfirmPassword(member.getPassword());
            member.setPhoneNumber(memberdto.getPhoneNumber());
            member.setCompanyName(memberdto.getCompanyName());
            member.setRole("ROLE_MEMBER");
            member.setCreationDate(new Date());

            memberrepository.save(member);

            return ResponseEntity.ok("회원가입 성공.");
        }
    }  
}