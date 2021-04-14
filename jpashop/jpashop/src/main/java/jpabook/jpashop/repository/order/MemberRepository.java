package jpabook.jpashop.repository.order;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public class MemberRepository extends JpaRepository<Member, Long> {
    // findALL(), save()... 등 Jpa 내의 함수를 바로 쓸 수 있음
    // jpa는 간단한 crud 기능을 제공함

}
