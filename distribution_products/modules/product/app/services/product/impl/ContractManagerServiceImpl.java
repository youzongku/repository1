package services.product.impl;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.inject.Inject;

import dto.product.ContractDto;
import dto.product.PageResultDto;
import entity.contract.Contract;
import entity.contract.ContractAttachment;
import entity.contract.ContractOprecord;
import mapper.contract.ContractAttachmentMapper;
import mapper.contract.ContractMapper;
import mapper.contract.ContractOprecordMapper;
import play.Logger;
import play.mvc.Http.MultipartFormData.FilePart;
import services.product.IContractManagerService;
import services.product.IHttpService;
import services.product.ISequenceService;
import util.product.Constant;
import util.product.DateUtils;
import util.product.FileUtil;
import util.product.IDUtils;
import util.product.JsonCaseUtil;

/**
 * @author Administrator
 *
 */
public class ContractManagerServiceImpl implements IContractManagerService {

	private final static String CONTRACT_NO = "CONTRACT_NO";

	@Inject
	private IHttpService httpService;

	@Inject
	private ContractMapper contractMapper;

	@Inject
	private ISequenceService sequenceService;

	@Inject
	private ContractAttachmentMapper attachmentMapper;

	@Inject
	private ContractOprecordMapper copMapper;
	
	@Override
	public Map<String, Object> insert(Map<String, String[]> params, String opUser) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			Contract contract = new Contract();
			String email = JsonCaseUtil.getString(params.get("email"));
			Date start = DateUtils.string2date(JsonCaseUtil.getString(params.get("start")), "yyyy-MM-dd");
			Date end = DateUtils.string2date(JsonCaseUtil.getString(params.get("end")), "yyyy-MM-dd");
			if(start.after(end)) {
				result.put("suc", false);
				result.put("msg", "合同开始时间不能大于合同结束时间。");
				return result;
			}
			JsonNode memberResult = httpService.getMemberInfo(email);
			if (null == memberResult || !memberResult.get("suc").asBoolean()) {
				result.put("suc", false);
				result.put("msg", "未查询到用户信息。");
				return result;
			}
			JsonNode member = memberResult.get("result");
			contract.setBussinessErp(params.containsKey("bussiness") ? JsonCaseUtil.getString(params.get("bussiness"))
					: JsonCaseUtil.jsonToString(member.get("salesmanErp")));
			contract.setDistributionMode(JsonCaseUtil.jsonToInteger(member.get("distributionMode")));
			contract.setDistributionName(JsonCaseUtil.jsonToString(member.get("nickName")));
			contract.setDistributionType(JsonCaseUtil.jsonToInteger(member.get("comsumerType")));
			contract.setPhone(JsonCaseUtil.jsonToString(member.get("telphone")));
			String cno = IDUtils.getContractNoCode(sequenceService.selectNextValue(CONTRACT_NO));

