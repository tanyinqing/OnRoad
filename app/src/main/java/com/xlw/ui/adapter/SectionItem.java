package com.xlw.ui.adapter;

import java.util.List;

/**
 * Created by hxsd on 2015/7/15.
 */

public class SectionItem<T> {
    //将两个布局和成一个适配器
    private String mTitle;
    private List<T> mItems;
    public SectionItem(String title, List<T> items) {
        if (title == null) {
            title = "";
        }
        mTitle = title;
        mItems = items;
    }

    public String getTitle() {
        return mTitle;
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    public int getCount() {
        // 包括一个额外的item,用于该部分section的标题
        return (mItems == null ? 1 : 1 + mItems.size());
    }

    @Override
    public boolean equals(Object object) {
        // 如果两个section有相同的标题,则这两个section是相等的
        if (object != null && object instanceof SectionItem) {
            return ((SectionItem) object).getTitle().equals(mTitle);
        }
        return false;
    }
}
