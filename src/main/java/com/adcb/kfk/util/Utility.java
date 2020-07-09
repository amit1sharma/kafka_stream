package com.adcb.kfk.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import com.adcb.kfk.component.TransactionStreamsValidatorProperties;

public class Utility extends TransactionStreamsValidatorProperties{
	
	protected String replaceMillisToDateFormat(String value){
		Long timeStamp = getTimeStamp(value);
		Date d = new Date(timeStamp);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		String stringDate = sdf.format(d);
		return value.replace(timeStamp.toString(), stringDate);
	}
	
	protected Long getTimeStamp(String value){
		String[] values = value.split(",");
		return Long.parseLong(values[0]);
	}
	
	protected String getKey(String value, int[] keyPosition, String keyName){
		String[] values = value.split(",");
		String returnValue = "";
//		System.out.println("getKey cb record "+value);
		for(int i : keyPosition){
			if(values.length>i && values[i]!=null){
				String str = changeDecimalFormat(values[i], keyName, i);
				returnValue+=str;
//				System.out.println("getKey reading index : "+i + ". Value : "+values[i]);
			}
		}
//		System.out.println("getKey complete key : "+ returnValue);
		return returnValue;
	}
	
	protected String appendCommas(String record){
		String[] columns = record.split(",");
		if(columns.length<allCoulmnsCount){
			for(int i=0; i<(allCoulmnsCount-columns.length);i++){
				record+=",";
			}
		}
		return record;
	}
	
	protected String prependCommas(String record){
		String[] columns = record.split(",");
		if(columns.length<allCoulmnsCount){
			for(int i=0; i<(allCoulmnsCount-columns.length);i++){
				record=","+record;
			}
		}
		return record;
	}

	
	protected String changeDecimalFormat(String value, String keyName, int index){
		try{
			if(checkIfConfiguredDecimal(keyName, index)){
				Double dValue = Double.parseDouble(value);
				return MathUtility.amountRespresantation(dValue, 2);
			}else {
				Integer[] indexes = getSubStrIndex(keyName, index);
				if(indexes!=null && indexes.length>0){
					return value.substring(indexes[0], (indexes[1]));
				}else{
//					System.out.println("returning as it is : "+value);
					return value;
				}
			}
		}catch (Exception e){
//			System.out.println("error occured for index : "+e.getMessage());
			return value;
		}
		
	}
	
	public static void main(String[] args){
		Double dValue = Double.parseDouble("25454");
		System.out.println(MathUtility.amountRespresantation(dValue, 2));
	}
	
	private boolean checkIfConfiguredDecimal(String keyName, int index){
		if(mapOfDecimalIndex.containsKey(keyName)){
			List<Integer> configuredDecimalIndex = mapOfDecimalIndex.get(keyName);
			if(configuredDecimalIndex!=null && configuredDecimalIndex.size()>0){
				return configuredDecimalIndex.contains(index);
			}else{
				return false;
			}
		}
		return false;
	}
	
	private Integer[] getSubStrIndex(String keyName, int i){
		try{
			if(mapOfIndexes.containsKey(keyName+"."+i)){
				return mapOfIndexes.get(keyName+"."+i).toArray(new Integer[0]);
			}else{
				return null;
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
