package mapper.product;

import entity.product.ExportSyncResult;

public interface ExportSyncResultMapper {
	public ExportSyncResult selectByOperator(String operator);
	public int updateByPrimaryKeySelective(ExportSyncResult exportResult);
	public int insertSelective(ExportSyncResult exportResult);
	public void deleteExportResultByOperator(String operator);
}
