/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.mapper.impl;

 
import com.mycompany.njtspringbioskop.dto.impl.UserDto;
import com.mycompany.njtspringbioskop.entity.impl.User;
import com.mycompany.njtspringbioskop.mapper.DtoEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements DtoEntityMapper<UserDto, User> {

    @Override
    public UserDto toDto(User e) {
        if (e == null) return null;
        return new UserDto(
            e.getId(),
            e.getUsername(),
            e.getEmail(),
            e.getRole()
        );
    }

    @Override
    public User toEntity(UserDto t) {
        if (t == null) return null;
        User u = new User();
        u.setId(t.getId());
        u.setUsername(t.getUsername());
        u.setEmail(t.getEmail());
        if (t.getRole() != null) u.setRole(t.getRole());
        // passwordHash se ne mapira iz DTO-a
        return u;
    }
}