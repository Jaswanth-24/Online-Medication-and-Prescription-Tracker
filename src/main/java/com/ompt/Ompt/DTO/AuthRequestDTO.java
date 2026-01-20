package com.ompt.Ompt.DTO;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthRequestDTO {

    private String email;
    private String password;
    private String newPassword;
    private String token;

}
