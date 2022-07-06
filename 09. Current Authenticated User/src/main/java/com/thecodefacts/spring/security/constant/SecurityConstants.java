package com.thecodefacts.spring.security.constant;

public class SecurityConstants {
    /* Course API URLs */
    public static final String API_CREATE_COURSES = "/api/v1/courses";
    public static final String API_LIST_COURSES = "/api/v1/courses";
    public static final String API_GET_COURSE = "/api/v1/courses/*";

    /* User API URLs */
    public static final String API_LIST_STUDENTS = "/api/v1/users/students";
    public static final String API_LIST_INSTRUCTORS = "/api/v1/users/instructors";

    public static final String[] PUBLIC_API_LIST = new String[] {
            API_LIST_COURSES,
            API_GET_COURSE
    };
}
