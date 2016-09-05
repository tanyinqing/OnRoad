package com.xlw.presenter;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by xinliwei on 2015/7/9.
 */
public interface ITravelMemoryView {

    // 跳转到下一个Activity
    void gotoNextActivity(Class tClass);
    // 刷新listview数据
    void updateAdapterData(LinkedHashMap<String,List<TravelMemoryPresenter.TripAndPhotos>> tripSection);
}
