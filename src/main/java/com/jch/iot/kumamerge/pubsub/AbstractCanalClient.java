package com.jch.iot.kumamerge.pubsub;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.Assert;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.protocol.Message;
import com.jch.iot.kumamerge.config.ConstantValue;

/**
 * Abstract class for canal client
 * 
 * @author guangyin.gu 2023-10-24
 * @version 1.0.4
 */
public class AbstractCanalClient extends BaseCanalClient {
	// log日志记录器
	private static final Logger log = LoggerFactory.getLogger(AbstractCanalClient.class);

	public AbstractCanalClient(String destination) {
		this(destination, null);
	}
	
	public AbstractCanalClient(String destination, CanalConnector connector) {
		this.destination = destination;
		this.connector = connector;
	}

	protected void start() {
		Assert.notNull(connector, "connector is null");
		thread = new Thread(()->{
			process();
		});

		thread.setUncaughtExceptionHandler(handler);
		running = true;
		thread.start();
	}

	protected void stop() {
		if (!running) {
			return;
		}
		running = false;
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// ignore
			}
		}

		MDC.remove("destination");
	}

	protected void process() {
		int batchSize = 5 * 1024;
		while (running) {
			try {
				MDC.put("destination", destination);
				connector.connect();
				connector.subscribe();
				while (running) {
					Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
					long batchId = message.getId();
					int size = message.getEntries().size();
					if (batchId == -1 || size == 0) {
						// try {
						// Thread.sleep(1000);
						// } catch (InterruptedException e) {
						// }
					} else {
//						printSummary(message, batchId, size);
//						printEntry(message.getEntries());
						
						this.handleEntry(message.getEntries());
					}

					if (batchId != -1) {
						connector.ack(batchId); // 提交确认
						// connector.rollback(batchId); // 处理失败, 回滚数据
					}
				}
			} catch (Exception e) {
				logger.error("process error!", e);
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e1) {
					// ignore
				}
			} finally {
				connector.disconnect();
				MDC.remove("destination");
			}
		}
	}

	private void handleEntry(List<Entry> entrys) {
		for (Entry entry : entrys) {
			if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN
					|| entry.getEntryType() == EntryType.TRANSACTIONEND) {
				continue;
			}

			RowChange rowChage = null;
			try {
				rowChage = RowChange.parseFrom(entry.getStoreValue());
			} catch (Exception e) {
				throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
						e);
			}

			EventType eventType = rowChage.getEventType();
			log.trace(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
					entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
					entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), eventType));
			
			log.debug("Table: {} is getting updated, eventType: {}",entry.getHeader().getTableName(), eventType);
			
				for (RowData rowData : rowChage.getRowDatasList()) {
					if (eventType == EventType.UPDATE) {
						// printColumn(rowData.getBeforeColumnsList());
						this.handleUpdate(rowData.getBeforeColumnsList(),rowData.getAfterColumnsList());
					} else if (eventType == EventType.INSERT) {
						// printColumn(rowData.getAfterColumnsList());
						this.handleInsert(rowData.getBeforeColumnsList(),rowData.getAfterColumnsList());
					} else if (eventType == EventType.DELETE) {
						// System.out.println("-------> before");
						// printColumn(rowData.getBeforeColumnsList());
						// System.out.println("-------> after");
						// printColumn(rowData.getAfterColumnsList());
						this.handleDelete(rowData.getBeforeColumnsList(),rowData.getBeforeColumnsList());
					}
				}

		}
	}

	private void handleInsert(List<Column> previousColumns,List<Column> lastestColumns) {
		
		log.debug(">> Previous key-value list");
		for (Column column : previousColumns) {
				log.debug(">>> key:{}, value: {} .",column.getName(),column.getValue());
		}
		
		log.debug(">> Current key-value list");
		for (Column column : lastestColumns) {
			log.debug(">>> key:{}, value: {} .",column.getName(),column.getValue());
		}
		
		//TODO: Deepak please implement the logic to insert new data to legacy kuma
	}

	private void handleUpdate(List<Column> previousColumns,List<Column> lastestColumns) {
		log.debug(">> Previous key-value list");
		for (Column column : previousColumns) {
				log.debug(">>> key:{}, value: {} .",column.getName(),column.getValue());
		}
		
		log.debug(">> Current key-value list");
		for (Column column : lastestColumns) {
			log.debug(">>> key:{}, value: {} .",column.getName(),column.getValue());
		}
		
		//TODO: Deepak please implement the logic to update the data to legacy kuma
	}

	private void handleDelete(List<Column> previousColumns,List<Column> lastestColumns) {
		log.debug(">> Previous key-value list");
		for (Column column : previousColumns) {
				log.debug(">>> key:{}, value: {} .",column.getName(),column.getValue());
		}
		
		log.debug(">> Current key-value list");
		for (Column column : lastestColumns) {
			log.debug(">>> key:{}, value: {} .",column.getName(),column.getValue());
		}
		
		//TODO: Deepak please implement the logic to delete the data from legacy kuma
	}


}
