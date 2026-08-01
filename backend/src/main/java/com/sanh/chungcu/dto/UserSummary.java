package com.sanh.chungcu.dto;

import com.sanh.chungcu.entity.User;
import lombok.Getter;

@Getter
public class UserSummary {
    private final Integer id;
    private final String username;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String avatarUrl;
    private final String roleName;

    public UserSummary(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.avatarUrl = user.getAvatarUrl();
        this.roleName = user.getRole() != null ? user.getRole().getName() : null;
    }
}
