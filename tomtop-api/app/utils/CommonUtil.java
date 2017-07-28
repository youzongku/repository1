package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * 公共的工具类
 *
 * Created by Administrator on 2016/3/22.
 */
public class CommonUtil {

//    private void json2List(JsonNode srcData, List<E> resList){
//        //List resList = new ArrayList();
//        if(srcData.isArray()){
//            Iterator it = srcData.iterator();
//            if(it.hasNext()){
//                JsonNode jn = (JsonNode)it.next();
//                BeanUtils.copyProperties(jn, );
//            }
//        }
//        //return resList;
//    }

}

//class JsonUtil<T>{
//    private void json2List(JsonNode srcData, List<T> resList){
//        //List resList = new ArrayList();
//        if(srcData.isArray()){
//            List<> Json.fromJson(srcData, List.class);
//            Iterator it = srcData.iterator();
//
//            if(it.hasNext()){
//                JsonNode jn = (JsonNode)it.next();
//                BeanUtils.copyProperties(jn, T);
//            }
//        }
//        //return resList;
//    }
//}
