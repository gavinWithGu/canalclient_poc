package com.jch.iot.kumamerge.pubsub;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;

import lombok.extern.slf4j.Slf4j;

/**
 * CanalClient: Monitor MySQL bin-log
 * 
 * @author guangyin.gu
 *
 */
@Component
@Slf4j
public class DatabasePubsubClient{

	@Value("${canal.server.destination}")
	private String canalServerDestination;

	@Value("${canal.server.ip}")
	private String canalServerIP;

	@Value("${canal.server.port}")
	private int canalServerPort;
	
	// @PostConstruct
	public void init() {
		CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalServerIP, canalServerPort),
				canalServerDestination,"","");

		final SimpleCanalClient client = new SimpleCanalClient(canalServerDestination);
		client.setConnector(connector);
		client.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					log.info("## Stop the canal client");
					client.stop();
				} catch (Throwable e) {
					log.warn("## Something goes wrong when stopping canal:", e);
				} finally {
					log.info("## Canal client is down.");
				}
			}

		});
	}
}
