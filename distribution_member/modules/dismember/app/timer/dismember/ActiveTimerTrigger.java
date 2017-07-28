package timer.dismember;

import events.dismember.ApkApplyEvent;
import events.dismember.WithdrawApplyEvent;

import org.apache.camel.builder.RouteBuilder;

import com.google.common.eventbus.EventBus;

import events.dismember.AccountPerioEvent;
import events.dismember.AccountPeriodTextReminderEvent;
import events.dismember.ActiveStateEvent;
import extensions.InjectorInstance;

/**
 * 定时执行更新活动状态
 * @author Administrator
 *
 */
public class ActiveTimerTrigger extends RouteBuilder {
	
	    @Override
	    public void configure() throws Exception {
	        from("quartz2://execute?cron=59+59+23+*+*+?").bean(this, "triggerEvent");
	        from("quartz2://sendWithdrawApplyToMsite?cron=0+0+*/1+*+*+?").bean(this, "withdrawApplyEvent");
	        from("quartz2://apkApplyCheck?cron=0+*/1+*+*+*+?").bean(this, "apkApplyCheckEvent");
	        //add by zbc  凌晨执行更新账期状态
	        from("quartz2://dealAccountPeriod?cron=0+0+0+*+*+?").bean(this, "accountPerioEvent");
	        
	        
	        // 账期短信提醒
	        // 每天上午11:55触发
	        from("quartz2://sendMessage?cron=0+55+11+*+*+?").bean(this, "accountPeriodTextReminderEvent");
	        // 测试使用，30秒执行一次：0/10 * * * * ?
//	        from("quartz2://sendMessage?cron=0/30+*+*+*+*+?").bean(this, "accountPeriodTextReminderEvent");
	    }

	    public void triggerEvent(){
	        InjectorInstance.getInstance(EventBus.class).post(new ActiveStateEvent());
	    }
	    
	    public void withdrawApplyEvent(){
			InjectorInstance.getInstance(EventBus.class).post(new WithdrawApplyEvent());
	    }
	    
	    public void apkApplyCheckEvent(){
			InjectorInstance.getInstance(EventBus.class).post(new ApkApplyEvent());
	    }
	    
	    public void accountPerioEvent(){
	    	InjectorInstance.getInstance(EventBus.class).post(new AccountPerioEvent());
	    }
	    
	    /**
	     * 账期短信提醒
	     */
	    public void accountPeriodTextReminderEvent() {
	    	InjectorInstance.getInstance(EventBus.class).post(new AccountPeriodTextReminderEvent());
	    }
}
