@echo off

java -cp lib/h2-1.4.191.jar org.h2.tools.Server -tcp -tcpAllowOthers -baseDir ./data/db

pause