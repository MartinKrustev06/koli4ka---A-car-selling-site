package com.koli4ka.app.web.dtos;

import com.koli4ka.app.user.model.UserRole;
import lombok.Data;

import java.util.UUID;

@Data
public class UserRoleSwitch {
    private UUID id;
    private UserRole role;
}
