package cn.whitrayhb.furbot.data;


import cn.whitrayhb.furbot.FurbotMain;
import net.mamoe.mirai.utils.MiraiLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchPicture {
    /**
     * 下载图片
     * @param inUrl 将要下载的图片的链接
     * @param path 图片将要保存的位置
     * @return 字符串，为带文件名的图片位置
     */
    private static MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static String fetchPicture(String inUrl,String path){
        HttpURLConnection httpUrl = null;
        byte[] bytes = new byte[4096];
        int size;
        String[] arrUrl2 = inUrl.split("\\?");
        String[] arrUrl = arrUrl2[0].split("/");
        String name = arrUrl[arrUrl.length-1];
        if(new File(path+"/"+name).exists()){
            return path+"/"+name;
        }
        try{
            URL url = new URL(inUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.setConnectTimeout(3000);
            httpUrl.setReadTimeout(60000);
            httpUrl.connect();
            File file = new File(path);
            if(!file.exists()) file.mkdirs();
            BufferedInputStream bis = new BufferedInputStream(httpUrl.getInputStream());
            FileOutputStream fos = new FileOutputStream(path+"/"+name);
            while ((size = bis.read(bytes)) != -1){
                fos.write(bytes, 0, size);
            }
            fos.close();
            bis.close();
            httpUrl.disconnect();
        } catch (Exception e){
            logger.error("图片下载失败!");
            logger.error(e);
            return null;
        }
        logger.info("图片下载成功！");
        return path+"/"+name;
    }
    public static String fetchPictureWithCookie(String inUrl,String path){
        byte[] bytes = new byte[4096];
        int size;
        String[] arrUrl2 = inUrl.split("\\?");
        String[] arrUrl = arrUrl2[0].split("/");
        String name = arrUrl[arrUrl.length-1];
        try{
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String cookie = "Token=" + PluginData.Cookie.INSTANCE.getToken() + ";" +
                            "User=" + PluginData.Cookie.INSTANCE.getUser() + ";" +
                            "PHPSESSID=" + PluginData.Cookie.INSTANCE.getPhpsessionid();
            Request request = new Request.Builder()
                    .url(inUrl)
                    .addHeader("Cookie",cookie)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            File file = new File(path);
            if(!file.exists()) file.mkdirs();
            InputStream is = response.body().byteStream();
            FileOutputStream fos = new FileOutputStream(path+"/"+name);
            while ((size = is.read(bytes)) != -1){
                fos.write(bytes, 0, size);
            }
            fos.close();
            is.close();
        } catch (Exception e){
            logger.error("图片下载失败!");
            logger.error(e);
            return null;
        }
        logger.info("图片下载成功！");
        return path+"/"+name;
    }
}
