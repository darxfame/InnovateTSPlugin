@ECHO OFF
cd %~dp0
del InnovatePlugin.jar
jar cvmf manifest.mf  InnovatePlugin.jar -C ./bin innovate
echo "Create"
echo "Rem last jar"
del %USERPROFILE%\.efianalytics\TunerStudio\plugins\InnovatePlugin.jar
echo "Copy"
COPY InnovatePlugin.jar %USERPROFILE%\.efianalytics\TunerStudio\plugins\InnovatePlugin.jar
dir %USERPROFILE%\.efianalytics\TunerStudio\plugins\
echo "=================================SUCCESSFULLY MAKE JAR========================================="
