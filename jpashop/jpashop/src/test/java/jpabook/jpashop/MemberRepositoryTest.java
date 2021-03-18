//package jpabook.jpashop;
//import jpabook.jpashop.domain.Member;
//import jpabook.jpashop.repository.MemberRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.Assert.*;
//
//@RunWith(SpringRunner.class)    // junit한테 스프링 테스트한다고 알려줌
//@SpringBootTest
//public class MemberRepositoryTest {
//    // MemberRepository 의존관계 주입
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Test
//    @Transactional
////    @Rollback(false): TEST는 실행시 DB에 넣고 실행 후 삭제(Rollback)함
//    public void testMember() throws Exception {
//        // given
//        Member member = new Member();
//        member.setUsername("memberB");
//
//        // when
//        Long saveId = memberRepository.save(member);
//        Member findMember = memberRepository.find(saveId);
//
//        // then
//        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
//        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
//    }
//
//
//
//}