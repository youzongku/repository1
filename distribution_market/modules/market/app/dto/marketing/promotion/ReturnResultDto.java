package dto.marketing.promotion;

import java.util.List;

/**
 * 促销活动返回的实体类
 * 
 * @author ljq
 *
 */
public class ReturnResultDto {
	private Integer id;
	/** 促销活动名称 */
	private String name;
	/** 描述 */
	private String description;
	/** 开始时间 */
	private String startTime;
	/** 结束时间 */
	private String endTime;

	private List<FullActInstDto> fullActInstDtoList;

	public List<FullActInstDto> getFullActInstDtoList() {
		return fullActInstDtoList;
	}

	public void setFullActInstDtoList(List<FullActInstDto> fullActInstDtoList) {
		this.fullActInstDtoList = fullActInstDtoList;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}