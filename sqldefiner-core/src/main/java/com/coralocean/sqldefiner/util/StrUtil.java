package com.coralocean.sqldefiner.util;

public final class StrUtil {
    public static boolean isBlank(String str) {
        if (null == str || str.length() == 0) return true;
        StringBuilder builder = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c != ' ') {
                builder.append(c);
            }
        }
        return builder.toString().isEmpty();
    }
}
