package service.timer;

public interface IActiveService {
	
	/**
	 * 执行每天12点更新活动状态
	 */
	public void execute();
	
}
