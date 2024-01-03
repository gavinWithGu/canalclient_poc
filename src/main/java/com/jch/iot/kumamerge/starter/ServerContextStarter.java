package com.jch.iot.kumamerge.starter;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jch.iot.kumamerge.pubsub.DatabasePubsubClient;

import lombok.extern.slf4j.Slf4j;

/**
 * Trigger which will be launched after application startup
 * 
 * @author guangyin.gu 2023-10-24 
 * @version 1.0.4
 */
@Component
@Slf4j
public class ServerContextStarter implements CommandLineRunner  {
	private static final byte[] lock = new byte[0];

	@Resource
	private DatabasePubsubClient databasePubsubStarter;
	
	@Override
	public void run(String... args) throws Exception {
		
		databasePubsubStarter.init();  // Launch the database monitor client
		log.info("Start database pubsub client successfully!");
		
	}

}
