package services.dismember;

import java.util.List;
import java.util.Map;

import dto.dismember.PaymentMethodDto;
import entity.dismember.DisMember;
import entity.dismember.PaymentMethod;

/**
 * @author zbc
 * 2016年12月6日 上午11:40:01
 */
public interface IPaymentMethodService {

	/**
	 * 查询所有支付方式
	 * @author zbc
	 * @since 2016年12月6日 下午4:10:15
	 */
	List<PaymentMethod> readAllMethod();

	/**
	 * 新增更新支付方式映射
	 * @author zbc
	 * @since 2016年12月6日 下午4:10:13
	 */
	Map<String,Object> create(PaymentMethodDto condit,String email);

	/**
	 * 更具分销商 查询支付方式
	 * @param member 分销商实体
	 * @param isfront 是否前台 true 为前台 false 为后台
	 * @author zbc
	 * @since 2016年12月6日 下午4:10:10
	 */
	Map<String, Object> read(Integer purpose,DisMember member, Boolean isfront);
	
}
