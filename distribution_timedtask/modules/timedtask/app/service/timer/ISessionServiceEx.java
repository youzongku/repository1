package service.timer;

import java.io.Serializable;

public interface ISessionServiceEx {
	
    public Serializable doGet(final String key);
    
    public void doSet(final String key, final Serializable value);
    
    public void doRemove(final String key);
    
    public void doDestroy(final String sessionId);

}
