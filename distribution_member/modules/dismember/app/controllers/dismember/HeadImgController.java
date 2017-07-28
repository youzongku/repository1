package controllers.dismember;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.inject.Inject;
import entity.dismember.HeadImg;
import entity.dismember.enums.ImgType;
import mapper.dismember.HeadImgMapper;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.EnumUtils;
import org.mybatis.guice.transactional.Transactional;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.dismember.IDUtils;
import utils.dismember.ImgUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by luwj on 2015/11/27.
 */
public class HeadImgController extends Controller {

    private final String algorithm = "MD5";

    private final String IMG_PATH = "/headImg/";

    @Inject
    private HeadImgMapper headImgMapper;

    /**
     * 图片上传（已暂停使用）
     * @return
     */
    @Transactional
    public Result uploadHeadImg(){
        String errorCode = "0";
        String errorInfo = "图片上传成功";
        Map<String,String> map = Maps.newHashMap();
        try {
            MultipartFormData data = request().body().asMultipartFormData();
            Map<String, String[]> params = data.asFormUrlEncoded();
            Logger.debug("uploadHeadImg FormData-->" + Json.toJson(data).toString());
            FilePart filePart = data.getFile("headImg");
            String[] types = filePart.getFilename().split("\\.");
            String imgType = types[types.length - 1];
            if(EnumUtils.isValidEnum(ImgType.class,imgType)) {
                byte[] content = Files.toByteArray(filePart.getFile());
                Logger.debug(">>>>content>>>"+content.length);
                String fileType = filePart.getContentType();
                String md5 = Hex.encodeHexString(MessageDigest.getInstance(algorithm)
                        .digest(content));
                HeadImg headImg = new HeadImg();
                String imgPath = "";
                if(content.length >= 524288){//超过512KB则压缩图片大小
                    content = ImgUtils.compressImg(filePart.getFile(),true,imgType);
                }
                if(content == null){
                    errorCode = "1";
                    errorInfo = "上传失败,图片压缩异常";
                }else {
                    if(params.containsKey("imgPath")){//更新
                        imgPath = params.get("imgPath")[0];
                        headImg = headImgMapper.getInfoByPath(imgPath);
                        headImg.setBcontent(content);
                        headImg.setCcontenttype(fileType);
                        headImg.setCmd5(md5);
                        headImgMapper.updateByPrimaryKeySelective(headImg);
                    }else{//保存
                        headImg.setBcontent(content);
                        imgPath = IMG_PATH + IDUtils.buildRefundNo() + "." + imgType;
                        headImg.setCpath(imgPath);
                        Logger.debug(">>uploadHeadImg>>>imgPath>>>" + imgPath);
                        headImg.setCcontenttype(fileType);
                        headImg.setCmd5(md5);
                        headImgMapper.insertSelective(headImg);
                    }
                }
                map.put("imgPath", imgPath);
            }else {
                errorCode = "1";
                errorInfo = "上传失败,请上传jpg,jpeg,gif,png,bmp格式的图片";
            }
        }catch (NoSuchAlgorithmException e){
            errorCode = "1";
            errorInfo = "异常";
            Logger.error("NoSuchAlgorithmException",e);
        } catch (Exception e){
            errorCode = "1";
            errorInfo = "异常";
            Logger.error("Exception",e);
        }
        map.put("errorCode", errorCode);
        map.put("errorInfo", errorInfo);
        return ok(Json.toJson(map));
    }

    /**
     * 图片展示（已暂停使用）
     * @param imgPath
     * @return
     */
    public Result view(String imgPath){
        HeadImg headImg = headImgMapper.getInfoByPath(imgPath);
        if(headImg == null){
        	return notFound(imgPath + " not found");
        }
        
        return ok(headImg.getBcontent()).as(headImg.getCcontenttype());        
    }
}
