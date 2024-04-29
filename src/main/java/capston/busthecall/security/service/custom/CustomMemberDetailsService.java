package capston.busthecall.security.service.custom;

import capston.busthecall.domain.Member;
import capston.busthecall.repository.MemberRepository;
import capston.busthecall.security.dto.custom.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> memberOptional = memberRepository.findByEmailAndDeletedFalse(email);

        if (memberOptional.isPresent()) {
            log.info("CustomMemberDetailsService : memberName = {}", memberOptional.get().getName());
            return new CustomMemberDetails(memberOptional.get());
        }

        throw new UsernameNotFoundException("해당 이메일이 Member 에 존재하지 않습니다.");
    }
}
