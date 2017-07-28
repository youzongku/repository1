package enums.warehousing;

/**
 * 出入库订单类型枚举
 * 
 * @author ye_ziran
 * @since 2016年4月12日 下午3:41:53
 */
public enum OrderType {
	INIT(0){
		public String getName(){return "库存初始化";}
	},
	IN(10){
		public String getName(){return "入库";}
	},
	IN_PURCHASE(11){
		public String getName(){return "采购入库";}
	},
	IN_CHECK(12){
		public String getName(){return "盘点入库";}
	},
	/**
	 * 还原入库
	 */
	IN_RESET(13){
		public String getName(){return "还原入库";}
	},
	IN_OTHER(19){
		public String getName(){return "其他入库";}
	},
	OUT(20){
		public String getName(){return "出库";}
	},
	OUT_SALES(21){
		public String getName(){return "销售出库";}
	},
	OUT_CHECK(22){
		public String getName(){return "盘点出库";}
	},
	OUT_OTHER(29){
		public String getName(){return "其他出库";}
	};
	
	private final int value; 
    public int getValue() { 
        return value; 
    } 
    
    //构造器默认也只能是private, 从而保证构造函数只能在内部使用 
    OrderType(int value) { 
        this.value = value; 
    } 
    
    public abstract String getName();
}
