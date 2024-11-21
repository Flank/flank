# Working with Windows on GitHub actions

## Differences between system property user.home and environment variable HOMEPATH

1. ```%HOMEPATH%``` in a batch scripts returns ```D:\Users\runneradmin\```
1. In Java ```System.getProperty("user.home")``` returns ``` C:\Users\runneradmin\ ```

For Windows recommended is using ``` System.getenv("HOMEPATH") ``` instead of ```System.getProperty("user.home")```
