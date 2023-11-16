package com.coralocean.sqldefiner.parser.tokenHandler;

import org.apache.ibatis.parsing.TokenHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfTokenHandler implements TokenHandler {
    private static final Pattern TEST_PATTERN = Pattern.compile("(\\w+)\\s*!=\\s*null");

    private final Set<String> tokenSet = new HashSet<>();

    @Override
    public String handleToken(String content) {
        Matcher matcher = TEST_PATTERN.matcher(content);
        if (matcher.find()) {
            String token = matcher.group(1);
            tokenSet.add( token );
        }
        return null;
    }

    public Set<String> getTokenSet() {
        return tokenSet;
    }
}
