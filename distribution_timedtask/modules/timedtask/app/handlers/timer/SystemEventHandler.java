package handlers.timer;


import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import events.core.SystemStartedEvent;
import events.core.SystemStoppedEvent;
import events.timer.TimedTaskRunningStatusEvent;
import play.Logger;
import service.timer.ISessionServiceEx;

public class SystemEventHandler{

	@Inject
	private ISessionServiceEx sessionServiceEx;

	public static boolean run_timed_task = false;
	
	private static final String STATUS_KEY = "timedtask_running_status";
	
	@Subscribe
    public void executeSystemStart(SystemStartedEvent event){
        Logger.info("[systemEvent]========	BBC timedtask module started	========[systemEvent]");
        
        // 获取redis中，定时任务模块的运行状态
        Boolean status = (Boolean) sessionServiceEx.doGet(STATUS_KEY);
        
        if(status == null){
        	//空的话添加状态值,并修改本模块的定时任务执行标识
        	sessionServiceEx.doSet(STATUS_KEY, new Boolean(true));
        	run_timed_task = true;
        }else{
        	//非空的话，校验状态
        	if(!status){
        		sessionServiceEx.doSet(STATUS_KEY, new Boolean(true));
            	run_timed_task = true;
        	}
        }
        
        Logger.info("[systemEvent]========	BBC timedtask module running status:[{}]	========[systemEvent]",run_timed_task);
        
    }
	
	@Subscribe
    public void executeSystemStop(SystemStoppedEvent event){
		Logger.info("[systemEvent]========	BBC timedtask module stop	========[systemEvent]");
		
		//直接修改redis中的状态为“非运行”
        Boolean status = (Boolean) sessionServiceEx.doGet(STATUS_KEY);
        
        if(status != null){
        	//空的话添加状态值,并修改本模块的定时任务执行标识
        	sessionServiceEx.doSet(STATUS_KEY, new Boolean(false));
        	run_timed_task = false;
        }
        
        Logger.info("[systemEvent]========	BBC timedtask module running status:[{}]	========[systemEvent]",run_timed_task);
        
    }
	
	@Subscribe
	public void syncRunningStatus(TimedTaskRunningStatusEvent event){
		
        // 获取redis中，定时任务模块的运行状态
        Boolean status = (Boolean) sessionServiceEx.doGet(STATUS_KEY);
        
        if(status == null){
        	//空的话添加状态值,并修改本模块的定时任务执行标识
        	sessionServiceEx.doSet(STATUS_KEY, new Boolean(true));
        	run_timed_task = true;
        }else{
        	//非空的话，校验状态
        	if(!status){
        		sessionServiceEx.doSet(STATUS_KEY, new Boolean(true));
            	run_timed_task = true;
        	}
        }
        
        Logger.info("[systemEvent]========	syncRunningStatus BBC timedtask module running status,current status:[{}]	========[systemEvent]",run_timed_task);
		
	}
	
}
