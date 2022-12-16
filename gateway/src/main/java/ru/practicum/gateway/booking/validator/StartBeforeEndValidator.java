package ru.practicum.gateway.booking.validator;


import ru.practicum.gateway.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDto> {

    @Override
    public boolean isValid(BookingDto dto, ConstraintValidatorContext constraintValidatorContext) {
        @FutureOrPresent
        LocalDateTime startTime = dto.getStart();
        @Future
        LocalDateTime endTime = dto.getEnd();

        return startTime.isBefore(endTime);
    }
}