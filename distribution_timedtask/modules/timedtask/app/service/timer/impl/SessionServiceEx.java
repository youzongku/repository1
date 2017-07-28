package service.timer.impl;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.core.RMap;

import com.google.inject.Inject;

import play.Logger;
import service.timer.ISessionServiceEx;

public class SessionServiceEx implements ISessionServiceEx{

    final Redisson redisson;
    
    private final static String SESSIONID = "430980j09309039f209r092u09239u90327r";
    
    @Inject
    public SessionServiceEx(final Redisson redisson) {
        this.redisson = redisson;
    }
	
	@Override
	public Serializable doGet(String key) {
        final RMap<String, Serializable> sessionMap = this.redisson.getMap(SESSIONID);
        final Serializable s = (Serializable)sessionMap.get((Object)key);
        return s;
	}

	@Override
	public void doSet(String key, Serializable value) {
		final RMap<String, Serializable> sessionMap = this.redisson.getMap(SESSIONID);
        sessionMap.put(key, value);
		
	}

	@Override
	public void doRemove(String key) {
		  final RMap<String, Serializable> sessionMap = this.redisson.getMap(SESSIONID);
	      sessionMap.remove((Object)key);
	}

	@Override
	public void doDestroy(String sessionId) {
		if(StringUtils.isBlank(sessionId)){
			this.redisson.getMap(SESSIONID).delete();
		}else{
			this.redisson.getMap(sessionId).delete();
		}
	}
	
}
