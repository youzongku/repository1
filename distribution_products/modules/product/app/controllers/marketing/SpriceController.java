package controllers.marketing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.product.ISpriceService;
import util.product.HttpUtil;
import valueobjects.product.Pager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.inject.Inject;

import dto.marketing.ActivityDTO;
import entity.marketing.DisSpriceActivity;
import entity.marketing.DisSpricePoster;

/**
 * Created by LSL on 2016/7/4.
 */
public class SpriceController extends Controller {

    private static String imagePath = "";

    @Inject
    private ISpriceService spriceService;

    static {
        if (StringUtils.isEmpty(imagePath)) {
            Configuration config = Play.application().configuration().getConfig("b2bSPA");
            imagePath = config.getString("imagePath");
        }
    }

    /**
     * B2B前台特价专区活动列表
     */
    public Result findOpenedActivities() {
        return ok(spriceService.findOpenedActivities());
    }

    /**
     * B2B后台特价专区活动列表
     */
    @SuppressWarnings("rawtypes")
	public Result findActivities() {
        JsonNode params = request().body().asJson();
        if (params == null) {
            return ok(Json.toJson(new Pager(false, "参数不存在或格式有误")));
        }
        Logger.debug("findActivities    params----->" + params.toString());
        ActivityDTO dto = Json.fromJson(params, ActivityDTO.class);
        Logger.debug("findActivities    dto----->" + Json.toJson(dto));
        return ok(Json.toJson(spriceService.findActivityByCondition(dto)));
    }

