@ECHO OFF
cd D:\Java\projects\innovate_wbo_jar_example\InnovatePluginSource
del InnovatePlugin.jar
jar cvmf manifest.mf  InnovatePlugin.jar -C ./bin innovate
echo "Create"
echo "Rem last jar"
del C:\Users\metra\.efianalytics\TunerStudio\plugins\InnovatePlugin.jar
echo "Copy"
COPY InnovatePlugin.jar C:\Users\metra\.efianalytics\TunerStudio\plugins\InnovatePlugin.jar
dir C:\Users\metra\.efianalytics\TunerStudio\plugins\
echo "=================================SUCCESSFULLY MAKE JAR========================================="
