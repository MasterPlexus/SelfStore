package com.masterplexus.selfstore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExSnipped {

    public String GetSearch(String input, String Search) {

        Matcher m = Pattern.compile(Search,Pattern.CASE_INSENSITIVE+Pattern.DOTALL).matcher(input);

        if (!m.find()) {
            return "";
        };

        String body = m.group(1);

        return body;
    }

}