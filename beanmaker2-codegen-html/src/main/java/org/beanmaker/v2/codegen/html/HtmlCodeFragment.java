package org.beanmaker.v2.codegen.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class HtmlCodeFragment {

    private List<Tag> tags = new ArrayList<Tag>();

    public void addTag(final Tag tag) {
        tags.add(tag);
    }

    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void removeLastTag() {
        if (tags.isEmpty())
            throw new IllegalArgumentException("Code Fragment is Empty: cannot remove tag from it.");

        tags.remove(tags.size() - 1);
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        for (Tag tag: tags)
            buf.append(tag);

        return buf.toString();
    }
}
