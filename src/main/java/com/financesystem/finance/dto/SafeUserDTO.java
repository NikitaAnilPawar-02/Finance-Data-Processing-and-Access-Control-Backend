package com.financesystem.finance.dto;

import com.financesystem.finance.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SafeUserDTO {
    private Long id;
    private String name;
    private String email;
    private boolean active;
    private Role role;
}