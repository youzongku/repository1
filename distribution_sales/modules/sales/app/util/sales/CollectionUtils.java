package util.sales;

import java.util.List;

import com.google.common.collect.Lists;

public class CollectionUtils {
	
	
	
	public static <T> List<List<T>>  createList(List<T> targe,int size) {  
        List<List<T>> listArr = Lists.newArrayList();  
        int arrSize = targe.size()%size==0?targe.size()/size:targe.size()/size+1;  
        for(int i=0;i<arrSize;i++) {  
            List<T>  sub = Lists.newArrayList();  
            for(int j=i*size;j<=size*(i+1)-1;j++) {  
                if(j<=targe.size()-1) {  
                    sub.add(targe.get(j));  
                }  
            }  
            listArr.add(sub);  
        }  
        return listArr;  
    }  
	
	
}
