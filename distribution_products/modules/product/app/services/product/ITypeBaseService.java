package services.product;

import java.util.List;

import entity.product.TypeBase;

public interface ITypeBaseService {

	String addProductType(String param);

	String updateProductType(String param);

	List<TypeBase> getAllTypes();

	String deleteType(Integer tid);

}
