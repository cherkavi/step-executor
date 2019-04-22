java -jar {{ root_folder }}/status-holder-db/h2-1.4.192.jar \
 -webAllowOthers -webPort {{port_status_holder_db_web}} \
 -tcpAllowOthers -tcpPort {{port_status_holder_db_jdbc}} \
 -tcpPassword sa -baseDir \
 {{ root_folder }}/status-holder-db/data