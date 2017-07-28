package entity.marketing.promotion;

import java.util.Objects;

/**
 * 促销类型与条件/优惠关系
 * 
 * @author huangjc
 * @date 2016年7月22日
 */
public class RelPromotionTypePrivilegeCondition {
	private Integer id;
	/** 条件id，当condtId有值时，pvlgId为空 */
	private Integer condtId;
	/** 优惠id，当pvlgId有值时，condtId为空 */
	private Integer pvlgId;
	/** 促销类型id */
	private Integer proTypeId;

	public RelPromotionTypePrivilegeCondition() {
	}

	private RelPromotionTypePrivilegeCondition(Builder builder) {
		this.proTypeId = builder.proTypeId;
		this.condtId = builder.condtId;
		this.pvlgId = builder.pvlgId;
	}

	public static class Builder {
		/** 条件id，当condtId有值时，pvlgId为空 */
		private Integer condtId;
		/** 优惠id，当pvlgId有值时，condtId为空 */
		private Integer pvlgId;
		/** 促销类型id */
		private Integer proTypeId;

		public Builder setCondtId(Integer condtId) {
			if(pvlgId!=null){
				throw new RuntimeException("pvlgId must be null");
			}
			this.condtId = condtId;
			return this;
		}

		public Builder setPvlgId(Integer pvlgId) {
			if(condtId!=null){
				throw new RuntimeException("condtId must be null");
			}
			this.pvlgId = pvlgId;
			return this;
		}

		public Builder setProTypeId(Integer proTypeId) {
			this.proTypeId = proTypeId;
			return this;
		}

		public RelPromotionTypePrivilegeCondition buildTypeAndCondtRel() {
			RelPromotionTypePrivilegeCondition rel = new RelPromotionTypePrivilegeCondition(this);
			return rel;
		}

		public RelPromotionTypePrivilegeCondition buildTypeAndPvlgRel() {
			RelPromotionTypePrivilegeCondition rel = new RelPromotionTypePrivilegeCondition(this);
			return rel;
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCondtId() {
		return condtId;
	}

	public void setCondtId(Integer condtId) {
		this.condtId = condtId;
	}

	public Integer getPvlgId() {
		return pvlgId;
	}

	public void setPvlgId(Integer pvlgId) {
		this.pvlgId = pvlgId;
	}

	public Integer getProTypeId() {
		return proTypeId;
	}

	public void setProTypeId(Integer proTypeId) {
		this.proTypeId = proTypeId;
	}

	@Override
	public String toString() {
		return "RelPromotionTypePrivilegeCondition [id=" + id + ", condtId="
				+ condtId + ", pvlgId=" + pvlgId + ", proTypeId=" + proTypeId
				+ "]";
	}

	/**
	 * 判断包含的是条件还是优惠
	 * 
	 * @return
	 */
	public boolean isCondition() {
		return Objects.nonNull(condtId);
	}
}