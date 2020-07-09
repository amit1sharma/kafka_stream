package com.adcb.kfk.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

public class MathUtility {

	/**
	 * This method will roundoff if value has decimal values
	 * @param value
	 * @param places
	 * @return
	 */
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	/**
	 * this method will append zeros after decimal if not present.
	 * will roundoff if value has decimal values.
	 * @param value
	 * @param number
	 * @return
	 */
	public static String amountRespresantation(Double value, int number){
		if(value==null || value == 0.00) return "0";
		StringBuilder zeros = new StringBuilder("##.");
		for(int a=0;a<number;a++){
			zeros.append("0");
		}
		DecimalFormat format = new DecimalFormat(zeros.toString());
		return format.format(value);
	}
	
}