			contract.setContractStart(start);
			contract.setContractEnd(end);
			contract.setContractNo(cno);
			contract.setAccount(email);
			contract.setCreateUser(opUser);
			contract.setStatus(1);// 默认状态
			Integer size = Integer.parseInt(JsonCaseUtil.getString(params.get("size")));
			result.put("suc", true);
			result.put("cno", contract.getContractNo());
//			result.put("msg", "添加合同成功，上传附件个数【" + size + "】");
			contractMapper.insertSelective(contract);
			setRcord(cno, opUser, "添加合同成功，附件个数【" + size + "】");
		} catch (Exception e) {
			Logger.error("添加合同失败", e);
			result.put("suc", false);
			result.put("msg", "添加合同失败。");
		}
		return result;
	}

	private void setRcord(String cno, String opUser, String commont) {
		ContractOprecord record = new ContractOprecord();
		record.setCno(cno);
		record.setComment(commont);
		record.setOpuser(opUser);
		record.setOpdate(new Date());
		copMapper.insertSelective(record);
	}

	public void uploadFile(List<File> files, String cno, String opUser, String fileName,String md5) {
		File target, source, folder;
		ContractAttachment attachment = null;
		ContractAttachment exsit = null;

		for (File file : files) {
			try {
				source = file;
				folder = new File(FileUtil.getUploadPath() + File.separator + cno);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				target = new File(FileUtil.getUploadPath() + File.separator + cno + File.separator + fileName);
				target.createNewFile();
				Files.copy(source, target);
				attachment = new ContractAttachment();
				attachment.setContractNo(cno);
				attachment.setFileName(fileName);
				attachment.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
				attachment.setFilePath(target.getAbsolutePath());
				attachment.setStatus(0);
				attachment.setCreateTime(new Date());
				attachment.setMdValue(md5);
				exsit = attachmentMapper.checkExsit(cno, attachment.getFileType(), attachment.getFileName());
				if(exsit == null) {
					attachmentMapper.insertSelective(attachment);
				}
			} catch (Exception e) {
				Logger.error("上传文件失败{0}", fileName, e);
			}
		}
	}

	@Override
	public PageResultDto<ContractDto> getContracts(JsonNode node) {
		Map<String, Object> param = Maps.newHashMap();
		Integer currPage = node.has("page") ? node.get("page").asInt() : null;
		Integer pageSize = node.has("rows") ? node.get("rows").asInt() : null;
		param.put("page", currPage);
		param.put("rows", pageSize);
		param.put("search", node.has("search") ? node.get("search").asText() : null);
		param.put("start", node.has("start") ? node.get("start").asText() : null);
		param.put("end", node.has("end") ? node.get("end").asText() : null);
		param.put("model", node.has("model") && StringUtils.isNotEmpty(node.get("model").asText())
				? node.get("model").asInt() : null);
		param.put("sidx", JsonCaseUtil.getString(node, "sidx", null));
		param.put("sord", JsonCaseUtil.getString(node, "sord", null));
		List<Contract> conts = contractMapper.select(param);
		List<ContractDto> contracts = transformation(conts);
		Integer count = contractMapper.selectCount(param);
		return new PageResultDto<ContractDto>(pageSize, count, currPage, contracts);
	}
	
	private List<ContractDto> transformation(List<Contract> conts) {
		List<ContractDto> contracts = Lists.newArrayList();
		ContractDto dto = null;
		for (Contract con : conts) {
			dto = new ContractDto();
			BeanUtils.copyProperties(con, dto);
			dto.setDistributionType(Constant.DISTRIBUTIONTYPE.get(con.getDistributionType()));
			dto.setDistributionMode(Constant.DISTRIBUTIONMODE.get(con.getDistributionMode()));
			dto.setModel(con.getDistributionMode());
			dto.setContractStart(DateUtils.date2string(con.getContractStart(), "yyyy-MM-dd"));
			dto.setContractEnd(DateUtils.date2string(con.getContractEnd(), "yyyy-MM-dd"));
			// dto.setCreateTime(DateUtils.date2string(con.getCreateTime()(),
			// "YYYY-MM-DD"));
			// dto.setUpdateTime(DateUtils.date2string(con.getUpdateTime()(),
			// "YYYY-MM-DD"));
			contracts.add(dto);
		}
		return contracts;
	}

	@Override
	public Map<String, Object> update(Map<String, String[]> params, List<FilePart> files, String opUser) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			String cid = JsonCaseUtil.getString(params.get("cid"));
			if (StringUtils.isEmpty(cid)) {
				result.put("suc", false);
				result.put("msg", "请检查参数是否正确。");
				return result;
			}
			String start = JsonCaseUtil.getString(params.get("start"));
			String end = JsonCaseUtil.getString(params.get("end"));
			if (StringUtils.isEmpty(start) || StringUtils.isEmpty(end)) {
				result.put("suc", false);
				result.put("msg", "合同开始或结束时间不能为空。");
				return result;
			}
			Contract contract = new Contract();
			contract.setId(Integer.parseInt(cid));
			contract.setUpdateTime(new Date());
			Date startDate = DateUtils.string2date(JsonCaseUtil.getString(params.get("start")), "yyyy-MM-dd");
			Date endDate = DateUtils.string2date(JsonCaseUtil.getString(params.get("end")), "yyyy-MM-dd");
			if(startDate.after(endDate)) {
				result.put("suc", false);
				result.put("msg", "合同开始时间不能大于合同结束时间。");
				return result;
			}
			contract.setContractStart(startDate);
			contract.setContractEnd(endDate);
			String cno = JsonCaseUtil.getString(params.get("contractNo"));
			Integer size = Integer.parseInt(JsonCaseUtil.getString(params.get("size")));
			contractMapper.updateByPrimaryKeySelective(contract);
			setRcord(cno, opUser, "更新合同信息成功，【" + start + "】【" + end + "】，文件更新个数【" + size + "】");
			result.put("suc", true);
			result.put("cno", cno);
			result.put("msg", "更新合同成功。");
		} catch (Exception e) {
			Logger.error("更新合同失败", e);
			result.put("suc", false);
			result.put("msg", "更新合同失败。");
		}
		return result;
	}

	@Override
	public Map<String, Object> deleteAttachment(JsonNode node, String opUser) {
		Map<String, Object> result = Maps.newHashMap();
		JsonNode attaIds = node.get("attaIds");
		List<Integer> aids = Lists.newArrayList();
		if (attaIds.isArray()) {
			JsonNode jsonNode = null;
			for (Iterator<JsonNode> it = attaIds.iterator(); it.hasNext();) {
				jsonNode = (JsonNode) it.next();
				aids.add(jsonNode.asInt());
			}
		} else {
			aids.add(attaIds.asInt());
		}

		if (CollectionUtils.isNotEmpty(aids)) {
			List<ContractAttachment> attas = attachmentMapper.getAttachments(aids);
			File file = null;
			List<Integer> dels = Lists.newArrayList();

			for (ContractAttachment contractAttachment : attas) {
				file = new File(contractAttachment.getFilePath());
				if (file.delete()) {
					dels.add(contractAttachment.getId());
				} else {
					Logger.info("删除失败");
				}
			}
			// 硬删除
			if (CollectionUtils.isNotEmpty(dels)) {
				attachmentMapper.deleteAttachment(dels);
				List<String> fileNames = Lists.transform(attas, att -> att.getFileName());
				setRcord(attas.get(0).getContractNo(), opUser, "删除附件" + fileNames.toString() + "");
			}
		}
		result.put("suc", true);
		result.put("msg", "删除附件成功。");
		return result;
	}

	@Override
	public List<ContractOprecord> getOprecord(String cno) {
		if (StringUtils.isEmpty(cno)) {
			return Lists.newArrayList();
		}
		List<ContractOprecord> lsit = copMapper.getRecord(cno);
		transRecord(lsit);
		return lsit;
	}

	private void transRecord(List<ContractOprecord> lsit) {
		for (ContractOprecord contractOprecord : lsit) {
			contractOprecord.setOpdateStr(DateUtils.date2string(contractOprecord.getOpdate(), "yyyy-MM-dd HH:mm:ss"));
		}
	}

	@Override
	public List<ContractAttachment> getAttachment(String cno) {
		if (StringUtils.isEmpty(cno)) {
			return Lists.newArrayList();
		}
		return attachmentMapper.getAttachmentsBycno(cno);
	}

	@Override
	public File pictureView(Integer aid) {
		ContractAttachment att = attachmentMapper.selectByPrimaryKey(aid);
		return new File(att.getFilePath());
	}

	@Override
	public File download(String cno) {
		File zip = null;
		try {
			List<ContractAttachment> attachments = attachmentMapper.getAttachmentsBycno(cno);
			String filePath = "";
			String fileName = cno;
			if (CollectionUtils.isNotEmpty(attachments)) {
				filePath = FileUtil.getUploadPath() + File.separator + cno;
				FileUtil.fileToZip(filePath, FileUtil.getUploadPath() + File.separator + "zip", fileName);
				String filename = FileUtil.getUploadPath() + File.separator + "zip" + File.separator + fileName
						+ ".zip";
				zip = new File(filename);
			}
		} catch (Exception e) {
			Logger.error("压缩失败。", e);
		}
		return zip;
	}

}
