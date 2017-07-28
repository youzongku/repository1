package mapper.sales;

import entity.sales.SaleInvoice;

public interface SaleInvoiceMapper {
	
	SaleInvoice selectByPrimaryKey(Integer id);
	
    int insert(SaleInvoice record);

    int insertSelective(SaleInvoice record);

	SaleInvoice selectByOrderNo(String so);
}