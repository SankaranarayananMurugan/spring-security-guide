package com.thecodefacts.spring.security.constant;

public class SecurityConstants {
    public static final String API_LIST_COURSES = "/api/v1/courses";
    public static final String API_GET_COURSE = "/api/v1/courses/*";

    public static final String[] PUBLIC_API_LIST = new String[] {
            API_LIST_COURSES,
            API_GET_COURSE
    };
}
