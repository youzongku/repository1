package services.base.utils;

import java.text.DecimalFormat;
import java.util.List;

import com.google.common.collect.Lists;

public class Utils {

	public static String percent(Double percent) {
		if (percent == null) {
			return "";
		}
		DecimalFormat df = new DecimalFormat("#0");
		return df.format(percent * 100);
	}

	public static String money(Double money) {
		if (money == null) {
			return "";
		}
		DecimalFormat df = new DecimalFormat("#,##0.00");
		return df.format(money);
	}

	public static <T> List<List<T>> partition(List<T> list, int partitionSize) {
		List<List<T>> partitioned = Lists.newLinkedList();
		for (int i = 0; i < list.size(); i += partitionSize) {
			partitioned.add(list.subList(i,
					i + Math.min(partitionSize, list.size() - i)));
		}
		return partitioned;
	}

	public static <T> List<T> flatten(List<List<T>> list) {
		List<T> all = Lists.newLinkedList();
		for (List<T> o : list) {
			if (o != null) {
				all.addAll(o);
			}
		}
		return all;
	}
	
	public static String getIncompleteEmail(String email) {
		StringBuffer sBuffer = new StringBuffer();
		if (null != email && !"".equals(email)) {
			sBuffer.append(email.substring(0, 1));
			sBuffer.append("***");
			Integer index = email.indexOf("@");
			String incompleteEmailTolower = email.substring(index,
					email.length()).toLowerCase();
			if (incompleteEmailTolower.contains("tomtop")) {
				incompleteEmailTolower = incompleteEmailTolower.replace(
						"tomtop", "******");
			}
			sBuffer.append(incompleteEmailTolower);
		}
		return sBuffer.toString();
	}
}
