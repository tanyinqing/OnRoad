package com.xlw.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xlw.model.Photo;
import com.xlw.model.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinliwei on 2015/7/4.
 */
public abstract class XlwBaseAdapter<T> extends BaseAdapter{

    protected Context context;
    protected LayoutInflater inflater;
    protected List<T> itemList = new ArrayList<T>();
//    protected List<Photo> photoList;
//    protected List<Trip>  tripList;

    public XlwBaseAdapter(Context context) {
        this.context = context;
//        this.tripList=tripList;
//        this.photoList=photoList;
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * 判断数据是否为空
     *
     * @return 为空返回true，不为空返回false
     */
    public boolean isEmpty() {
        return itemList.isEmpty();
    }

    /**
     * 在原有的数据上添加一批新数据
     *
     * @param itemList
     */
    public void addItems(List<T> itemList) {
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    /**
     * 在原有的数据上添加一个新数据
     *
     * @param item
     */
    public void addItems(T item) {
        this.itemList.add(item);
        notifyDataSetChanged();
    }

    /**
     * 在原有的数据上删除一个新数据
     *
     * @param item
     */
    public void removeItems(T item) {
        this.itemList.remove(item);
        notifyDataSetChanged();
    }

    /**
     * 设置为新的数据，旧数据会被清空
     *
     * @param itemList
     */
    public void setItems(List<T> itemList) {
        this.itemList.clear();
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    /**
     * 清空数据
     */
    public void clearItems() {
        itemList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    abstract public View getView(int position, View convertView, ViewGroup parent);

}
