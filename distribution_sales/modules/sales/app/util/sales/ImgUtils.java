package util.sales;

import util.sales.StringUtils;

/**
 *  替换图片路径
 * @author zbc
 *
 */
public class ImgUtils {
	
	public static String getImgUrl(String sku,String res){
		
		String tmpImgUrl = "http://static.tomtop.com.cn:8081/imaging/imaging/product/"+sku;
		String imgPUrl = "../../img/IW71-4-1a3c.jpg";
		if(!StringUtils.isBlankOrNull(res)){//如果返回值为空
			imgPUrl = res;
		}
		String imgName = imgPUrl.substring(imgPUrl.lastIndexOf("/"));
		return tmpImgUrl + imgName ;
	}
}
