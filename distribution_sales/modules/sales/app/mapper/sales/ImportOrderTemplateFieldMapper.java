package mapper.sales;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.sales.ImportOrderTemplateField;

public interface ImportOrderTemplateFieldMapper{

    List<ImportOrderTemplateField> selectByType(@Param("type")Integer type);
}
