package com.java.no16.protos;

/**
 * Exception class.
 */

public class NewsException extends Exception {
    public static final String CONVERT_FROM_STRING_TO_JSON_ERROR = "ConversionError";
    public static final String CONVERT_FROM_STRING_TO_JSON_MESSAGE = "Failed to convert news list string to json format in %s method, %s service.";
    public static final String GET_IMAGE_ERROR = "ImageError";
    public static final String GET_IMAGE_MESSAGE = "Failed to get missed image from Internet by keyword %s.";
}
