java \
-Dserver.port={{port_monitor}} \
-Dspring.datasource.url=jdbc:h2:tcp://{{inventory_hostname}}:{{port_status_holder_db_jdbc}}{{ root_folder }}/status-holder-db/data \
-Dspring.datasource.username=sa \
-Dspring.datasource.password=sa \
-Ddeployer.host={{ inventory_hostname }} \
-jar {{ root_folder }}/monitor/monitor.jar
