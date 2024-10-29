package com.fastcampus.healthcare.middleware;

import com.fastcampus.healthcare.common.exception.BadRequestException;
import com.fastcampus.healthcare.common.exception.EmailAlreadyExistsException;
import com.fastcampus.healthcare.common.exception.InvalidPasswordException;
import com.fastcampus.healthcare.common.exception.ResourceNotFoundException;
import com.fastcampus.healthcare.common.exception.RoleNotFoundException;
import com.fastcampus.healthcare.common.exception.UserNotFoundException;
import com.fastcampus.healthcare.common.exception.UsernameAlreadyExistsException;
import com.fastcampus.healthcare.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(
      {
          ResourceNotFoundException.class,
          UserNotFoundException.class,
          RoleNotFoundException.class
      }
  )
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public @ResponseBody ErrorResponse handleResourceNotFoundException(
      HttpServletRequest request, RuntimeException ex
  ) {
    return ErrorResponse.builder()
        .code(HttpStatus.NOT_FOUND.value())
        .message(ex.getMessage())
        .timestamp(LocalDateTime.now())
        .build();
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public @ResponseBody ErrorResponse handleBadRequestException(
      HttpServletRequest request, BadRequestException ex
  ) {
    return ErrorResponse.builder()
        .code(HttpStatus.BAD_REQUEST.value())
        .message(ex.getMessage())
        .timestamp(LocalDateTime.now())
        .build();
  }


  @ExceptionHandler(InvalidPasswordException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public @ResponseBody ErrorResponse handleUnauthorizedException(
      HttpServletRequest request, Exception ex
  ) {
    return ErrorResponse.builder()
        .code(HttpStatus.UNAUTHORIZED.value())
        .message(ex.getMessage())
        .timestamp(LocalDateTime.now())
        .build();
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public @ResponseBody ErrorResponse handleGenericException(
      HttpServletRequest request, Exception ex
  ) {
    log.error("Telah terjadi error pada endpoint {}. status code: {}. error_message: {} ",
        request.getRequestURI(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        ex.getMessage()
        );

    return ErrorResponse.builder()
        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(ex.getMessage())
        .timestamp(LocalDateTime.now())
        .build();
  }


  @ExceptionHandler({
      UsernameAlreadyExistsException.class,
      EmailAlreadyExistsException.class
  })
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public @ResponseBody ErrorResponse handleConflictException(
      HttpServletRequest request, Exception ex
  ) {
    return ErrorResponse.builder()
        .code(HttpStatus.UNAUTHORIZED.value())
        .message(ex.getMessage())
        .timestamp(LocalDateTime.now())
        .build();
  }
}
