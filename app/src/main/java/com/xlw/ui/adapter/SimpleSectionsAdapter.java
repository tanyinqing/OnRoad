package com.xlw.ui.adapter;

import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.xlw.model.Trip;
import com.xlw.onroad.R;
import com.xlw.presenter.TravelMemoryPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxsd on 2015/7/15.
 */
public abstract class SimpleSectionsAdapter<T> extends BaseAdapter implements AdapterView.OnItemClickListener {


    /* 为每个视图类型(view type)定义常量 */
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private LayoutInflater mLayoutInflater;
    private int mHeaderResource;        // 用于header的布局资源id
    private int mItemResource;	        // 用于item的布局资源id

    /* 所有setions的唯一集合 */
    private List<SectionItem<T>> mSectionsList;

    /* 将这些section组合到一起,使用它们初始位置作为key
    * SparseArray指的是稀疏数组(Sparse array)，所谓稀疏数组就是数组中大部分的内容值都未被使用（或都为零），在数组中仅有少部分的空间使用。
    * 因此造成内存空间的浪费，为了节省内存空间，并且不影响数组中原有的内容值，我们可以采用一种压缩的方式来表示稀疏数组的内容。
    * 总结：SparseArray是android里为<Interger,Object>这样的Hashmap而专门写的类,目的是提高效率，其核心是折半查找函数（binarySearch）。
    * */
    private SparseArray<SectionItem<T>> mKeyedSectionsList;

    // 构造函数
    public SimpleSectionsAdapter(ListView parent, int headerResId, int itemResId) {
        mLayoutInflater = LayoutInflater.from(parent.getContext());
        mHeaderResource = headerResId;
        mItemResource = itemResId;

        // 创建一个集合,使用自动控制排序key
        mSectionsList = new ArrayList<SectionItem<T>>();
        mKeyedSectionsList = new SparseArray<SectionItem<T>>();

        // 将自身设为list的单击事件处理器
        parent.setOnItemClickListener(this);
    }

    // 向list添加一个新的标题部分,或更新已经存在的一个标题部分
    public void addSection(String title, List<T> items) {
        SectionItem<T> sectionItem = new SectionItem<T>(title, items);
        // 添加section,使用相同的标题替代任何已经存在的版本
        int currentIndex = mSectionsList.indexOf(sectionItem);
        if (currentIndex >= 0) {
            mSectionsList.remove(sectionItem);
            mSectionsList.add(currentIndex, sectionItem);
        } else {
            mSectionsList.add(sectionItem);
        }
        // 排序最新的集合
        reorderSections();
        // 通知view数据已经改变了
        notifyDataSetChanged();
    }

    // 使用初始全局位置作为引用key来标记该section
    private void reorderSections() {
        mKeyedSectionsList.clear();
        int startPosition = 0;
        for (SectionItem<T> item : mSectionsList) {
            mKeyedSectionsList.put(startPosition, item);
            // 这个统计数包括header view
            startPosition += item.getCount();
        }
    }

    @Override
    public int getCount() {
        int count = 0;
        for (SectionItem<T> item : mSectionsList) {
            // 添加列表项统计数
            count += item.getCount();
        }
        return count;
    }

    @Override
    public int getViewTypeCount() {
        // 两个视图类型: headers 和 items
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderAtPosition(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public T getItem(int position) {
        return findSectionItemAtPosition(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 重写并返回false,告诉ListView有些项(header)不能被单击
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    // 重写并告诉ListView哪一个项(header)是不可单击的
    @Override
    public boolean isEnabled(int position) {
        return !isHeaderAtPosition(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                return getHeaderView(position, convertView, parent);
            case TYPE_ITEM:
                return getItemView(position, convertView, parent);
            default:
                return convertView;
        }
    }

    private View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(mHeaderResource, parent, false);
        }
        SectionItem<T> item = mKeyedSectionsList.get(position);
        TextView textView = (TextView) convertView.findViewById(R.id.trip_list_header);
        textView.setText(item.getTitle());
        return convertView;
    }

    private View getItemView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(mItemResource, parent, false);
        }
        T item = findSectionItemAtPosition(position);

        if(item instanceof TravelMemoryPresenter.TripAndPhotos){
            TravelMemoryPresenter.TripAndPhotos tripAndPhotos = (TravelMemoryPresenter.TripAndPhotos) item;
            Trip trip = tripAndPhotos.trip;
            TextView tvTopic = (TextView) convertView.findViewById(R.id.tv_trip_topic);
            tvTopic.setText(trip.getTopic());

            TextView tvStart = (TextView) convertView.findViewById(R.id.tv_trip_start);
            tvStart.setText(trip.getStart().toLocaleString());

            TextView tvDesc = (TextView) convertView.findViewById(R.id.tv_trip_desc);
            tvDesc.setText(trip.getDesc());

//            Photo photo = tripAndPhotos.photos.get(0);
            Bitmap photo = tripAndPhotos.bitmap;

            if(photo != null){
                ImageView imageView = (ImageView)convertView.findViewById(R.id.trip_photo_first);
                imageView.setImageBitmap(photo);
            }
        }

        return convertView;
    }

    /** OnItemClickListener Methods */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        T item = findSectionItemAtPosition(position);
        if (item != null) {
            onSectionItemClick(item);
        }
    }

    /**
     * 重写方法，以处理特定元素上的单击事件
     * @param item 用户单击的列表项
     */
    public abstract void onSectionItemClick(T item);


    /* 辅助方法,将Item映射到Section
    * 检查一个全局位置值是否代表一个section的头部.
    */
    private boolean isHeaderAtPosition(int position) {
        for (int i=0; i < mKeyedSectionsList.size(); i++) {
            // 如果该位置是一个key值,则说明它是一个header位置
            if (position == mKeyedSectionsList.keyAt(i)) {
                return true;
            }
        }
        return false;
    }

    // 对于给定的全局位置,返回显式的列表项
    private T findSectionItemAtPosition(int position) {
        int firstIndex, lastIndex;
        for (int i=0; i < mKeyedSectionsList.size(); i++) {
            firstIndex = mKeyedSectionsList.keyAt(i);
            lastIndex = firstIndex + mKeyedSectionsList.valueAt(i).getCount();
            if (position >= firstIndex && position < lastIndex) {
                int sectionPosition = position - firstIndex - 1;
                return mKeyedSectionsList.valueAt(i).getItem(sectionPosition);
            }
        }
        return null;
    }


}
