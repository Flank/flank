# WIP mock server for kotlin_poc

check if server is online:

```bash
nc -w 5 -z localhost 8080
```

remove process on port 8080

```bash
kill -9 $(lsof -n -i4TCP:8080 | awk '{print $2}' | sed -n 2p)
```
