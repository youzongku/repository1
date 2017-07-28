package mapper.marketing.promotion;


import entity.marketing.promotion.ActivityInformationLog;

public interface ActivityInformationLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ActivityInformationLog record);

    int insertSelective(ActivityInformationLog record);

    ActivityInformationLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ActivityInformationLog record);

    int updateByPrimaryKey(ActivityInformationLog record);
}