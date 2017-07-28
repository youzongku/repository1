package services.purchase;

import java.util.List;

import dto.purchase.PurchaseOrderInputDto;
import entity.purchase.PurchaseOrderInput;
import entity.purchase.PurchaseOrderInputGift;
import entity.purchase.PurchaseOrderInputPro;

/**
 * 手动录入正价商品和赠品service接口
 * @author huangjc
 * @date 2016年8月30日
 */
public interface IPurchaseOrderTypeInService {
	
	boolean addOrUpdateMain(PurchaseOrderInput input);
	
	List<PurchaseOrderInputPro> getCheckedInputPros(int inputId);
	
	/**
	 * 修改正价商品数量
	 * @param proId
	 * @param qty
	 * @return
	 */
	public int updateProQty(int proId,int qty);
	
	/**
	 * 修改赠品数量
	 * @param giftId
	 * @param qty
	 * @return
	 */
	public int updateGiftQty(int giftId,int qty);
	
	/**
	 * 添加正价商品
	 * @param inputId 主表id
	 * @param needExpirationDate 是否需要到期日期
	 * @param inputProList 正价商品
	 */
	public void addProducts(Integer inputId, boolean needExpirationDate, List<PurchaseOrderInputPro> inputProList);
	/**
	 * 删除正价商品
	 * @param ids 商品ids
	 */
	public void deleteProducts(String ids);
	
	/**
	 * 添加赠品
	 * @param inputId 主表id
	 * @param needExpirationDate 是否需要到期日期
	 * @param inputGiftList 赠品
	 */
	public void addGifts(Integer inputId, boolean needExpirationDate, List<PurchaseOrderInputGift> inputGiftList);
	/**
	 * 删除赠品
	 * @param ids 赠品ids
	 */
	public void deleteGifts(String ids);
	public void deleteAllProsAndGiftsByInputId(int inputId);
	/**
	 * 获取正价商品与赠品的对应关系
	 * @param inputUser 录入人
	 * @return
	 */
	public PurchaseOrderInput getPurchaseOrderInput(String inputUser);
	public PurchaseOrderInput getPurchaseOrderInput(int inputId);
	/**
	 * 获取全部的录入数据，不分组
	 * @param inputId
	 * @return
	 */
	public PurchaseOrderInputDto getPurchaseOrderInputDto(int inputId);
	/**
	 * 获取全部的录入数据，不分组
	 * @param inputUser 登录人
	 * @return
	 */
	public PurchaseOrderInputDto getPurchaseOrderInputDto(String inputUser);
	
	void updateChecked(int inputId, String proIds);
	
}
