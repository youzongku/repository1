package handlers.product_inventory;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import entity.product_inventory.ProductInventoryOrderLock;
import events.product_inventory.ProductReleaseCloudLockEvent;
import mapper.product_inventory.ProductInventoryOrderLockMapper;

import org.joda.time.*;

import play.Configuration;
import play.Logger;
import play.Play;
import utils.inventory.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * @author longhuashen
 * @since 2016/12/29
 */
public class ProductReleaseCloudLockHandler {

    @Inject
    private ProductInventoryOrderLockMapper productInventoryOrderLockMapper;

    @Subscribe
    public void realeaseProductCloudInventoryLockJob(ProductReleaseCloudLockEvent event) {
        //释放云仓锁定
        ProductInventoryOrderLock productInventoryOrderLockParam = new ProductInventoryOrderLock();
        productInventoryOrderLockParam.setIsEffective((short) 1);//获取临时锁
        List<ProductInventoryOrderLock> productInventoryOrderLockList = productInventoryOrderLockMapper.selectInventoryLockListByParams(productInventoryOrderLockParam);

        if (productInventoryOrderLockList != null && productInventoryOrderLockList.size() > 0) {
            for (ProductInventoryOrderLock productInventoryOrderLock : productInventoryOrderLockList) {
                Configuration config = Play.application().configuration()
                        .getConfig("cloudLockInventory");
                String cloudLockInventoryEffectiveMinute = config.getString("effectiveMinute");
                DateTime dateTimeNow = new DateTime(new Date());
                int differMinutes = Minutes.minutesBetween(new DateTime(productInventoryOrderLock.getLastCheckTime()), dateTimeNow).getMinutes();//相差分钟
                if (differMinutes > Integer.parseInt(cloudLockInventoryEffectiveMinute)) {
                    Logger.info("====================单号[{}]，sku[{}],于[{}]释放锁定的云仓====================",productInventoryOrderLock.getOrderNo(),productInventoryOrderLock.getSku(), DateUtils.date2string(new Date(),DateUtils.FORMAT_FULL_DATETIME));
                    productInventoryOrderLock.setIsEffective((short) 0);
                    productInventoryOrderLock.setUpdateTime(new Date());
                    productInventoryOrderLockMapper.updateByPrimaryKeySelective(productInventoryOrderLock);
                }
            }
        }
    }
}
