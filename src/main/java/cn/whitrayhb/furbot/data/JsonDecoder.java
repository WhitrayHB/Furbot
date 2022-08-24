package cn.whitrayhb.furbot.data;

import cn.whitrayhb.furbot.FurbotMain;
import com.google.gson.stream.JsonReader;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.MiraiLogger;

import java.io.StringReader;
import java.util.HashMap;

public class JsonDecoder {
    private static MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static JsonDecoder INSTANCE = new JsonDecoder();

    /**
     * 解码查询毛毛的JSON
     * @param json 随机毛图的json
     * @return 一个Hashmap，包括name（名字） id（编号） 和 picID（图片序号）
     */
    public HashMap decodeQueryJson(String json){
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
                                    logger.info(map.get("name"));
                                    break;
                                case "id":
                                    map.put("id", reader.nextString());
                                    logger.info(map.get("id"));
                                    break;
                                case "picture":
                                    map.put("picID", reader.nextString());
                                    logger.info(map.get("picID"));
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
            return map;
        }catch(Exception e){
            logger.error(e);
            return null;
        }
    }

    /**
     *解码查询图片所得JSON
     * @param picJson 输入
     * @return
     */
    public String decodePicJson(String picJson){
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
                        URL = reader.nextString();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        }catch (Exception e){
            logger.error(e);
        }
        return URL;
    }
}
