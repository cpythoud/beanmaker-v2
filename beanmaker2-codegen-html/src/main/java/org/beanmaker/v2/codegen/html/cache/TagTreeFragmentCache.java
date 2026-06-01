package org.beanmaker.v2.codegen.html.cache;

import org.beanmaker.v2.codegen.html.Tag;

import java.util.concurrent.ExecutorService;

public abstract class TagTreeFragmentCache {

    private final ExecutorService executorService;

    private String cachedData;

    protected TagTreeFragmentCache(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public boolean dataAvailable() {
        return cachedData != null;
    }

    public void refreshData() {
        executorService.execute(() -> {
            if (cachedData == null)
                cachedData = getContainerTag().toString();
        });
    }

    public String getData() {
        if (dataAvailable())
            return cachedData;

        refreshData();
        return getPlaceHolderContainerTag().toString();
    }

    protected abstract Tag getContainerTag();

    protected abstract Tag getPlaceHolderContainerTag();
}
