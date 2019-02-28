@ECHO OFF
cd %~dp0
rmdir bin /s /q
javac -source 6 -target 1.6 -cp lib\*  -sourcepath ./src -d bin src/innovate/InnovatePlugin.java
echo "=================================SUCCESSFULLY COMPILE========================================="
