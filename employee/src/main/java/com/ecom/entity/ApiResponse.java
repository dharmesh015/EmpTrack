package com.ecom.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ApiResponse {
    private int status;
    private String code;
    private String message;
    private Object data;
    
    public ApiResponse(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}