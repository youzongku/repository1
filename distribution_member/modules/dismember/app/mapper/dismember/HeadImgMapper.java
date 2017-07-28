package mapper.dismember;

import entity.dismember.HeadImg;
import org.apache.ibatis.annotations.Param;

/**
 * luwj
 */
public interface HeadImgMapper extends BaseMapper<HeadImg> {

    /**
     *通过路径获取图片信息
     * @param cpath
     * @return
     */
    HeadImg getInfoByPath(@Param("cpath")String cpath);
}