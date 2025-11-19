package org.beanmaker.v2.runtime.dbutil;

import org.dbbeans.sql.DBAccess;

import java.util.HashMap;
import java.util.Map;

public class IdMapCache {

    private final Map<Long, Long> idMap = new HashMap<>();

    public IdMapCache(String query, DBAccess dbAccess) {
        dbAccess.processQuery(
                query,
                rs -> {
                    while (rs.next()) {
                        long key = rs.getLong(1);
                        Long previousValue = idMap.putIfAbsent(key, rs.getLong(2));
                        if (previousValue != null)
                            throw new IllegalStateException("There is already an entry for ID #" + key);
                    }
                }
        );
    }

    public long getId(long id) {
        return idMap.getOrDefault(id, 0L);
    }

}
