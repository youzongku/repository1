package services.dismember.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.dismember.RankDto;
import dto.dismember.UserRankHistoryDto;
import entity.dismember.DisMember;
import entity.dismember.DisRank;
import entity.dismember.UserRankHistory;
import mapper.dismember.DisMemberMapper;
import mapper.dismember.DisRankMapper;
import mapper.dismember.UserRankHistoryMapper;
import play.Logger;
import play.libs.Json;
import services.dismember.IDisRankService;
import vo.dismember.Page;

/**
 * Created by LSL on 2016/2/5.
 */
public class DisRankService implements IDisRankService {

    @Inject
    private DisRankMapper disRankMapper;
    @Inject
    private DisMemberMapper disMemberMapper;
    @Inject
    private UserRankHistoryMapper userRankHistoryMapper;
    @Inject
    private DisMemberService disMemberService;

    @Override
    public List<DisRank> getAllRanks() {
        return disRankMapper.getAllRanks();
    }

    @Override
    public Page<RankDto> getRanksByPage(Map<String, Object> params) {
        Logger.info("getRanksByPage params--->" + Json.toJson(params).toString());
        List<RankDto> rankDtos = Lists.newArrayList();
        List<DisRank> disRanks = disRankMapper.getRanksByPage(params);
        int rows = disRankMapper.getCountByPage(params);
        if (disRanks != null && disRanks.size() > 0) {
            for (DisRank disRank : disRanks) {
                RankDto rankDto = new RankDto();
                rankDto.setId(disRank.getId());
                rankDto.setRankName(disRank.getRankName() + "级");
                rankDto.setDiscount(disRank.getDiscount() + "%");
                rankDto.setBdefault(disRank.getBdefault());
                rankDto.setUserNumber(disMemberMapper.getCountByRoleIdOrRankId(2, disRank.getId()));
                rankDto.setCreateTime(disRank.getCreateTime() != null ?
                        new DateTime(disRank.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss") : "--");
                rankDto.setCreateUser(disRank.getCreateUser() != null ? disRank.getCreateUser() : "--");
                rankDto.setUpdateTime(disRank.getUpdateTime() != null ?
                        new DateTime(disRank.getUpdateTime()).toString("yyyy-MM-dd HH:mm:ss") : "--");
                rankDto.setUpdateUser(disRank.getUpdateUser() != null ? disRank.getUpdateUser() : "--");
                rankDtos.add(rankDto);
            }
        }
        Integer currPage = (Integer) params.get("currPage");
        Integer pageSize = (Integer) params.get("pageSize");
        return new Page<RankDto>(currPage, pageSize, rows, rankDtos);
    }

    @Override
    public int addOrUpdateRank(Map<String, String> params) {
        Logger.info("addOrUpdateRank params--->" + params.toString());
        Integer sign = Strings.isNullOrEmpty(params.get("sign")) ? 0 : Integer.valueOf(params.get("sign"));
        Integer rankId = params.containsKey("rid") && !Strings.isNullOrEmpty(params.get("rid")) ? Integer.valueOf(params.get("rid")) : null;
        String user = params.get("user");
        String rankName = params.containsKey("rankname") && !Strings.isNullOrEmpty(params.get("rankname")) ? params.get("rankname") : null;
        Integer discount = params.containsKey("discount") && !Strings.isNullOrEmpty(params.get("discount")) ? Integer.valueOf(params.get("discount")) : null;
        Boolean bdefault = params.containsKey("isdefault") && !Strings.isNullOrEmpty(params.get("isdefault")) ? Boolean.valueOf(params.get("isdefault")) : null;

        //更新原先的默认等级
        if (bdefault != null && bdefault) {
            DisRank temp = disRankMapper.getDefaultRank();
            if (temp != null) {
                temp.setBdefault(false);
                disRankMapper.updateByPrimaryKeySelective(temp);
                Logger.info("addOrUpdateRank update default rank.");
            } else {
                Logger.info("addOrUpdateRank default rank isn't exist.");
            }
        }

        DisRank rank = new DisRank();
        rank.setRankName(rankName);
        rank.setDiscount(discount);

        int line = 0;
        if (sign == 1) {
            //检查新增的等级是否已存在
            Map<String, Object> checkParams = Maps.newHashMap();
            checkParams.put("rankName", rankName);
            List<DisRank> ranks = disRankMapper.getRanksByPage(checkParams);
            if (ranks != null && ranks.size() > 0) {
                return 3;
            }
            checkParams.clear();
            checkParams.put("discount", discount);
            if(ranks!=null){
            	ranks.clear();
            }
            ranks = disRankMapper.getRanksByPage(checkParams);
            if (ranks != null && ranks.size() > 0) {
                return 3;
            }

            //新增
            rank.setBdefault(bdefault);
            rank.setCreateTime(new Date());
            rank.setCreateUser(user);
            line = disRankMapper.insertSelective(rank);
            Logger.info("addOrUpdateRank add line--->" + line);
        } else if (sign == 2) {
            //更新
            rank.setId(rankId);
            rank.setBdefault(bdefault);
            rank.setUpdateTime(new Date());
            rank.setUpdateUser(user);
            line = disRankMapper.updateByPrimaryKeySelective(rank);
            Logger.info("addOrUpdateRank update line--->" + line);
        }

        return line == 1 ? 1 : 2;
    }

    @Override
    public boolean deleteRankById(Integer rankId) {
        int count = disMemberMapper.getCountByRoleIdOrRankId(2, rankId);
        int line = 0;
        if (count == 0) {
            line = disRankMapper.deleteByPrimaryKey(rankId);
        } else {
            Logger.info("current rank is using, can not delete.");
        }
        Logger.info("deleteRankById line--->" + line);
        return line == 1;
    }

    @Override
    public boolean updateUserRank(JsonNode params) {
        Logger.info("updateUserRank params--->" + params.toString());
        Integer rankId = params.get("rid").asInt();//用户账号等级ID
        boolean isCustomized = params.get("isCustomized").asBoolean();//是否定制折扣
        List<JsonNode> userIds = params.get("uids").findValues("uid");
        String operator = params.get("operator").asText();

        int lines = 0;
        if (userIds != null && userIds.size() > 0) {
            for (JsonNode userId : userIds) {
                DisMember member = disMemberMapper.selectByPrimaryKey(userId.asInt());
                UserRankHistory 	urh = new UserRankHistory();
                urh.setEmail(member.getEmail());
                urh.setOperator(operator);
                urh.setCreateTime(new Date());
                DisRank from = disRankMapper.selectByPrimaryKey(member.getRankId());
                DisRank to = disRankMapper.selectByPrimaryKey(rankId);
                String operateDesc="";//操作日志描述
                if(!from.getRankName().equals(to.getRankName())){
                	operateDesc+="等级由 ：" + from.getRankName() + " → " + to.getRankName();
                }
                if (isCustomized){
                	int customizeDiscount = params.get("customizeDiscount").asInt();
                	member.setIsCustomized(isCustomized);
                	if(customizeDiscount!=member.getCustomizeDiscount()){//是否更改折扣
                		Integer customizeDiscountFrom=member.getCustomizeDiscount();         		
                		member.setCustomizeDiscount(customizeDiscount);
                		if(customizeDiscountFrom!=null){
                			operateDesc+=" 定制折扣由："+customizeDiscountFrom+"% → "+customizeDiscount+"%"; 
                		}else{
                			operateDesc+=" 设置定制折扣为："+customizeDiscount+"%";
                		}  
                	}else{
            			operateDesc+=" 设置定制折扣为："+customizeDiscount+"%";
            		}
				}else if(member.getIsCustomized()){
                	member.setIsCustomized(isCustomized);
                	operateDesc+=" 取消折扣定制";
                }
                urh.setOperateDesc(operateDesc);
                member.setRankId(rankId);
                member.setLastUpdateDate(new Date());
                int line = 0;
                line = disMemberService.update(member);
                if (line == 1&&!urh.getOperateDesc().equals("")){
                    //记录用户等级变更历史
                    userRankHistoryMapper.insertSelective(urh);
                }
                lines += line;
            }
        }

        Logger.info("updateUserRank lines--->" + lines);
        return lines > 0;
    }

    @Override
    public List<UserRankHistoryDto> getURHsByEmail(String email) {
        List<UserRankHistoryDto> urhds = Lists.newArrayList();
        List<UserRankHistory> urhs = userRankHistoryMapper.getURHsByEmail(email);
        if (urhs != null && urhs.size() > 0) {
            for (UserRankHistory urh : urhs) {
                UserRankHistoryDto urhd = new UserRankHistoryDto();
                BeanUtils.copyProperties(urh, urhd, new String[]{"createTime"});
                urhd.setCreateTime(new DateTime(urh.getCreateTime()).toString("yyyy-MM-dd HH:mm:dd"));
                urhds.add(urhd);
            }
        }
        return urhds;
    }

	@Override
	public Map<String, Object> checkIsExistOfRank(Integer id,String rankName, Integer discount) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("rankName", rankName);
		paramMap.put("discount", discount);
		paramMap.put("id", id);
		List<DisRank> ranks = disRankMapper.getRanksByNameAndDis(paramMap);
		if (ranks.size()>0) {
			paramMap.put("isFlag", true);
			paramMap.put("rank", ranks.get(0));
			return paramMap;
		}
		
		paramMap.put("isFlag", false);
		return paramMap;
	}
	
}
