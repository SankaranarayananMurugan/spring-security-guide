package com.thecodefacts.spring.security.constant;

public class SecurityConstants {
    public static final String API_LIST_COURSES = "/api/v1/courses";
    public static final String API_GET_COURSE = "/api/v1/courses/*";

    public static final String[] PUBLIC_API_LIST = new String[] {
            API_LIST_COURSES,
            API_GET_COURSE
    };

    public static class Authority {
        public static final String LIST_STUDENTS = "hasAuthority(T(com.thecodefacts.spring.security.enums.PermissionEnum).LIST_STUDENTS.name())";
        public static final String LIST_INSTRUCTORS = "hasAuthority(T(com.thecodefacts.spring.security.enums.PermissionEnum).LIST_INSTRUCTORS.name())";
        public static final String VIEW_PROFILE = "hasAuthority(T(com.thecodefacts.spring.security.enums.PermissionEnum).VIEW_PROFILE.name())";
        public static final String CREATE_COURSE = "hasAuthority(T(com.thecodefacts.spring.security.enums.PermissionEnum).CREATE_COURSE.name())";
        public static final String UPDATE_COURSE = "hasPermission(#courseId, T(com.thecodefacts.spring.security.domain.Course).getSimpleName(), T(com.thecodefacts.spring.security.enums.PermissionEnum).UPDATE_COURSE.name())";
        public static final String PLAY_COURSE = "hasPermission(#courseId, T(com.thecodefacts.spring.security.domain.Course).getSimpleName(), T(com.thecodefacts.spring.security.enums.PermissionEnum).PLAY_COURSE.name())";

    }
}
