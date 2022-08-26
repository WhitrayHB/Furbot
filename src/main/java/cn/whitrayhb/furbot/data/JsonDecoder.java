package cn.whitrayhb.furbot.data;

import cn.whitrayhb.furbot.FurbotMain;
import com.google.gson.stream.JsonReader;
import net.mamoe.mirai.utils.MiraiLogger;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonDecoder {
    private static MiraiLogger logger = FurbotMain.INSTANCE.getLogger();

    /**
     * 解码查询毛毛的JSON
     * 对应random
     * @param json 随机毛图的json
     * @return 一个Hashmap，包括name（名字） id（编号） 和 picID（图片序号）
     */
    public static HashMap<String,String> decodeQueryJson(String json){
        if(json==null){
            logger.error("获取到的JSON为空");
            return null;
        }
        HashMap<String, String> map = new HashMap<>();
        try{
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.beginObject();
            while(reader.hasNext()){
                switch (reader.nextName()){
                    case"picture":{
                        reader.beginObject();
                        while (reader.hasNext()) {
                            switch (reader.nextName()) {
                                case "name":
                                    map.put("name", reader.nextString());
                                    break;
                                case "id":
                                    map.put("id", reader.nextString());
                                    break;
                                case "picture":
                                    map.put("picID", reader.nextString());
                                    break;
                                case "format":
                                    map.put("format", reader.nextString());
                                    break;
                                case "suggest":
                                    map.put("suggest", reader.nextString());
                                    break;
                                case"time":
                                    map.put("time",reader.nextString());
                                    break;
                                case"timestamp":
                                    map.put("timeStamp",reader.nextString());
                                    break;
                                default:
                                    reader.skipValue();
                                    break;
                            }
                        }
                        reader.endObject();
                        break;
                    }
                    case"msg":
                        logger.info(reader.nextString());
                        break;
                    case"code":
                        logger.info("RespondCode is "+reader.nextInt());
                        break;
                }
            }
            reader.endObject();
            if(map.isEmpty()){
                return null;
            }
            return map;
        }catch(Exception e){
            logger.error(e);
            return null;
        }
    }

    /**
     *解码查询图片所得JSON
     * 对应pictures
     * @param picJson 输入
     * @return 图片URL
     */
    public static HashMap<String,String> decodePicJson(String picJson){
        HashMap<String,String> map = new HashMap<String,String>();
        if(picJson == null) {
            return null;
        }
        String URL = null;
        try{
            JsonReader reader = new JsonReader(new StringReader(picJson));
            reader.beginObject();
            while (reader.hasNext()){
                switch (reader.nextName()){
                    case"url":
                        map.put("url",reader.nextString());
                        break;
                    case"id":
                        map.put("id",reader.nextString());
                        break;
                    case"name":
                        map.put("name",reader.nextString());
                        break;
                    case"msg":
                        logger.info(reader.nextString());
                        break;
                    case"code":
                        logger.info("RespondCode is "+reader.nextInt());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
        }catch (Exception e){
            logger.error(e);
        }
        return map;
    }

    /**
     * 解码API信息JSON
     * @param json 输入
     * @return 一个Hashmap，包括 queryNum (图片请求数)和 picNum (总图片数)
     */
    public static HashMap<String,String> decodeAPIInfoJson(String json){
        HashMap<String, String> map = new HashMap<>();
        try {
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.beginObject();
            while (reader.hasNext()){
                switch (reader.nextName()){
                    case"total":{
                        reader.beginObject();
                        reader.skipValue();
                        map.put("queryNum",reader.nextString());
                        reader.skipValue();
                        reader.skipValue();
                        reader.endObject();
                        break;
                    }
                    case"atlas":{
                        reader.beginObject();
                        reader.skipValue();
                        map.put("picNum",reader.nextString());
                        reader.skipValue();
                        reader.skipValue();
                        reader.endObject();
                        break;
                    }
                    case "power":{
                        reader.beginObject();
                        reader.skipValue();
                        map.put("publicPicNum",reader.nextString());
                        reader.skipValue();
                        reader.skipValue();
                        reader.endObject();
                        break;
                    }
                    case "examine":{
                        reader.beginObject();
                        reader.skipValue();
                        map.put("examinePicNum",reader.nextString());
                        reader.skipValue();
                        reader.skipValue();
                        reader.endObject();
                        break;
                    }
                    case"msg":
                        logger.info(reader.nextString());
                        break;
                    case"code":
                        logger.info("RespondCode is "+reader.nextInt());
                        break;
                    default:{
                        reader.beginObject();
                        for(int i=0;i<4;i++)reader.skipValue();
                        reader.endObject();
                        break;
                    }
                }
            }
            reader.endObject();
            return map;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    /**
     * 对应pullpic和pulllist
     * @param json
     * @return
     */
    public static  ArrayList<HashMap<String, String>> decodeListPicQueryJson(String json){
        if(json==null){
            logger.error("获取到的JSON为空");
            return null;
        }
        ArrayList<HashMap<String, String>> maps = new ArrayList<>();
        try{
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.beginObject();
            while(reader.hasNext()){
                switch (reader.nextName()){
                    case"picture":{
                        reader.beginArray();
                        reader.beginObject();
                        HashMap<String, String> map = new HashMap<>();
                        while (reader.hasNext()) {
                            switch (reader.nextName()) {
                                    case "name":
                                        map.put("name", reader.nextString());
                                        break;
                                    case "id":
                                        map.put("id", reader.nextString());
                                        break;
                                    case "picture":
                                        map.put("picID", reader.nextString());
                                        break;
                                    case "format":
                                        map.put("format", reader.nextString());
                                        break;
                                    case "suggest":
                                        map.put("suggest", reader.nextString());
                                        break;
                                    case "time":
                                        map.put("time", reader.nextString());
                                        break;
                                    case "timestamp":
                                        map.put("timeStamp", reader.nextString());
                                        break;
                                    default:
                                        reader.skipValue();
                                        break;
                                }

                        }
                        maps.add(map);
                        reader.endObject();
                        reader.endArray();
                        break;
                    }
                    case"msg":
                        logger.info(reader.nextString());
                        break;
                    case"code":
                        logger.info("RespondCode is "+reader.nextInt());
                        break;
                    default:reader.skipValue();
                }
            }
            reader.endObject();
            return maps;
        }catch(Exception e){
            logger.error(e);
            return null;
        }
    }
}
