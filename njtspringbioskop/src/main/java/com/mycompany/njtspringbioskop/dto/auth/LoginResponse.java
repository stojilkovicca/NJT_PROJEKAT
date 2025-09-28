/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.dto.auth;
import java.io.Serializable;
public class LoginResponse {
    private String token;
    private String role;
    private String username;
    private Long userId;  

    public LoginResponse(String token, String role, String username, Long userId) {
        this.token = token;
        this.role = role;
        this.username = username;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getUsername() { return username; }
    public Long getUserId() { return userId; }
}
