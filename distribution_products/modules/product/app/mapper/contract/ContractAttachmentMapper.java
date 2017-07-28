package mapper.contract;

import java.util.List;

import entity.contract.ContractAttachment;

public interface ContractAttachmentMapper {

    int insertSelective(ContractAttachment record);

    ContractAttachment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ContractAttachment record);

	int deleteAttachment(List<Integer> aids);

	List<ContractAttachment> getAttachments(List<Integer> aids);

	List<ContractAttachment> getAttachmentsBycno(String cno);
	
	ContractAttachment checkExsit(String cno,String type,String name);

	List<ContractAttachment> checkMd5(String md5);
}