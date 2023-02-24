package org.beanmaker.v2.runtime;

import org.jcodegen.html.LiTag;
import org.jcodegen.html.UlTag;

import java.util.List;

public class HtmlHelper {

    public static <B extends NamedDbBean> UlTag getList(List<B> beans) {
        if (beans.isEmpty())
            throw new IllegalArgumentException("Cannot create HTML list from empty list of beans");

        var list = new UlTag();

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

        var list = new UlTag();

        for (B bean: beans)
            list.child(new LiTag(bean.getDisplayName(dbBeanLanguage)));

        return list;
    }

    public static int nullToZeroConversion(Integer intValue) {
        if (intValue == null)
            return 0;

        return intValue;
    }

    public static long nullToZeroConversion(Long longValue) {
        if (longValue == null)
            return 0L;

        return longValue;
    }

}
