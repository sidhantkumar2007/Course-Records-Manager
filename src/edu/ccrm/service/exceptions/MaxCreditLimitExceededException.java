package edu.ccrm.service.exceptions;

/**
 * Custom exception thrown when a student exceeds the
 * maximum allowed credit limit in a semester.
 */
public class MaxCreditLimitExceededException extends Exception {
    public MaxCreditLimitExceededException(String message) {
        super(message);
    }
}
