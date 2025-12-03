package org.son.sonstudy.domain.user.application.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        String name,
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        String email,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
