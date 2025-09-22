/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.njtspringbioskop.dto.impl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.mycompany.njtspringbioskop.dto.MyDto;

public class GenreDto implements MyDto {
    private Long id;

    @NotBlank(message = "name is required")
    @Size(max = 60, message = "name max 60 chars")
    private String name;

    public GenreDto() {}
    public GenreDto(Long id, String name) {
        this.id = id; this.name = name;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}