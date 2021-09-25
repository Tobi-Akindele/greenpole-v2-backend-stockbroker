package com.ap.greenpole.usermodule.config;

import com.ap.greenpole.usermodule.model.GenericResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 26-May-20 04:00 AM
 */
@ControllerAdvice
public class ExceptionHandlerConfig extends DefaultResponseErrorHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public GenericResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = (ex.getBindingResult().getAllErrors().size() > 0 ?
                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage():
                "");
        return new GenericResponse<String>("01", errorMessage, null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public GenericResponse<?> handleInternalServerExceptions(ExpiredJwtException ex) {
        logger.error(ex.getMessage(), ex);
        return new GenericResponse<>("01", "The Authentication token has expired", null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public GenericResponse<?> handleInternalServerExceptions(IllegalArgumentException ex) {
        logger.error(ex.getMessage(), ex);
        return new GenericResponse<>("01", ex.getMessage(), null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public GenericResponse<?> handleInternalServerExceptions(HttpRequestMethodNotSupportedException ex) {
        return new GenericResponse<>("01", ex.getMessage(), null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public GenericResponse<?> handleInternalServerExceptions(AccessDeniedException ex) {
        logger.error(ex.getMessage(), ex);
        return new GenericResponse<>("01", "Access denied. You do not have access to this resource", null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public GenericResponse<?> handleInternalServerExceptions(MissingServletRequestParameterException ex) {
        return new GenericResponse<>("01", ex.getMessage(), null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public GenericResponse<?> handleInternalServerExceptions(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return new GenericResponse<>("01", ex.getMessage(), null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public GenericResponse<?> handleInternalServerExceptions(HttpMediaTypeNotSupportedException ex) {
        logger.error(ex.getMessage(), ex);
        return new GenericResponse<>("01", ex.getMessage(), null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public GenericResponse<?> handleInternalServerExceptions(HttpMessageNotReadableException ex) {
        logger.error(ex.getMessage(), ex);
        return new GenericResponse<>("01", "The request body is missing or is invalid", null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public GenericResponse<?> handleInternalServerExceptions(InvalidDataAccessResourceUsageException ex) {
        logger.error(ex.getMessage(), ex);
        return new GenericResponse<>("01", "Trying to access a resources which does not exist or with invalid request", null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentConversionNotSupportedException.class)
    public GenericResponse<?> handleInternalServerExceptions(MethodArgumentConversionNotSupportedException ex) {
        logger.error(ex.getMessage(), ex);
        return new GenericResponse<>("01", "Invalid parameter, check the values and try again", null);
    }

}
