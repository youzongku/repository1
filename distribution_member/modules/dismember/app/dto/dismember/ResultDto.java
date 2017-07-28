package dto.dismember;

/**
 * 公共返回实体
 * 
 * @author zbc 2017年2月17日 下午6:15:45
 */
public class ResultDto<T> extends PermissionRes<T> {

	private static final long serialVersionUID = -5997682521324174187L;

	/**
	 * 是否成功标识，兼容旧版本
	 */
	/*
	 * private Boolean success;
	 * 
	 *//**
		 * 是否成功标识，兼容旧版本
		 *//*
		 * private Boolean suc;
		 */

	/**
	 * @param code
	 * @param msg
	 * @param obj
	 */
	public ResultDto(Integer code, String msg, T obj) {
		setCode(code);
		setMsg(msg);
		setObj(obj);
		/*
		 * this.success = code == 100; this.suc = code == 100;
		 */
	}

	public ResultDto() {

	}

	public static <T> ResultDto<T> newIns() {
		return new ResultDto<>();
	}

	public ResultDto<T> suc(Boolean flag) {
		setCode(flag ? 100 : 103);
		return this;
	}

	public ResultDto<T> msg(String msg) {
		setMsg(msg);
		return this;
	}

	public ResultDto<T> obj(T data) {
		setObj(data);
		return this;
	}

	public ResultDto(boolean flag, String msg) {
		/*
		 * this.success = flag; this.suc = flag;
		 */
		setCode(flag ? 100 : 103);
		setMsg(msg);
	}

	/*
	 * public Boolean getSuccess() { return success; }
	 * 
	 * public void setSuccess(Boolean success) { this.success = success; }
	 * 
	 * public Boolean getSuc() { return suc; }
	 * 
	 * public void setSuc(Boolean suc) { this.suc = suc; }
	 */
}
