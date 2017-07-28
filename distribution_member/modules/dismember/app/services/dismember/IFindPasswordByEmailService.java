package services.dismember;

import java.util.List;
import java.util.Map;

import entity.dismember.FindPasswordRecord;

public interface IFindPasswordByEmailService {
	
public boolean sendEmail(String toEmail,String key,String decode,String url);

public Map<String, Object> resetPassword(String email,Integer id,String password);

public boolean checkEmail(String email);

public Map<String,Object> checkEmailAndDecode(String email,String decode);

public List<FindPasswordRecord> getAllRecordOfToday(String email);

public FindPasswordRecord getResetRecord(Map<String, String> params);

}
