package utils;


import com.google.gson.Gson;

/**
 * Created by zgf on 2016/4/28.
 * json数据解析的工具类
 */
public class GsonUtils {
	public static <T> T jsonToBean(String result,Class<T> clazz){
		Gson gson = new Gson();
		return gson.fromJson(result, clazz);
	}

}
