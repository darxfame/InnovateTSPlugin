@ECHO OFF
echo "Rem last jar"
del %USERPROFILE%\.efianalytics\TunerStudio\plugins\InnovatePlugin_d.jar
echo "Copy"
COPY InnovatePlugin.jar %USERPROFILE%\.efianalytics\TunerStudio\plugins\InnovatePlugin_d.jar
dir %USERPROFILE%\.efianalytics\TunerStudio\plugins\
echo "=================================SUCCESSFULLY MAKE JAR========================================="
timeout 3
