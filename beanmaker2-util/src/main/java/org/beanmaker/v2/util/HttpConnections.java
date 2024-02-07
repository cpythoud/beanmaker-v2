package org.beanmaker.v2.util;

import java.net.http.HttpResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The HttpConnections class provides utility methods for working with HTTP connections.
 */
public class HttpConnections {

    /**
     * Gets the cookies from the provided HTTP response.
     *
     * @param response The HTTP response containing the cookies.
     * @return A map of cookies, where the key represents the cookie name
     *         and the value represents a list of cookie values.
     */
    public static Map<String, List<String>> getCookies(HttpResponse<String> response) {
        var headers = response.headers();
        var cookies = new HashMap<String, List<String>>();
        headers.map().forEach((key, value) -> {
            if(key.equalsIgnoreCase("Set-Cookie")) {
                for(String cookie : value) {
                    String[] singleCookie = cookie.split(";", 2)[0].split("=", 2);
                    if(singleCookie.length > 1) {
                        cookies.put(singleCookie[0], Collections.singletonList(singleCookie[1]));
                    }
                }
            }
        });
        return cookies;
    }

    /**
     * Joins a map of cookies into a single formatted cookie string.
     *
     * @param cookies A map of cookies, where the key represents the cookie name
     *                and the value represents a list of cookie values.
     * @return A formatted cookie string that can be used in HTTP requests.
     */
    public static String joinCookies(Map<String, List<String>> cookies) {
        var cookieString = new StringBuilder();
        for(var entry : cookies.entrySet()) {
            cookieString
                    .append(entry.getKey()).append("=")
                    .append(String.join(", ", entry.getValue()))
                    .append("; ");
        }
        return cookieString.toString();
    }

}
