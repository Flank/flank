# Development kotlin scripts hints

Sometimes kotlin scrips keep cached scripts even if you modify the source to prevent it you could:

1. Set env variable for ```KOTLIN_MAIN_KTS_COMPILED_SCRIPTS_CACHE_DIR```
1. Run script with additional commands

```bash

rm -d -r $KOTLIN_MAIN_KTS_COMPILED_SCRIPTS_CACHE_DIR && mkdir $KOTLIN_MAIN_KTS_COMPILED_SCRIPTS_CACHE_DIR && kotlin {SCRIPT_FILE_NAME}

```

This command remove all cached scripts and make fresh run
 
