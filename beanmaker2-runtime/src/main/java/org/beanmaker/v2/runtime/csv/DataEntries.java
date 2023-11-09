package org.beanmaker.v2.runtime.csv;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DataEntries implements Iterable<DataEntry> {

    private final long lineCount;
    private final List<String> headers;
    private final List<DataEntry> data;

    DataEntries(long lineCount, List<String> headers, List<DataEntry> data) {
        this.lineCount = lineCount;
        this.headers = headers;
        this.data = data;
    }

    public long getLineCount() {
        return lineCount;
    }

    public List<String> getHeaders() {
        return Collections.unmodifiableList(headers);
    }

    public List<DataEntry> getData() {
        return Collections.unmodifiableList(data);
    }

    @Override
    public Iterator<DataEntry> iterator() {
        return data.iterator();
    }

}
