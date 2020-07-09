package com.adcb.kfk.component;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

//import org.apache.hadoop.security.authentication.util.JaasConfiguration;

@Component
public class TransactionStreamsValidatorProperties {
	
	@Value("${spring.config.location}")
	protected String configFilePath;
	
	@Value("${inbound.topic.fcubs}")
	protected String inboundTopicFcubs;
	
	@Value("${inbound.topic.cb}")
	protected String inboundTopicCB;
	
	@Value("${outbound.topic.email}")
	protected String outboundTopicEmail;
	
	@Value("${outbound.topic.db}")
	protected String outboundTopicDB;
	
	@Value("${key1.position.fcubs}")
	protected int[] fcubsKey1Position;
	
	@Value("${key1.position.cb}")
	protected int[] cbKey1Position;
	
	@Value("${key2.position.fcubs}")
	protected int[] fcubsKey2Position;
	
	@Value("${key2.position.cb}")
	protected int[] cbKey2Position;
	
	@Value("${key3.position.fcubs}")
	protected int[] fcubsKey3Position;
	
	@Value("${key3.position.cb}")
	protected int[] cbKey3Position;
	
	@Value("${key4.position.fcubs}")
	protected int[] fcubsKey4Position;
	
	@Value("${key4.position.cb}")
	protected int[] cbKey4Position;
	
//	@Value("${internal.topic.fcubs}")
//	protected String internalTopicForFCUB;
//	
//	@Value("${internal.topic.cb}")
//	protected String internalTopicForCB;
	
	@Value("${output.json.seperator}")
	protected String outputJsonSeperator;
	
	@Value("${join.window.minutes}")
	protected int joinWindowMinutes;
	
	@Value("${all.columns.count}")
	protected int allCoulmnsCount;
	
	@Value("${sasl.jaas.config}")
	protected String saslJaasConfig;
	
	@Autowired
	protected Environment environment;
	
	@Value("#{${keys.substr.indexes}}")
	protected Map<String, List<Integer>> mapOfIndexes;
	
	@Value("#{${keys.decimal.indexes}}")
	protected Map<String, List<Integer>> mapOfDecimalIndex;
	
	
}
