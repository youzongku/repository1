package dxo.category;

import java.util.ArrayList;
import java.util.List;

import entity.category.VirtualCategory;
import forms.category.VirtualCategoryForm;

/**
 * VirtualCategory裁剪/翻译工具
 * 
 * @author ye_ziran
 * @since 2015年12月8日 下午6:15:20
 */
public class VirtualCategoryDxo {
	
	static void translate(VirtualCategory vCategory, VirtualCategoryForm vcForm){
		vcForm.setVcId(vCategory.getId());
		vcForm.setName(vCategory.getName());
		vcForm.setParentId(vCategory.getParentid());
		vcForm.setLevel(vCategory.getLevel());
		vcForm.setPosition(vCategory.getPosition());
		vcForm.setIsShow(vCategory.getShow());
		vcForm.setIsFloat(vCategory.getFloatshow());
		vcForm.setIsNavi(vCategory.getNavi());
		vcForm.setLinkUrl(vCategory.getUrl());
	}
	
	public static VirtualCategoryForm trans(VirtualCategory vCategory){
		VirtualCategoryForm vcForm = new VirtualCategoryForm();
		if(vCategory != null){
			translate(vCategory, vcForm);
		}
		return vcForm;
	}
	
	public static List<VirtualCategoryForm> mutilTrans(List<VirtualCategory> vcList){
		List<VirtualCategoryForm> formList = new ArrayList<VirtualCategoryForm>();
		for (VirtualCategory vc : vcList) {
			formList.add(trans(vc));
		}
		return formList;
	}
}
