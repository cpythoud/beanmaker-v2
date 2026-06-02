package org.beanmaker.v2.codegen.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class HtmlCodeFragment {

    private final List<Tag<?>> tags = new ArrayList<>();

    public void addTag(Tag<?> tag) {
        tags.add(tag);
    }

    public List<Tag<?>> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void removeLastTag() {
        if (tags.isEmpty())
            throw new IllegalArgumentException("Code Fragment is Empty: cannot remove tag from it.");

        tags.removeLast();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        for (var tag: tags)
            buf.append(tag);

        return buf.toString();
    }

}
