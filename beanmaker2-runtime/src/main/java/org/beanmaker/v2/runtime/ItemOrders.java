package org.beanmaker.v2.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemOrders {

    public static <B extends DbBeanWithItemOrder> List<B> getRange(
            long firstItemOrder,
            long lastItemOrder,
            List<B> itemInventory)
    {
        boolean reversed = firstItemOrder > lastItemOrder;

        List<B> items = new ArrayList<B>(itemInventory);
        if (reversed)
            Collections.reverse(items);

        long currentItemOrder;
        if (reversed)
            currentItemOrder = Long.MAX_VALUE;
        else
            currentItemOrder = 0;

        List<B> itemsInRange = new ArrayList<B>();
        for (B item: items) {
            if (reversed) {
                if (currentItemOrder <= item.getItemOrder())
                    throw new IllegalArgumentException("Item list is not properly ordered");
            } else {
                if (currentItemOrder >= item.getItemOrder())
                    throw new IllegalArgumentException("Item list is not properly ordered");
            }
            currentItemOrder = item.getItemOrder();

            if (currentItemOrder >= firstItemOrder && currentItemOrder <= lastItemOrder)
                itemsInRange.add(item);
        }

        return itemsInRange;
    }

    public static <B extends DbBeanWithItemOrder> List<B> getRange(
            B firstItem,
            B lastItem,
            List<B> itemInventory)
    {
        return getRange(firstItem.getItemOrder(), lastItem.getItemOrder(), itemInventory);
    }

    public static <B extends DbBeanWithItemOrder> List<B> globalSort(Collection<B> beans) {
        var sortedBeans = new ArrayList<>(beans);
        if (!sortedBeans.isEmpty() && sortedBeans.get(0).isItemOrderLinkedToSecondaryField())
            throw new IllegalArgumentException("Beans with a secondary field cannot be sorted by this function");
        sortedBeans.sort(Comparator.comparingLong(DbBeanWithItemOrder::getItemOrder));
        return sortedBeans;
    }

}
