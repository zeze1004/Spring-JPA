package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // Get 클라이언트가 요청 받기
    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm"; // html 디렉토리 경로
    }
    // Post 클라이언트가 요청 보내기
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {    // 에러가 있어도 BindingResult가 에러 결과를 갖고 아래 코드 동작

        // 에러 발생 시 페이지 넘어가지 않게 하기
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/"; // 회원가입 폼 작성 완료시 홈으로 이동
    }

    @GetMapping("/members")
    public String list(Model model) {   // Model이란 객체를 통해 화면에 데이터 전달
        List<Member> members = memberService.findMember();
        model.addAttribute("members", members);
        // 위 코드 두 줄을 ctrl+alt+n으로 줄일 수 있음
        // model.addAttribute("members", memberService.findMember());

        return "members/memberList";
    }
}
