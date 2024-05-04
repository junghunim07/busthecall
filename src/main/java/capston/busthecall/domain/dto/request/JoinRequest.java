package capston.busthecall.domain.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class JoinRequest {


    private String name;
    private String email;
    private String password;
}
