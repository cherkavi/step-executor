java \
-Dserver.port={{port_status_holder}} \
-Dspring.datasource.url=jdbc:h2:tcp://{{inventory_hostname}}:{{port_status_holder_db_jdbc}}{{ root_folder }}/status-holder-db/data \
-Dspring.datasource.username=sa \
-Dspring.datasource.password=sa \
-Dspring.jpa.hibernate.ddl-auto=update \
-jar {{ root_folder }}/status-holder/status-holder.jar