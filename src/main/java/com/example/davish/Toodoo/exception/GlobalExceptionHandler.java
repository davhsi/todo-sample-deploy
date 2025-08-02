package com.example.davish.Toodoo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataAccessException.class)
    public ModelAndView handleDataAccessException(DataAccessException ex, HttpServletRequest request) {
        logger.error("Database error occurred: {}", ex.getMessage(), ex);
        
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", "Database error occurred. Please try again later.");
        mav.addObject("errorDetails", "The application encountered a database error while processing your request.");
        mav.addObject("status", "500");
        
        return mav;
    }

    @ExceptionHandler(SQLException.class)
    public ModelAndView handleSQLException(SQLException ex, HttpServletRequest request) {
        logger.error("SQL error occurred: {}", ex.getMessage(), ex);
        
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", "Database connection error. Please try again later.");
        mav.addObject("errorDetails", "Unable to connect to the database. Please check your connection.");
        mav.addObject("status", "500");
        
        return mav;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        logger.error("Validation error occurred: {}", ex.getMessage(), ex);
        
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", "Data validation error occurred.");
        mav.addObject("errorDetails", "The data provided does not meet the required constraints.");
        mav.addObject("status", "400");
        
        return mav;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        logger.error("Invalid argument error: {}", ex.getMessage(), ex);
        
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", "Invalid request parameters.");
        mav.addObject("errorDetails", "The request contains invalid parameters or data.");
        mav.addObject("status", "400");
        
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", "An unexpected error occurred.");
        mav.addObject("errorDetails", "The application encountered an unexpected error. Please try again later.");
        mav.addObject("status", "500");
        
        return mav;
    }
} 