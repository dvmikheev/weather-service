# Weather service

This is the service that monitors weather forecasts for specific locations.

Main functions
1. Read a list of locations and limit temperatures to monitor from a configuration file 2. Periodically request weather service
3. Check if any limit is exceeded in the next five days and store the results
4. Implement a REST API that will return the results
5. (OPTIONAL) Implement a web frontend consuming the API to show each monitored location and the results

## Build and execute the project
### Executable .jar file
If you want to build the project, you need to execute the following command:

```bash
mvn clean install
```
You can then run the service by executing the following command:
```bash
mvn spring-boot:run 
```
You can execute following command to run code checks:
```bash
mvn clean install -PcodeChecks
```

## Docker Container
This project is also runnable as a Docker container. 
If you want to build the corresponding image, you need to execute the following in the project's root folder:

Build the image
```bash
docker build -t weather-service . 
```
You can then run the service by executing the following command
```bash
docker run -p 8089:8089 weather-service
```

## Calling the service
You can access the service by sending GET request to `http://localhost:8089/weather`

Service will return the JSON array of location with periods when the temperature exceeded limits
