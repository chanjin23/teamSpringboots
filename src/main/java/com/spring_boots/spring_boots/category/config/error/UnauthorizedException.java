package com.spring_boots.spring_boots.category.config.error;

// 401 Unauthorized Error
public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException(String message) {
    super(message);
  }
}