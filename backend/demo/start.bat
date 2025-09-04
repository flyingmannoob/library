@echo off
echo Starting Spring Boot Application...
cd /d "E:\Study\D1\暑期实训\大作业\lib\backend\demo"

echo Checking Java classpath...
set CLASSPATH=target\classes

echo Adding MySQL connector to classpath...
for %%i in ("%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\*\*.jar") do set CLASSPATH=!CLASSPATH!;%%i

echo Adding Spring Boot dependencies to classpath...
for %%i in ("%USERPROFILE%\.m2\repository\org\springframework\boot\*\*\*.jar") do set CLASSPATH=!CLASSPATH!;%%i

echo Starting application...
java -cp "%CLASSPATH%" com.example.demo.DemoApplication

pause
