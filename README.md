# POC For Kuma2.0 Roll Back Solution

## Dependency
- Java 8
- MySQL 8.0
- [SpringBoot 2.1.0.RELEASE](https://spring.io/projects/spring-boot/)
- [FastJson 1.2.62](https://github.com/alibaba/fastjson)
- [Canal Client 1.1.4](https://github.com/alibaba/canal)

## TODO
- [ ] 
- [ ] 
> - [ ] 

## How to setup
> 1. RDS Setting
>> - Enable bin-log setting on AWS RDS: Set the value of binlog_format to **ROW**
>> - Create user and grant privilege of SELECT, REPLICATION SLAVE, REPLICATION CLIENT to this user  

	```SQL
	
	# CREATE USER canal2 IDENTIFIED WITH mysql_native_password BY 'JCH#1357';  
	# GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal2'@'%';
	# FLUSH PRIVILEGES;
	
	```
>> - Run **init.sql** script under the folder sql

> 2. Canal Server Setting
>> - Download [canal 1.1.7](https://github.com/alibaba/canal) from github 
>> - ${Canal_Home_Path}/conf/example/instance.properties

	```YMAL
	# position info
	canal.instance.master.address=${RDS URL}
	...
	# username/password
	canal.instance.dbUsername=canal2
	canal.instance.dbPassword=JCH#1357
	```

> 3. Canal Server Setting: to support multiple datasource
>> - ${Canal_Home_Path}/conf/canal.properties

	```YMAL
	# position info
	canal.destinations = dev,staging
	...
	```
>> Create a new folder under conf/, for example: dev. And refer to step2 to create a new subscribe instance


>> - ${Canal_Home_Path}/bin  
Double click **startup.bat**

> 3. Project Setting  

>> - ${Canal_Home_Path}/src/main/resources/application-dev.yml  

>> - Run the main method of StartupApplication  

>> - AbstractCanalClient.handleEntry(): Callback method which can get the notification of any table modification

## Change Log

### 0.0.1 : 2023-10-24
> Feature added:
>> - Monitor & Subscribe table modification from AWS RDS and print the detail information to console

>  BUG Fix:
>> - N/A


### TODO : 2024-01-31 
> Feature added:
>> - Monitor & Subscribe table modification from kuma2.0, develop the logic and publish to the legacy kuma db, to make sure the two databases are sync-up with each other
