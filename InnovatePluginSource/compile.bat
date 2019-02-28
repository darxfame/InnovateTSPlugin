@ECHO OFF
cd D:\Java\projects\innovate_wbo_jar_example\InnovatePluginSource
rmdir bin /s /q
javac -source 6 -target 1.6 -cp lib\*  -sourcepath ./src -d bin src/innovate/InnovatePlugin.java
echo "=================================SUCCESSFULLY COMPILE========================================="
