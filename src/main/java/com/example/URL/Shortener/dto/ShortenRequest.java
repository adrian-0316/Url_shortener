package com.example.URL.Shortener.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class ShortenRequest {
    @NotBlank
    private String url;
}