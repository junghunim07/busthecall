package capston.busthecall.service;


import capston.busthecall.domain.Member;
import capston.busthecall.domain.dto.request.SaveUserRequest;
import capston.busthecall.domain.dto.response.SavedUserInfo;
import capston.busthecall.repository.MemberRepository;
import capston.busthecall.security.authentication.authority.Roles;
import capston.busthecall.support.token.TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SavedUserService {

    private final MemberRepository memberRepository;
    private final TokenGenerator tokenGenerator;
    private static final Long NOT_EXIST_MEMBER  = -1L;

    @Transactional
    public SavedUserInfo excute(SaveUserRequest request)
    {
        Long existMember = validateMember(request.getEmail());

        if(!Objects.equals(existMember,NOT_EXIST_MEMBER))
        {
            Member member = getMember(existMember);

            validatePassword(member, request.getPassword());

            return getExistedMember(member.getId());
        }

        Long saveMemberId =saveMember(request);

        return getNewMember(saveMemberId);
    }

    private Long validateMember(String email)
    {
        Optional<Member> source = memberRepository.findByEmailAndDeletedFalse(email);

        if(source.isPresent())
        {
            return source.get().getId();
        }

        return NOT_EXIST_MEMBER;
    }

    private Member getMember(Long existMember)
    {
        return (Member) Objects.requireNonNull(memberRepository.findByIdAndDeletedFalse(existMember).orElse(null));
    }


    private void validatePassword(Member member, String requestPassword)
    {
        if(!member.isPassword(requestPassword))
            throw new IllegalArgumentException();
    }

    private Long saveMember(SaveUserRequest request) {
        return memberRepository
                .save(
                        Member.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(request.getPassword())
                                .build())
                .getId();
    }

    private SavedUserInfo getNewMember(Long memberId) {
        return SavedUserInfo.builder()
                .id(memberId)
                .token(tokenGenerator.generateAuthToken(memberId, List.of(Roles.USER)))
                .isRegistered(true)
                .build();
    }

    private SavedUserInfo getExistedMember(Long memberId) {
        return SavedUserInfo.builder()
                .id(memberId)
                .token(tokenGenerator.generateAuthToken(memberId, List.of(Roles.USER)))
                .isRegistered(false)
                .build();
    }

}