    /**
     * 上传特价活动海报
     */
    public Result uploadPoster() {
        ObjectNode res = Json.newObject();
        res.put("suc", false);
        BufferedImage bi = null;
        InputStream is = null;
        try {
            MultipartFormData mFD = request().body().asMultipartFormData();
            Map<String, String[]> params = mFD.asFormUrlEncoded();
            Logger.debug("uploadPoster    params----->" + Json.toJson(params).toString());
            List<FilePart> parts = mFD.getFiles();
            if (CollectionUtils.isNotEmpty(parts)) {
                Integer actId = params.containsKey("actId") && params.get("actId").length > 0 ?
                        Integer.valueOf(params.get("actId")[0]) : null;
                String user = params.containsKey("user") && params.get("user").length > 0 ?
                        params.get("user")[0] : null;
                String fileID = params.containsKey("id") && params.get("id").length > 0 ?
                        params.get("id")[0] : null;
                String md5Key = fileID + "_md5";
                String md5 = params.containsKey(md5Key) && params.get(md5Key).length > 0 ?
                        params.get(md5Key)[0] : null;
                String fileSize = params.containsKey("size") && params.get("size").length > 0 ?
                        params.get("size")[0] : null;
                String contentType, fileName, fileMD5, msg = "";
                File origin, folder, target;
                DisSpricePoster poster;
                int line, mark = 1;
                if (CollectionUtils.isNotEmpty(parts)) {
                    FilePart part = parts.get(0);
                    contentType = part.getContentType();
                    fileName = part.getFilename();
                    Logger.debug("uploadPoster    contentType----->" + contentType);
                    Logger.debug("uploadPoster    fileName----->" + fileName);

                    origin = part.getFile();
                    is = new FileInputStream(origin);
                    fileMD5 = DigestUtils.md5Hex(is);
                    Logger.debug("uploadPoster    fileMD5----->" + fileMD5);
                    if (fileMD5 != null && fileMD5.equals(md5)) {
                        target = new File(imagePath + File.separator + fileName);
                        Logger.debug("uploadPoster    filePath----->" + target.getAbsolutePath());
                        folder = new File(imagePath);
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                        target.createNewFile();
                        Files.copy(origin, target);
                        bi = ImageIO.read(target);

                        poster = new DisSpricePoster();
                        poster.setActivityId(actId);
                        poster.setImageName(fileName);
                        poster.setImageSize(fileSize);
                        poster.setImageUrl(target.getAbsolutePath());
                        poster.setImageWidth(bi.getWidth());
                        poster.setImageHeight(bi.getHeight());
                        poster.setCreateUser(user);
                        line = spriceService.insertPoster(poster);
                        Logger.debug("uploadPoster    [insert DisSpricePoster]line----->" + line);
                    } else {
                        mark = 0;
                        msg = "上传文件MD5值校验不通过";
                    }
                }
                if (mark == 1) {
                    res.put("suc", true);
                } else {
                    res.put("msg", msg);
                }
            } else {
                res.put("msg", "无上传文件");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.error("uploadPoster    Exception----->", e);
            res.put("msg", "处理上传文件发生异常");
        } finally {
            HttpUtil.closeStream(is);
        }
        Logger.debug("uploadPoster    res----->" + res.toString());
        return ok(res);
    }

    /**
     * 保存特价活动信息
     */
    public Result saveActivity() {
        ObjectNode res = Json.newObject();
        JsonNode params = request().body().asJson();
        if (params == null) {
            res.put("suc", false);
            res.put("msg", "参数不存在或格式有误");
            Logger.debug("saveActivity    res----->" + res.toString());
            return ok(res);
        } 
        
        DisSpriceActivity activity = Json.fromJson(params, DisSpriceActivity.class);
        Logger.debug("saveActivity    activity----->" + Json.toJson(activity));
        int line;
        if (activity.getId() == null) {
            activity.setActivityStatus(1);//未开启
            line = spriceService.insertActivity(activity);
            Logger.debug("saveActivity    [insert DisSpriceActivity]line----->" + line);
        } else {
        	line = spriceService.updateActivity(activity);
            Logger.debug("saveActivity    [update DisSpriceActivity]line----->" + line);
        }
        res.put("suc", true);
        res.put("id", activity.getId());
        Logger.debug("saveActivity    res----->" + res.toString());
        return ok(res);
    }

    /**
     * 保存特价活动商品
     */
    public Result saveActProduct() {
        ObjectNode res = Json.newObject();
        JsonNode params = request().body().asJson();
        if (params == null) {
            res.put("suc", false);
            res.put("msg", "参数不存在或格式有误");
            return ok(res);
        }
        
        Logger.debug("saveActProduct    params----->" + params.toString());
        return ok(spriceService.saveActProduct(params.toString()));
    }

    /**
     * 删除特价活动指定商品
     */
    public Result delActProduct() {
        ObjectNode res = Json.newObject();
        String id = request().getQueryString("id");
        if (Strings.isNullOrEmpty(id)) {
            res.put("suc", false);
            res.put("msg", "参数不存在或格式有误");
            Logger.debug("delActProduct    res----->" + res.toString());
            return ok(res);
        }
        
        Logger.debug("delActProduct    id----->" + id);
        int line  = spriceService.deleteSpriceGoods(Integer.valueOf(id));
        Logger.debug("delActProduct    line----->" + line);
        res.put("suc", true);
        Logger.debug("delActProduct    res----->" + res.toString());
        return ok(res);
    }

    /**
     * 获取指定特价活动所有信息
     */
    public Result getActInfo() {
        ObjectNode res = Json.newObject();
        String id = request().getQueryString("id");
        Logger.debug("getActInfo    id----->" + id);
        if (Strings.isNullOrEmpty(id)) {
            res.put("suc", false);
            res.put("msg", "参数不存在或格式有误");
            return ok(res);
        }
        return ok(spriceService.getActInfo(Integer.valueOf(id)));
    }

    /**
     * 禁止指定特价活动
     */
    public Result closeActivity() {
        ObjectNode res = Json.newObject();
        String id = request().getQueryString("id");
        Logger.debug("closeActivity    id----->" + id);
        if (Strings.isNullOrEmpty(id)) {
            res.put("suc", false);
            res.put("msg", "参数不存在或格式有误");
            Logger.debug("closeActivity    res----->" + res.toString());
            return ok(res);
        }
        
        DisSpriceActivity activity = spriceService.selectActivity(Integer.valueOf(id));
        if (activity != null && activity.getActivityStatus() == 1) {
            activity.setActivityStatus(4);
            int line = spriceService.updateActivity(activity);
            Logger.debug("closeActivity    line----->" + line);
            res.put("suc", true);
            Logger.debug("closeActivity    res----->" + res.toString());
            return ok(res);
        }
        
        res.put("suc", false);
        res.put("msg", "当前活动不处于“未开始”状态，无法禁用。");
        Logger.debug("closeActivity    res----->" + res.toString());
        return ok(res);
    }

    /**
     * 开启指定特价活动
     */
    public Result openActivity() {
        ObjectNode res = Json.newObject();
        String id = request().getQueryString("id");
        Logger.debug("openActivity    id----->" + id);
        if (Strings.isNullOrEmpty(id)) {
            res.put("suc", false);
            res.put("msg", "参数不存在或格式有误");
            return ok(res);
        }
        
        return ok(spriceService.openActivity(Integer.valueOf(id)));
    }

    /**
     * 获取海报图片
     */
    public Result getActPoster() {
        String id = request().getQueryString("id");
        Logger.debug("getActPoster    id----->" + id);
        if (Strings.isNullOrEmpty(id)) {
            Logger.debug("getActPoster    ID为空或不存在");
            return ok("");
        }
        
        DisSpricePoster poster = spriceService.selectPoster(Integer.valueOf(id));
        File file = new File(poster.getImageUrl());
        return ok(file);
    }

    /**
     * 删除海报图片
     * @Author LSL on 2016-09-26 14:39:16
     */
    public Result deletePoster() {
        ObjectNode res = Json.newObject();
        String id = request().getQueryString("id");
        Logger.debug("deletePoster    id----->" + id);
        if (Strings.isNullOrEmpty(id)) {
            Logger.debug("getActPoster    ID为空或不存在");
            res.put("suc", false);
            res.put("msg", "请求参数为空或不存在");
            return ok(res);
        }
        
        return ok(spriceService.deletePoster(Integer.valueOf(id)));
    }

}
