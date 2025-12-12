package com.app.movie.DTO;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private String name;
    private String email;
    private String password;
}
