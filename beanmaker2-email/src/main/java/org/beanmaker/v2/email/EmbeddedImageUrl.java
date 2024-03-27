package org.beanmaker.v2.email;

import java.net.MalformedURLException;
import java.net.URL;

public class EmbeddedImageUrl {

    private final URL imageUrl;
    private final String name;

    private EmbeddedImageUrl(URL imageUrl, String name) {
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public static EmbeddedImageUrl create(String imageURL, String name) {
        try {
            URL url = new URL(imageURL);
            return new EmbeddedImageUrl(url, name);
        } catch (MalformedURLException muex) {
            throw new RuntimeException(muex);
        }
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

}
