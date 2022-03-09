package org.beanmaker.v2.runtime;

import org.jcodegen.html.LiTag;
import org.jcodegen.html.UlTag;

import java.util.List;

public class HtmlHelper {

    public static <B extends NamedDbBean> UlTag getList(List<B> beans) {
        if (beans.isEmpty())
            throw new IllegalArgumentException("Cannot create HTML list from empty list of beans");

        final UlTag list = new UlTag();

        for (B bean: beans)
            list.child(new LiTag(bean.getDisplayName()));

        return list;
    }

    public static <B extends MultiLingualNamedDbBean> UlTag getList(
            List<B> beans,
            DbBeanLanguage dbBeanLanguage)
    {
        if (beans.isEmpty())
            throw new IllegalArgumentException("Cannot create HTML list from empty list of beans");

        final UlTag list = new UlTag();

        for (B bean: beans)
            list.child(new LiTag(bean.getDisplayName(dbBeanLanguage)));

        return list;
    }
}
