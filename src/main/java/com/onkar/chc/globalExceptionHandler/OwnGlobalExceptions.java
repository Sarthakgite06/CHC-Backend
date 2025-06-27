package com.onkar.chc.globalExceptionHandler;

import com.onkar.chc.globalException.DataNotFoundException;
import com.onkar.chc.helper.Messages;
import com.onkar.chc.helper.ValidationArgumentHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class OwnGlobalExceptions {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Messages> runTimeExceptionHandler(RuntimeException e){
        return new ResponseEntity<>(Messages.builder().msg(e.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Messages> dataNotFoundExceptionHandler(DataNotFoundException dataNotFoundException){
        return new ResponseEntity<>(Messages.builder().msg(dataNotFoundException.getMessage()).build(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationArgumentHelper>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException){
        List<ObjectError> objectErrorsList =methodArgumentNotValidException.getBindingResult().getAllErrors();
        List<ValidationArgumentHelper> argumentHelpers=objectErrorsList.stream().map(a-> ValidationArgumentHelper.builder().field(a.getCode()).defaultMessage(a.getDefaultMessage()).build()).toList();
        return new ResponseEntity<>(argumentHelpers,HttpStatus.NOT_ACCEPTABLE);
    }
}
