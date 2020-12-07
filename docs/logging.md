# Logs in Flank

1. Log level depends on the output style.
1. ```Simple, multi``` and ```verbose``` output style prints logs from ```SIMPLE``` and ```DETAILED``` levels.
1. ```Compact``` style prints log only from ```SIMPLE``` level.
1. If you want a print message for all output styles uses ```log``` or ```logLn``` with only ```message``` parameter.
1. If you want print message more detailed message use ```log``` or ```logLn``` and set ```level``` to ```OutputLogLevel.DETAILED```
