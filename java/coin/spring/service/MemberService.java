package coin.spring.service;  // 서비스 클래스를 포함하는 패키지

import coin.spring.domain.dao.Member1;
import coin.spring.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// 회원 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
@RequiredArgsConstructor  // 생성자 주입을 위한 자동 생성자 생성
public class MemberService {

    private final MemberRepository memberRepository;  // MemberRepository 인스턴스 변수

    // 회원가입 메서드
    public Long join(Member1 member1) {
        // 같은 이름이 있는 중복회원 검증
        validateDuplicateMember(member1);
        // 회원 정보를 저장하고, 회원 ID 반환
        this.memberRepository.save(member1);
        return member1.getId();
    }

    // 중복회원 조회 메서드
    private void validateDuplicateMember(Member1 member1) {
        // 주어진 로그인 ID로 회원 조회
        Optional<Member1> findMember = memberRepository.findByLoginId(member1.getLoginId());
        // 이미 존재하는 회원일 경우 예외 발생
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 수정 메서드
    public Long save(Member1 member1) {
        // 회원 정보를 저장하고, 회원 ID 반환
        memberRepository.save(member1);
        return member1.getId();
    }

    // 전체 회원 조회 메서드
    public List<Member1> findMember() {
        // 모든 회원 정보를 반환
        return memberRepository.findAll();
    }

    // 특정 회원 조회 메서드
    public Optional<Member1> findOne(Long memberId) {
        // 주어진 ID로 회원 조회
        return memberRepository.findById(memberId);
    }

    // 회원 삭제 메서드
    public void delete(Member1 member1) {
        // 회원 정보를 삭제
        this.memberRepository.delete(member1);
    }
}
