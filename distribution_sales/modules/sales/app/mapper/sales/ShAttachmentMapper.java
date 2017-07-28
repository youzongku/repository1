package mapper.sales;

import entity.sales.ShAttachment;

import java.util.List;

public interface ShAttachmentMapper {
    int insert(ShAttachment record);

    int insertSelective(ShAttachment record);

    List<ShAttachment> getShAttachmentListByShOrderId(Integer id);

    String getImg(Integer id);
}