package services.dismember.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import constant.dismember.Constant;
import entity.dismember.DisMember;
import entity.dismember.DisRegisterApply;
import entity.dismember.FindPasswordRecord;
import mapper.dismember.DisMemberMapper;
import mapper.dismember.DisRegisterApplyMapper;
import mapper.dismember.FindPasswordRecordMapper;
import play.Logger;
import services.base.utils.DateFormatUtils;
import services.dismember.IDisMemberService;
import services.dismember.IFindPasswordByEmailService;

public class FindPasswordByEmailService implements IFindPasswordByEmailService {
	
	@Inject
	private FindPasswordRecordMapper findPasswordRecordMapper;
	@Inject
	private DisMemberMapper disMemberMapper;
	@Inject
    private IDisMemberService disMemberService;
	@Inject
    private DisRegisterApplyMapper disRegisterApplyMapper;
	
	@Override
	public boolean sendEmail(String toEmail,String key,String decode,String url) {
		FindPasswordRecord findPasswordRecord = new FindPasswordRecord();
		findPasswordRecord.setEmail(toEmail);
		findPasswordRecord.setKey(key);
		findPasswordRecord.setDecode(decode);
		//修改密码验证邮件有效时间为2小时
		findPasswordRecord.setTimeout(2);
	    int result = findPasswordRecordMapper.createRecord(findPasswordRecord);
	    if (result <= 0) {
			Logger.error("保存修改密码发送的邮件信息失败：tomail['"+toEmail+"']");
		}
	    
	    Map<String, Object> map = Maps.newHashMap();
	    map.put("url", url);
	    map.put("toemail", toEmail);
	    return disMemberService.sendEamil(map, Constant.EMAIL_SMTP, Constant.FIND_PASSWORD, toEmail);
	}

	@Override
	public Map<String, Object> resetPassword(String email,Integer id, String password) {
		Map<String,Object> res = new HashMap<String,Object>();
		DisMember disMember = new DisMember();
		if(id != null){
		  disMember.setId(id);
		  disMember = disMemberMapper.getMember(disMember);
		}
		if(email != null){
			disMember.setEmail(email);
			disMember = disMemberMapper.getMember(disMember);
			if(disMember.getPassWord().equals(password)){
				res.put("success",false);
				res.put("info","新密码不能与旧密码相同!");
				return res;
			}
		}
		disMember.setPassWord(password);
		int result = disMemberService.update(disMember);
		if(result >0){
			res.put("success", true);
			res.put("info", "修改密码成功!");
			return res;
		}
		
		res.put("success", false);
		res.put("info", "修改密码失败!");
		return res;
	}

	@Override
	public boolean checkEmail(String email) {
		DisMember disMember = new DisMember();
		disMember.setEmail(email);
		DisMember dis = disMemberMapper.getMember(disMember);
		if (dis != null) {
			return true;
		}
		//查询注册申请表
		Map<String, Object> map = Maps.newHashMap();
		map.put("account", email);
		map.put("status", -1);
		List<DisRegisterApply> list = disRegisterApplyMapper.getApplysByConditon(map);
		return (list!=null && list.size()>0);
	}

	@Override
	public Map<String, Object> checkEmailAndDecode(String email, String decode) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<FindPasswordRecord> recordList = getAllRecordOfToday(email);
		FindPasswordRecord record = findPasswordRecordMapper.getRecordByEmailAndKey(email, decode);
		DisMember info = null;
		//修改密码验证邮件记录是否存在且是否超出邮件有效时间
		if (record != null &&
				DateTime.now().toDate().getTime() <= (record.getCreateTime().getTime() + record.getTimeout() * 60 * 60 * 1000)) {
			DisMember disMember = new DisMember();
			disMember.setEmail(email);
			info = disMemberMapper.getMember(disMember);
			resultMap.put("isRight", true);
		}
		else{
			resultMap.put("isRight",false);//是否通过验证
		}
		resultMap.put("disMember", info);
		resultMap.put("recordCount",recordList==null?0:recordList.size());
		return resultMap;
	}

	@Override
	public List<FindPasswordRecord> getAllRecordOfToday(String email) {
		String currectDate = DateFormatUtils.getCurrentTime();
		List<FindPasswordRecord> recordList = findPasswordRecordMapper.getAllRcordForToday(DateFormatUtils.parseDate(currectDate, "yyyy-MM-dd"),email);
		return recordList;
	}

	/* 
	 * m:邮箱
	 * d:重置码
	 * (non-Javadoc)
	 * @see services.dismember.IFindPasswordByEmailService#getResetRecord(java.util.Map)
	 */
	@Override
	public FindPasswordRecord getResetRecord(Map<String, String> params) {
		return findPasswordRecordMapper.getRecordByEmailAndKey(params.get("m"), params.get("d"));
	}

}
