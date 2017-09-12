package com.java.no16.protos;

/**
 * Exception class.
 */

public class NewsException extends Exception {
    public static final String CONVERT_FROM_STRING_TO_JSON_ERROR = "ConversionException";
    public static final String CONVERT_FROM_STRING_TO_JSON_MESSAGE = "Failed to convert news list string to json format in %s method, %s service.";
    public static final String GET_IMAGE_ERROR = "ImageException";
    public static final String GET_IMAGE_MESSAGE = "Failed to get missed image from Internet by keyword %s.";
    public static final String FORCE_EXIT_ERROR = "AppExitException";
    public static final String FORCE_EXIT_MESSAGE = "The app is forced to exit.";
    public static final String FAIL_IN_STORE_NEWS = "SqlException";
    public static final String NEWS_ERROR = "NewsException";
    public static final String INDEX_OUT_OF_BOUND_MESSAGE = "The indexs in request message are out of bound.";
    public static final String NEWS_ID_NOT_EXIST_MESSAGE = "The news_id %s doesn't exist in database.";

    public static final String EXIT_INFO = "ExitInfo";
    public static final String EXIT_MESSAGE = "The app exits formally.";

    private String errorCode;

    public NewsException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
