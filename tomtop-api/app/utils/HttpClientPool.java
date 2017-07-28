package utils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import play.Logger;

import java.io.IOException;
import java.util.Stack;

/**
 * HttpClient池
 *
 * @author ye_ziran
 * @since 2016/3/30 21:10
 */
public class HttpClientPool {
    //默认容量
    private static int DEFAULT_CAP = 2<<4;
    private static int MAX_IDLE_CAP;
    private int capacity = 0;

    static Stack<CloseableHttpClient> clientPool = null;
    private static HttpClientPool pool = null;

    private HttpClientPool(){
        this(DEFAULT_CAP);
    }

    /**
     *
     * @param initialCapacity 初始化容量
     */
    private HttpClientPool(int initialCapacity){
        addClients(initialCapacity);
    }

    public static HttpClientPool instance(){
        int initialCapacity = 16;
        if(pool == null){
            initialCapacity = initialCapacity < DEFAULT_CAP ? DEFAULT_CAP : initialCapacity;
            clientPool =  new Stack<>();
            DEFAULT_CAP = initialCapacity;
            MAX_IDLE_CAP = DEFAULT_CAP << 2;
            pool = new HttpClientPool(initialCapacity);
        }
        return pool;
    }

    /**
     * 获得连接
     * @return
     */
    public CloseableHttpClient get(){
        ensureCapacity();
        return clientPool.pop();
    }

    /**
     * 关闭连接
     * @param client
     * @return
     */
    public boolean close(CloseableHttpClient client){
        clientPool.push(client);
        return true;
    }


    /**
     * 确保栈的容量
     *
     * note:
     * <ul>
     *     <li>低于初始化数量的一半时，生成1.5倍于当前容量的clients</li>
     *     <li>高于初始化数量2倍时，回到初始化的数量</li>
     * </ul>
     *
     * @author ye_ziran
     * @return
     */
    public void ensureCapacity(){
        boolean res = false;
        if(clientPool.size() < (capacity >> 1)){
            addClients(capacity);
        }else if(clientPool.size() > MAX_IDLE_CAP){
            cutClients(DEFAULT_CAP);
        }
    }

    /**
     *
     *
     * @param capacity
     *
     * @author ye_ziran
     * @since 2016-03-31
     */
    private void addClients(int capacity){
        Logger.info("连接池新增clients, 数量：{}", capacity);
        for (int i = 0; i < capacity; i++) {
            CloseableHttpClient client = HttpClients.createDefault();
            clientPool.push(client);
        }
        this.capacity = clientPool.capacity();
    }
    /**
     *
     *
     * @param capacity
     *
     * @author ye_ziran
     * @since 2016-03-31
     */
    private void cutClients(int capacity){
        Logger.info("连接池关闭clients, 数量：{}", capacity);
        for (int i = 0; i < capacity; i++) {
            CloseableHttpClient client = clientPool.pop();
            try {
                client.close();
            } catch (IOException e) {
                Logger.error("HttpClientPool.cutClients，关闭连接错误: {}", e.getMessage());
            }
        }
    }

}
