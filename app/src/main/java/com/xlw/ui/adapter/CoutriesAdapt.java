package com.xlw.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.xlw.db.PhotoDBHelper;
import com.xlw.db.TripDBHelper;
import com.xlw.model.Photo;
import com.xlw.model.Trip;
import com.xlw.onroad.R;
import com.xlw.utils.ImagesUtil;

import java.util.List;

/**
 * Created by hxsd on 2015/7/11.
 */
public class CoutriesAdapt extends BaseAdapter {
    private Context context;//Context代表.MainActivity加载的activity布局文件
//    private List<String> Coutries;
    private LayoutInflater inflater;

       private List<Trip> listtrip;
    private List<Photo> listphoto;

    public CoutriesAdapt(Context context) {
        this.context = context;
        getDataTrip();
//        Log.d("tag",listtrip.get(2).getTopic().toString());
        getDataPhoto();
        inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    //从数据库得到需要的旅行路线数据
    public void getDataTrip(){
        TripDBHelper tripDBHelper=new TripDBHelper();
       listtrip=tripDBHelper.queryTrip("where CATEGORY=?","国外游");
    }
    //从数据库得到需要的图片数据
    public void getDataPhoto(){
        PhotoDBHelper photoDBHelper=new PhotoDBHelper();
        listphoto=photoDBHelper.loadAllPhoto();
    }

          @Override
          public int getCount() {
              return listtrip.size();
          }

          @Override
          public Object getItem(int i) {
              return listtrip.get(i);
          }

          @Override
          public long getItemId(int i) {
              return i;
          }

          @Override
          public View getView(int i, View convertView, ViewGroup viewGroup) {

              if (convertView == null) {
                  convertView = inflater.inflate(R.layout.activity_item_memory, null);//inflater作用为加载视图
              }

              ImageView image_memory = (ImageView) convertView.findViewById(R.id.image_memory);
              TextView tv_topic = (TextView) convertView.findViewById(R.id.tv_topic);
              TextView tv_data = (TextView) convertView.findViewById(R.id.tv_data);
              TextView tv_content = (TextView) convertView.findViewById(R.id.tv_content);

              Log.d("tag",listtrip.get(2).getTopic());
              tv_topic.setText(listtrip.get(i).getTopic());
              tv_content.setText(listtrip.get(i).getDesc());
              tv_data.setText(listtrip.get(i).getCategory());
              String s=listphoto.get(i).getUri();
              Log.d("tag","图片路径读取成功"+s);
              Bitmap bitmap=ImagesUtil.loadBitmap(s.substring(7),100, 80);
              Log.d("tag", "图片路径读取成功" + s.substring(7));

              image_memory.setScaleType(ImageView.ScaleType.FIT_XY);
              image_memory.setAdjustViewBounds(true);
              image_memory.setImageBitmap(bitmap);
              Log.d("tag", "图片显示成功");

              return convertView;
          }


//    实现适配器的点击事件
//          @Override
//          public void onClick(View view) {
//              Log.d("tag",view.toString());
//              int position = (Integer) view.getTag();
//              //将id号通过intent传递给，下一个页面，让下一个页面在从数据库读取数据并显示出来
//              Toast.makeText(context, position + "", Toast.LENGTH_SHORT).show();
//          }

      }
