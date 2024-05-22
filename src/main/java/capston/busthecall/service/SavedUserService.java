package capston.busthecall.service;


import capston.busthecall.domain.Member;
import capston.busthecall.repository.MemberRepository;
import capston.busthecall.domain.dto.request.JoinRequest;
import capston.busthecall.domain.dto.response.SavedInfo;
import capston.busthecall.security.authentication.authority.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SavedUserService {

    private final MemberRepository memberRepository;
    private static final Long NOT_EXIST_MEMBER  = -1L;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public SavedInfo join(JoinRequest request) {
        Long existMember = validateMember(request.getEmail());

        if(!Objects.equals(existMember,NOT_EXIST_MEMBER)) {
            Member member = getMember(existMember);

            validatePassword(member, request.getPassword());

            return getExistedMember(member.getId());
        }

        Long saveMemberId = saveMember(request);

        return getNewMember(saveMemberId);
    }

    private Long validateMember(String email) {
        Optional<Member> source = memberRepository.findByEmail(email);

        if(source.isPresent()) {
            return source.get().getId();
        }

        return NOT_EXIST_MEMBER;
    }

    private Member getMember(Long existMember) {
        return (Member) Objects.requireNonNull(memberRepository.findById(existMember).orElse(null));
    }


    private void validatePassword(Member member, String requestPassword) {
        if(encoder.encode(member.getPassword()).equals(requestPassword))
            throw new IllegalArgumentException();
    }

    private Long saveMember(JoinRequest request) {
        return memberRepository
                .save(
                        Member.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(encoder.encode(request.getPassword()))
                                .build())
                .getId();
    }

    private SavedInfo getNewMember(Long memberId) {
        return SavedInfo.builder()
                .id(memberId)
                .roles(Roles.MEMBER)
                .isRegistered(true)
                .build();
    }

    private SavedInfo getExistedMember(Long memberId) {
        return SavedInfo.builder()
                .id(memberId)
                .roles(Roles.MEMBER)
                .isRegistered(false)
                .build();
    }

}
