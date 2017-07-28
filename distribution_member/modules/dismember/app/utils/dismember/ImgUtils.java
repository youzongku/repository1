package utils.dismember;

import play.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * 图片处理工具类
 * Created by luwj on 2015/11/28.
 */
public class ImgUtils {

    private static int outputWidth = 200; // 默认输出图片宽
    private static int outputHeight = 200; // 默认输出图片高

    /**
     * 图片处理
     *
     * @param file
     * @param proportion 是否等比压缩
     * @return
     */
    public static byte[] compressImg(File file, boolean proportion, String imgType) {
        byte[] tagInfo = null;
        try {
            Image img = ImageIO.read(file);
            if (img.getWidth(null) == -1) {
                Logger.debug(" can't read,retry!" + "<BR>");
            } else {
                int newWidth = 0;
                int newHeight = 0;
                // 判断是否是等比缩放
                if (proportion) {
                    // 为等比缩放计算输出的图片宽度及高度
                    double rate1 = ((double) img.getWidth(null)) / (double) outputWidth + 0.1;
                    double rate2 = ((double) img.getHeight(null)) / (double) outputHeight + 0.1;
                    // 根据缩放比率大的进行缩放控制
                    double rate = rate1 > rate2 ? rate1 : rate2;
                    newWidth = (int) (((double) img.getWidth(null)) / rate);
                    newHeight = (int) (((double) img.getHeight(null)) / rate);
                } else {
                    newWidth = outputWidth; // 输出的图片宽度
                    newHeight = outputHeight; // 输出的图片高度
                }
                BufferedImage tag = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                 /*Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的
                 优先级比速度高 生成的图片质量比较好 但速度慢 */
                img = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                tag.getGraphics().drawImage(img, 0, 0, null);

                ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                ImageIO.write(tag, imgType, imageStream);
                imageStream.flush();
                tagInfo = imageStream.toByteArray();
                Logger.debug(">>>compressImg>>>>tagInfo>>>>"+tagInfo.length);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.error("IOException", ex);
        } catch(Exception e){
            e.printStackTrace();
            Logger.error("Exception", e);
        }
        return tagInfo;
    }


    public static void main(String[] arg) {



        long start = System.currentTimeMillis();   // 开始时间
        compressImg(new File("D:\\fdaef1ea2eea7ff3d539c904.jpeg"), true,"jpeg");
        long end = System.currentTimeMillis(); // 结束时间
        System.out.println("图片压缩处理使用了: " + (end - start) + "毫秒");
    }
}
