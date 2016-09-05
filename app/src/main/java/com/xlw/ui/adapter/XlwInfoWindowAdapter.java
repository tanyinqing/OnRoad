package com.xlw.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.Marker;
import com.xlw.onroad.R;

/**
 * Created by xinliwei on 2015/7/4.
 *
 * 地图中marker标记的信息窗口的自定义适配器类
 */
public class XlwInfoWindowAdapter implements AMap.InfoWindowAdapter {
    
    Context context;

    public XlwInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
//        LayoutInflater localinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View infoWindow = layoutInflater.inflate(R.layout.custom_info_window, null);
        render(marker, infoWindow);

        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(final Marker marker, View view) {
        // 填充图像部分
        ImageView infoImage = (ImageView)view.findViewById(R.id.info_image);
        infoImage.setImageResource(R.mipmap.ic_launcher);

        // 填充标题部分
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.info_title));
        if (title != null) {
            // 使用SpannableString给TextView加上特殊的文本效果
            SpannableString titleText = new SpannableString(title);
            // 第一个参数为需要设定的样式,第二个参数为开始的字符位置,第三个参数是结束的位置
            titleText.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
                    titleText.length(), 0);
            titleUi.setTextSize(14);
            titleUi.setText(titleText);

        } else {
            titleUi.setText("");
        }

        // 填充标记内容部分
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.info_snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.rgb(100,200,100)), 0,
                    snippetText.length(), 0);
            snippetUi.setTextSize(12);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("空的");
        }

        // 填充城市记内容部分
        String city = marker.getPosition().latitude + "," + marker.getPosition().longitude;
        TextView cityUi = ((TextView) view.findViewById(R.id.info_city));
        if (city != null) {
            SpannableString cityText = new SpannableString(city);
            cityText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
                    cityText.length(), 0);
            cityUi.setTextSize(10);
            cityUi.setText(cityText);
        } else {
            cityUi.setText("");
        }

        // 为按钮添加事件 - 拍照按钮
        ImageButton photoButton = (ImageButton)view.findViewById(R.id.btn_photo);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "可以转向拍照activity", Toast.LENGTH_SHORT).show();
            }
        });

        // 为按钮添加事件 - 查看详细信息按钮
        ImageButton detailButton = (ImageButton)view.findViewById(R.id.btn_detail);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"可以转向详细信息窗口查看",Toast.LENGTH_SHORT).show();
            }
        });

        // 为按钮添加事件 - 编辑"说说"按钮
        ImageButton editButton = (ImageButton)view.findViewById(R.id.btn_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"可以转向编辑窗口编辑说说",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
