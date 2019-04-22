call mvn clean package -DskipTests -f ..\deployer\pom.xml
dir /b ..\deployer\target\deployer*.jar > _out.tmp
set /p FILE=<_out.tmp
cp ..\deployer\target\%FILE%  .\jars\deployer.jar


call mvn clean package -DskipTests -f ..\monitor\pom.xml
dir /b ..\monitor\target\monitor*.jar > _out.tmp
set /p FILE=<_out.tmp
cp ..\monitor\target\%FILE%  .\jars\monitor.jar


call mvn clean package -DskipTests -f ..\status-holder\pom.xml
dir /b ..\status-holder\target\status-holder*.jar > _out.tmp
set /p FILE=<_out.tmp
cp ..\status-holder\target\%FILE%  .\jars\status-holder.jar


call mvn clean package -DskipTests -f ..\uploader\pom.xml
dir /b ..\uploader\target\uploader*.jar > _out.tmp
set /p FILE=<_out.tmp
cp ..\uploader\target\%FILE%  .\jars\uploader.jar

wget http://central.maven.org/maven2/com/h2database/h2/1.4.192/h2-1.4.192.jar
rm _out.tmp