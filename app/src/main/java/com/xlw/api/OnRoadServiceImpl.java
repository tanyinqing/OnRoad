package com.xlw.api;

import android.util.Log;

import com.tandong.sa.json.Gson;
import com.tandong.sa.json.JsonParseException;
import com.tandong.sa.json.reflect.TypeToken;
import com.xlw.exception.AndroidOnRoadException;
import com.xlw.model.Trip;
import com.xlw.utils.HttpUtil;

import org.apache.http.auth.AuthenticationException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by xinliwei on 2015/7/8.
 */
public class OnRoadServiceImpl {

    private static final String TAG = OnRoadServiceImpl.class.getCanonicalName();

    public static List<Trip> fetchTrips() throws AuthenticationException,
            JsonParseException, IOException, AndroidOnRoadException {
        Log.d(TAG, "抓取服务器端数据...");
        String url = AndroidOnRoadUrlFactory.getInstance().getTripUrl();
//        String response = HttpHelper.getHttpResponseAsString(url, null);
        String response = HttpUtil.getHttpResponseAsString(url);
        Gson gson = new Gson();
        List<Trip> lists = gson.fromJson(response, getToken());

        return lists;
    }

    private static Type getToken() {
        return new TypeToken<List<Trip>>() {
        }.getType();
    }

    public static Trip createTrip(Trip trip)
            throws AuthenticationException, JsonParseException, IOException,
            AndroidOnRoadException {
        Log.d(TAG, "Creating Trip list " + trip);
        String urlFmt = AndroidOnRoadUrlFactory.getInstance().getTripAddUrlFmt();
        String url = String.format(urlFmt, "这里应该是Trip对象数据");
//        String response = HttpHelper.getHttpResponseAsString(url, null);
        String response = HttpUtil.getHttpResponseAsString(url);

        Gson gson = new Gson();
        List<Trip> lists = gson.fromJson(response, getToken());

        if (lists.size() != 1) {
            throw new AndroidOnRoadException("创建Trip对象错误.");
        }

        return lists.get(0);
    }

    public static void deleteTrip(long id) throws AuthenticationException, AndroidOnRoadException {
        Log.d(TAG, "Deleting Trip with id " + id);
        String urlFmt = AndroidOnRoadUrlFactory.getInstance().getTripDeleteUrlFmt();
        String url = String.format(urlFmt, id);
//        HttpHelper.getHttpResponseAsString(url, null);
        HttpUtil.getHttpResponseAsString(url);     // ???????????????????????????????????????????????
    }
}
