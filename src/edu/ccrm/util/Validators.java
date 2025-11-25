package edu.ccrm.util;

public class Validators {
    public static boolean isEmail(String s) {
        return s != null && s.contains("@") && s.contains(".");
    }
}
