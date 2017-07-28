package mapper.dismember;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.dismember.FindPasswordRecord;

public interface FindPasswordRecordMapper extends BaseMapper<FindPasswordRecord>{
	
    int createRecord(@Param("findPasswordRecord") FindPasswordRecord findPasswordRecord);

    FindPasswordRecord getRecordByEmailAndKey(@Param("email")String email,@Param("decode")String decode);

    List<FindPasswordRecord> getAllRcordForToday(@Param("now")Date now,@Param("email")String email);

}
