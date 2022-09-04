package cn.whitrayhb.furbot.data;

import cn.whitrayhb.furbot.FurbotMain;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.MiraiLogger;

import java.io.*;
import java.util.UUID;

public class PostData {
    private static final MiraiLogger logger = FurbotMain.INSTANCE.getLogger();
    public static String postBackup(UUID uuid, String name, String type, String suggest, Image image, CommandSender sender){
        String url = Image.queryUrl(image);
        File file = new File("./data/cn.whitrayhb.furbot/postfur/"+uuid+"/");
        File info = new File("./data/cn.whitrayhb.furbot/postfur/"+ uuid +"/info.yml");
        if(!file.exists())file.mkdirs();
        String imagePath = FetchPicture.fetchPicture(url,file.getPath());
        String[] arrImagePath = imagePath.split("/");
        String imageName = arrImagePath[arrImagePath.length-1];
        try(FileWriter fr = new FileWriter(info)) {
            fr.write("Name:"+name+"\n");
            fr.write("Type:"+type+"\n");
            fr.write("Suggest:"+suggest+"\n");
            fr.write("Sender:"+sender.getUser().getId()+"\n");
        }catch(IOException e){
            logger.warning("存储数据失败！");
        }
        //判断图片类型
        byte[] b = new byte[4];
        File pic = new File(imagePath);
        logger.info(imagePath);
        try(FileInputStream fis = new FileInputStream(imagePath)) {
            fis.read(b, 0, b.length);
            /*String imageType = bytesToHexString(b).toUpperCase();
            if (imageType.contains("FFD8FF")) {
                imagePath = imagePath + ".jpg";
                logger.info(imagePath);
                logger.info(pic.getPath());
                logger.info(String.valueOf(pic.renameTo(new File(imagePath))));
            } else if (imageType.contains("89504E47")) {
                imagePath = imagePath + ".png";
                logger.info(imagePath);
                logger.info(pic.getPath());
                logger.info(String.valueOf(pic.renameTo(new File(imagePath))));
            } else if (imageType.contains("47494638")) {
                imagePath = imagePath + ".gif";
                pic.renameTo(new File(imagePath));
            } else if (imageType.contains("424D")) {
                imagePath = imagePath + ".bpm";
                pic.renameTo(new File(imagePath));
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePath;
    }
    public static void postEnsure(UUID uuid){
        File file = new File("./data/cn.whitrayhb.furbot/postfur/"+ uuid +"/投稿已提交.ensured");
        try(FileWriter fr = new FileWriter(file)) {
            fr.write("投稿已提交");
        }catch(IOException e){
            logger.info("存储数据失败！");
        }
    }
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
