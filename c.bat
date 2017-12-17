@Echo Off
if /i "%1"=="" goto :default
if /i "%1"=="i" goto :install
if /i "%1"=="u" goto :uninstall
goto :raw
:default
gradlew iD
goto :eof
:raw
gradlew %1
goto :eof
:install
gradlew iD
goto :eof
:uninstall
gradlew uA
goto :eof