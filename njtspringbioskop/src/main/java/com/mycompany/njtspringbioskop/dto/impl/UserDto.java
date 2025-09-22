/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.dto.impl;

import com.mycompany.njtspringbioskop.entity.impl.Role;
 
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.mycompany.njtspringbioskop.dto.MyDto;

public class UserDto implements MyDto {
    private Long id;

    @NotBlank(message = "username is required")
    @Size(max = 60, message = "username max 60 chars")
    private String username;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Size(max = 120, message = "email max 120 chars")
    private String email;

    @NotNull(message = "role is required")
    private Role role;

    // passwordHash se ne izlaže kroz DTO
    public UserDto() {}
    public UserDto(Long id, String username, String email, Role role) {
        this.id = id; this.username = username; this.email = email; this.role = role;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public Role getRole() {return role;}
    public void setRole(Role role) {this.role = role;}
}