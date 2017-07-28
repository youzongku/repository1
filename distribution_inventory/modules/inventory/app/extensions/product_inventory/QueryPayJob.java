package extensions.product_inventory;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * @author longhuashen
 * @since 2016/12/12
 */
public class QueryPayJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
System.out.println("=====================QueryPayJob========================" + new Date());
    }
}
