# Swagger Inflector

Run with

```
gradle appRun
```

Generate server

```
java -jar swagger-codegen-cli.jar generate \
  -i testing_openapi_3.json \
  -l inflector -o server/
```

build codegen cli:

```
git clone git@github.com:swagger-api/swagger-codegen.git
cd swagger-codegen
git checkout 3.0.0
mvn clean package
```

Generated with:
- https://github.com/swagger-api/swagger-codegen/commit/914ae5561a49aa5c2caa8c31d200b18d9c898f27
