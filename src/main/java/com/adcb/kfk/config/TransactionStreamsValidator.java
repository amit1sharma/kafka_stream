package com.adcb.kfk.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.stereotype.Component;

import com.adcb.kfk.util.Utility;

//import org.apache.hadoop.security.authentication.util.JaasConfiguration;

@Component
public class TransactionStreamsValidator extends Utility{
	
	
	
	@PostConstruct
	public void createStream() {
		System.out.println(configFilePath);
		Properties config = new Properties();
        try (FileReader in = new FileReader(configFilePath)){
        	config.load(in);
        	config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        	config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        	config.put("sasl.jaas.config", saslJaasConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }

		StreamsBuilder builder = new StreamsBuilder();
		
		//////////////////////////////////////////////// FOR ALL AND UNMATCHED COMPARISION WITH KEY1
		KStream<byte[],String> fcubsStream = builder.stream(inboundTopicFcubs);
		KStream<byte[], String> timedfcubsStream = fcubsStream.mapValues(v -> System.currentTimeMillis()+","+v);
		KStream<String, String> keyedfcubsStream = timedfcubsStream.selectKey(new KeyValueMapper<byte[], String, String>() {
													@Override
													public String apply(byte[] key, String value) {
														String rkey = getKey(value, fcubsKey1Position,"key1.position.fcubs");
//														System.out.println("setting key for fcubs key1 :"+rkey);
//														System.out.println("fcubs record : "+value);
														return rkey;
													}
												});

		////////////////////////////////////////////////for unmatched with key2
		KStream<String, String> keyedfcubsStream2 = timedfcubsStream.selectKey(new KeyValueMapper<byte[], String, String>() {
														@Override
														public String apply(byte[] key, String value) {
															String rkey = getKey(value, fcubsKey2Position, "key2.position.fcubs");
//															System.out.println("setting key for fcubs key2 :"+rkey);
//															System.out.println("fcubs record : "+value);
															return rkey;
														}
													});
		
		KStream<String, String> keyedfcubsStream3 = timedfcubsStream.selectKey(new KeyValueMapper<byte[], String, String>() {
													@Override
													public String apply(byte[] key, String value) {
														String rkey = getKey(value, fcubsKey3Position, "key3.position.fcubs");
//														System.out.println("setting key for fcubs key3 :"+rkey);
//														System.out.println("fcubs record : "+value);
														return rkey;
													}
												});
		
		
		KStream<String, String> keyedfcubsStream4 = timedfcubsStream.selectKey(new KeyValueMapper<byte[], String, String>() {
													@Override
													public String apply(byte[] key, String value) {
														String rkey = getKey(value, fcubsKey4Position, "key4.position.fcubs");
										//				System.out.println("setting key for fcubs key3 :"+rkey);
										//				System.out.println("fcubs record : "+value);
														return rkey;
													}
												});
//		keyedfcubsStream2.to(internalTopicForFCUB2);
//		KStream<String, String> keyedfcubsStreamInt2 = builder.stream(internalTopicForFCUB2);	
		////////////////////////////////////////////////FOR ALL WITH KEY1
		KStream<byte[], String> cbStream = builder.stream(inboundTopicCB);
		KStream<byte[], String> timedcbStream = cbStream.mapValues(v -> System.currentTimeMillis()+","+v);
		KStream<String, String> keyedcbStream = timedcbStream.selectKey(new KeyValueMapper<byte[], String, String>() {
													@Override
													public String apply(byte[] key, String value) {
														String rkey = getKey(value, cbKey1Position, "key1.position.cb");
//														System.out.println("setting key for cb key1 :"+rkey);
														return rkey;
													}
												});
		
		
		
		/**
		 * ################################################################################################################
		 */
		
		KStream<String, String> allTransactionStream = keyedcbStream.leftJoin(
				keyedfcubsStream, (cbRecordValue,fcubsRecordValue) -> {
						String returnString="";
						if(fcubsRecordValue==null){
							returnString = cbRecordValue;
						}else{
							long fcubsTime = getTimeStamp(fcubsRecordValue);
							long cbTime = getTimeStamp(cbRecordValue);
							if(fcubsTime>cbTime){
								return null;
							}
							returnString = getKey(cbRecordValue, cbKey1Position, "key1.position.cb")+outputJsonSeperator+replaceMillisToDateFormat(cbRecordValue)+","+
									getKey(fcubsRecordValue, fcubsKey1Position,"key1.position.fcubs")+","+replaceMillisToDateFormat(fcubsRecordValue)+",Matched";
						}
						System.out.println("join 1 returning record in db stream is : "+returnString);
						return returnString;
					},JoinWindows.of(TimeUnit.MINUTES.toMillis(joinWindowMinutes))).filter(new Predicate<String, String>() {
							@Override
							public boolean test(String key, String value) {
								if(StringUtils.isBlank(value)){
									return false;
								}else{
									return true;
								}
							}
						}
	            ).leftJoin(keyedfcubsStream2, (leftRecord,rightRecord) -> {
	            		System.out.println("join 2 cbRecordValue : "+leftRecord);
	            		System.out.println("join 2 fcubsRecordValue : "+rightRecord);
    					String returnString="";
    					if(rightRecord!=null){
    						returnString = getKey(leftRecord, cbKey2Position, "key2.position.cb")+outputJsonSeperator+replaceMillisToDateFormat(leftRecord)+","+
    								getKey(rightRecord, fcubsKey2Position,"key2.position.fcubs")+","+replaceMillisToDateFormat(rightRecord)+",Matched";
		            	} else {
		            		returnString = leftRecord;
		            	}
    					System.out.println("join 2 returning record in db stream is : "+returnString);
    					System.out.println("");
    					return returnString;
    				},JoinWindows.of(TimeUnit.MINUTES.toMillis(joinWindowMinutes))
	    		);
	    		
		KStream<String, String> keyedcbStream3 = allTransactionStream.selectKey(new KeyValueMapper<String, String, String>() {
													@Override
													public String apply(String key, String value) {
														String rkey = getKey(value, cbKey3Position, "key3.position.cb");
														System.out.println("setting key for cb key3 :"+rkey);
														return rkey;
													}
												});
	    		
		KStream<String, String> finalStream3 = keyedcbStream3.leftJoin(keyedfcubsStream3, (leftRecord,rightRecord)->{
		    			System.out.println("join 3 cbRecordValue : "+leftRecord);
	            		System.out.println("join 3 fcubsRecordValue : "+rightRecord);
	    				String returnString="";
		            	if(leftRecord.endsWith(",Matched")){
		            		returnString = leftRecord;
		            	} else if(rightRecord!=null){
		            		returnString = getKey(leftRecord, cbKey3Position, "key3.position.cb")+outputJsonSeperator+replaceMillisToDateFormat(leftRecord)+","+
		    						getKey(rightRecord, fcubsKey3Position,"key3.position.fcubs")+","+replaceMillisToDateFormat(rightRecord)+",Matched";
		            	} else{
		            		returnString = leftRecord;
//		            		returnString = getKey(leftRecord, cbKey3Position,"key3.position.cb")+outputJsonSeperator+replaceMillisToDateFormat(leftRecord);
//		    				returnString = appendCommas(returnString)+",No Matching transaction from FCUBS";
		            	}
		            	System.out.println("join 3 returning record in db stream is : "+returnString);
		            	System.out.println("");
		            	return returnString;
		            }, JoinWindows.of(TimeUnit.MINUTES.toMillis(joinWindowMinutes)));
		
		
		
		//// last condition
		KStream<String, String> keyedcbStream4 = finalStream3.selectKey(new KeyValueMapper<String, String, String>() {
						@Override
						public String apply(String key, String value) {
							String rkey = getKey(value, cbKey4Position, "key4.position.cb");
							System.out.println("setting key for cb key3 :"+rkey);
							return rkey;
						}
					});
		
		
		KStream<String, String> finalStream4 = keyedcbStream4.leftJoin(keyedfcubsStream4, (leftRecord,rightRecord)->{
						System.out.println("join 3 cbRecordValue : "+leftRecord);
			    		System.out.println("join 3 fcubsRecordValue : "+rightRecord);
						String returnString="";
			        	if(leftRecord.endsWith(",Matched")){
			        		returnString = leftRecord;
			        	} else if(rightRecord!=null){
			        		returnString = getKey(leftRecord, cbKey4Position, "key4.position.cb")+outputJsonSeperator+replaceMillisToDateFormat(leftRecord)+","+
									getKey(rightRecord, fcubsKey4Position,"key4.position.fcubs")+","+replaceMillisToDateFormat(rightRecord)+",Matched";
			        	} else{
			        		returnString = getKey(leftRecord, cbKey4Position,"key4.position.cb")+outputJsonSeperator+replaceMillisToDateFormat(leftRecord);
							returnString = appendCommas(returnString)+",No Matching transaction from FCUBS";
			        	}
			        	System.out.println("join 4 returning record in db stream is : "+returnString);
			        	System.out.println("");
			        	return returnString;
			        }, JoinWindows.of(TimeUnit.MINUTES.toMillis(joinWindowMinutes)));
		
		finalStream4.to(outboundTopicDB);
		finalStream4.filter((key,value)->{
			return value.contains(",No Matching transaction from FCUBS");
		}).to(outboundTopicEmail);
	
		KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.cleanUp();
		streams.start();
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}
	
	public static void main(String[] args){
		int i =30;
		long result = 1l;
		for(int j=1;j<=i;j++){
			if(j!=1){
				result=2*result;
				
			}
			System.out.println(result);
		}
	}
	

}
