package org.beanmaker.v2.email;

import java.net.MalformedURLException;
import java.net.URL;

public class EmbeddedImage {

    private final URL imageUrl;
    private final String name;

    private EmbeddedImage(URL imageUrl, String name) {
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public EmbeddedImage create(String imageURL, String name) {
        try {
            URL url = new URL(imageURL);
            return new EmbeddedImage(url, name);
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
