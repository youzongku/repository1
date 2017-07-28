package extensions.product;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import extensions.ModuleSupport;
import extensions.runtime.IApplication;
import mapper.contract.ChargesOprecordMapper;
import mapper.contract.ContractAttachmentMapper;
import mapper.contract.ContractCostMapper;
import mapper.contract.ContractCostTypeMapper;
import mapper.contract.ContractFeeItemLogMapper;
import mapper.contract.ContractFeeItemMapper;
import mapper.contract.ContractFeeItemRelatedSkuMapper;
import mapper.contract.ContractFeetypeMapper;
import mapper.contract.ContractMapper;
import mapper.contract.ContractOprecordMapper;
import mapper.contract.ContractQuotationsMapper;
import mapper.contract.QuotedOprecordMapper;
import mapper.contract.SequenceMapper;
import mapper.product.InventoryLockDetailMapper;
import mapper.product.InventoryLockMapper;
import mapper.product.InventoryOrderMapper;
import mapper.product.IvyOprecordMapper;
import mapper.product.IvyOptDetailMapper;
import mapper.product.store.AttrGroupMappingMapper;
import mapper.product.store.AttrMultivalueMapper;
import mapper.product.store.AttributeSetMapper;
import mapper.product.store.AttributeSetMappingMapper;
import mapper.product.store.AttributeTypeMapper;
import mapper.product.store.BbcAttributeMapper;
import mapper.product.store.BbcErpMappingMapper;
import mapper.product.store.CategoryMapper;
import mapper.product.store.CategorySetMappingMapper;
import mapper.product.store.ErpAttributeMapper;
import mapper.product.store.GroupMapper;
import mapper.product.store.ImageMapper;
import mapper.product.store.PriceMapper;
import mapper.product.store.SetAttrMultivalueMappingMapper;
import mapper.product.store.SkuAttrMappingMapper;
import mapper.product.store.SkuEntityMapper;
import mapper.product.store.SkuMapper;
import mapper.product.store.SpuCategoryMappingMapper;
import mapper.product.store.SpuMapper;
import mapper.product.store.SpuSkuMappingMapper;
import mapper.product.store.TranslateMapper;
import mybatis.MyBatisExtension;
import mybatis.MyBatisService;
import services.product.IAttributeService;
import services.product.IContractChargesService;
import services.product.IContractFeeItemMgrService;
import services.product.IContractFeetypeService;
import services.product.IContractManagerService;
import services.product.IQuotedService;
import services.product.ISequenceService;
import services.product.IUserService;
import services.product.impl.AttributeService;
import services.product.impl.ContractChargesService;
import services.product.impl.ContractFeeItemMgrService;
import services.product.impl.ContractFeetypeServiceImpl;
import services.product.impl.ContractManagerServiceImpl;
import services.product.impl.QuotedServiceImpl;
import services.product.impl.SequenceService;
import services.product.impl.UserService;

public class ProductStoreModule extends ModuleSupport implements MyBatisExtension {

    @Override
    public Module getModule(IApplication arg0) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(IAttributeService.class).to(AttributeService.class);
                bind(IContractManagerService.class).to(ContractManagerServiceImpl.class);
                bind(IQuotedService.class).to(QuotedServiceImpl.class);
                bind(IUserService.class).to(UserService.class);
                bind(ISequenceService.class).to(SequenceService.class);
                bind(IContractChargesService.class).to(ContractChargesService.class);
                bind(IContractFeetypeService.class).to(ContractFeetypeServiceImpl.class);
                bind(IContractFeeItemMgrService.class).to(ContractFeeItemMgrService.class);
            }
        };
    }

    @Override
    public void processConfiguration(MyBatisService service) {
        service.addMapperClass("b2b_product", AttrGroupMappingMapper.class);
        service.addMapperClass("b2b_product", AttributeSetMapper.class);
        service.addMapperClass("b2b_product", AttributeSetMappingMapper.class);
        service.addMapperClass("b2b_product", AttributeTypeMapper.class);
        service.addMapperClass("b2b_product", AttrMultivalueMapper.class);
        service.addMapperClass("b2b_product", BbcAttributeMapper.class);
        service.addMapperClass("b2b_product", BbcErpMappingMapper.class);
        service.addMapperClass("b2b_product", CategoryMapper.class);
        service.addMapperClass("b2b_product", CategorySetMappingMapper.class);
        service.addMapperClass("b2b_product", ErpAttributeMapper.class);
        service.addMapperClass("b2b_product", GroupMapper.class);
        service.addMapperClass("b2b_product", ImageMapper.class);
        service.addMapperClass("b2b_product", PriceMapper.class);
        service.addMapperClass("b2b_product", SetAttrMultivalueMappingMapper.class);
        service.addMapperClass("b2b_product", SkuAttrMappingMapper.class);
        service.addMapperClass("b2b_product", SkuEntityMapper.class);
        service.addMapperClass("b2b_product", SkuMapper.class);
        service.addMapperClass("b2b_product", SpuCategoryMappingMapper.class);
        service.addMapperClass("b2b_product", SpuMapper.class);
        service.addMapperClass("b2b_product", SpuSkuMappingMapper.class);
        service.addMapperClass("b2b_product", TranslateMapper.class);
        //合同管理映射
        service.addMapperClass("b2b_product", ContractMapper.class);
        service.addMapperClass("b2b_product", ContractAttachmentMapper.class);
        service.addMapperClass("b2b_product", ContractQuotationsMapper.class);
        service.addMapperClass("b2b_product", SequenceMapper.class);
        service.addMapperClass("b2b_product", ContractOprecordMapper.class);
        service.addMapperClass("b2b_product", QuotedOprecordMapper.class);
        service.addMapperClass("b2b_product", ContractCostMapper.class);
        service.addMapperClass("b2b_product", ContractCostTypeMapper.class);
        service.addMapperClass("b2b_product", ChargesOprecordMapper.class);
        //KA锁库映射
    	service.addMapperClass("b2b_product", InventoryLockMapper.class);
		service.addMapperClass("b2b_product", InventoryOrderMapper.class);
		service.addMapperClass("b2b_product", InventoryLockDetailMapper.class);
		// 合同费用项
        service.addMapperClass("b2b_product", ContractFeetypeMapper.class);
        service.addMapperClass("b2b_product", ContractFeeItemMapper.class);
        service.addMapperClass("b2b_product", ContractFeeItemRelatedSkuMapper.class);
        service.addMapperClass("b2b_product", ContractFeeItemLogMapper.class);
		//KA锁库释放库存
		service.addMapperClass("b2b_product", IvyOprecordMapper.class);
		service.addMapperClass("b2b_product", IvyOptDetailMapper.class);
    }

}
