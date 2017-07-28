package services.product;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dto.product.ContractDto;
import dto.product.PageResultDto;
import entity.contract.ContractAttachment;
import entity.contract.ContractOprecord;
import play.mvc.Http.MultipartFormData.FilePart;

/**
 * @author Administrator
 *
 */
public interface IContractManagerService {

	/**
	 * 新增合同
	 * @param node
	 * @param opUser
	 * @return
	 */
	public Map<String, Object> insert(Map<String, String[]> params, String opUser);

	void uploadFile(List<File> files, String cno, String opUser, String fileName, String md5);

	/**
	 * @param node
	 * @return
	 */
	public PageResultDto<ContractDto> getContracts(JsonNode node);

	/**
	 * 
	 * @param params
	 * @param files 
	 * @param opUser 
	 * @return
	 */
	public Map<String, Object> update(Map<String, String[]> params, List<FilePart> files, String opUser);

	/**
	 * 删除合同附件（软删除）
	 * @param node
	 * @param opUser
	 * @return
	 */
	public Map<String, Object> deleteAttachment(JsonNode node, String opUser);

	/**
	 * 获取操作记录
	 * @param cno
	 * @return
	 */
	public List<ContractOprecord> getOprecord(String cno);

	/**
	 * 获取附件列表
	 * @param cno
	 * @return
	 */
	public List<ContractAttachment> getAttachment(String cno);

	/**
	 * 图片预览
	 * @param aid
	 * @return
	 */
	public File pictureView(Integer aid);

	/**
	 * 文件打包下载
	 * @param cno
	 * @return
	 */
	public File download(String cno);

}
