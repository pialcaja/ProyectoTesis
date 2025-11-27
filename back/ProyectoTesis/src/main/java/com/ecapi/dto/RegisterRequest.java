package com.ecapi.dto;

import lombok.Data;

@Data
public class RegisterRequest {
	
    private String nombre;
    private String apepa;
    private String apema;
    private String dni;
    private String email;
    private String pwd;
}

