package com.greenblat.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class JsonUtil {

    public static String getJsonObject(BufferedReader reader) throws IOException {
        String line;
        StringBuilder json = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            json.append(line).append("\n");
        }
        return json.toString();
    }
}
