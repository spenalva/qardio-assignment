This project is the solution to the assignment sent by Qardio. 
Its purpose is to manage the temperature measurements made for one or more clients.

# How to start the project
This section describes how the project can be run locally.

## Requirements
To run the project locally the minimum requirement is Java 8.
If the source code should be run an IDE is advisable, JDK and Maven 3.6.1+ are mandatory.
In case the IDE chosen to run it is Eclipse, [Lombok](https://projectlombok.org/) must be installed first.
This project uses the port 8080 to expose the endpoints, so make sure that this port is free before start it.

## Console with JAR
The project can be run directly by executing the jar in the console.
To do so, [download the jar file](https://github.com/spenalva/qardio-assignment/raw/master/temperature-log-management-0.0.1-SNAPSHOT.jar) and execute the following command in your console:
```bash
java -jar path/to/jar/file
```

If you want to generate the jar instead of download it, execute the following command in the project directory:
```bash
mvn install
``` 

## Use instructions
The project exposes the following endpoint:
- /measurements: This endpoint has two purposes, according to the http method used:

### Method GET
If you use this method, the endpoint will return the data that the system could have for the client that is using the endpoint.
This method has only one parameter that is mandatory, and four optional parameters

#### Mandatory parameters
- idSensor: The ID of the client.

#### Optional parameters
- timestampFrom: The date, in timestamp format, since the client wants to retrieve data.
- timestampTo: The date, in timestamp format, until the client wants to retrieve data.
- scale: The scale on which the client wants to retrieve the data. By default, C (Celsius). Available values are C, F (Fahrenheit) and K (Kelvin).
- aggregationType: The aggregation type that will be used to aggregate the data. By default, H (hourly). Available values are H and D (daily).

#### Response
The endpoint will return all data that matches the specified filters. If there was no data matching it, a `404: Not Found` error will be returned.
The returned format will be the following:
```bash
[
    {
        "timestamp": 1527811200000,
        "idSensor": 1,
        "value": 15.222344576196832
    },
    {
        "timestamp": 1527897600000,
        "idSensor": 1,
        "value": 21.927720188401064
    },
	...
]
```

### Method POST
If you use this method, the endpoint will save all the data sent as payload from the client.

#### Payload
The accepted payload by this endpoint is the following:
```bash
{
	"measurements" : [
		{
			"timestamp": 1527811200000,
			"idSensor": 1,
			"value": 99,
			"scale": "F"
		},
		{
			"timestamp": 1527811300000,
			"idSensor": 1,
			"value": 75,
			"scale": "F"
		},
		...
	]
}
```

Note that the scale parameter is optional and can be removed. In this case, all the measurements will be considered as made on Celsius.

## cURL Commands
### Method POST
cURL command to create 5 measurements. Two of them will be in the same day but different hours. Two of them will be in different months. The last one will be in another day.
```bash
curl -X POST \
  http://localhost:8080/measurements \
  -H 'Content-Type: application/json' \
  -d '{"measurements" : [{ "timestamp": 1527811200000, "idSensor": 1, "value": 99.0}, { "timestamp": 1527814800000, "idSensor": 1, "value": 1.0 }, { "timestamp": 1527901200000, "idSensor": 1, "value": 30.0 }, { timestamp": 1525222800000, "idSensor": 1, "value": 15.0 }, { "timestamp": 1530493200000, "idSensor": 1, "value": 15.0 } ] }'
```

### Method GET
cURL command to retrieve the data stored previously. This command will return the data aggregated daily, and will filter out two of the five measurements created with the previous cURL command.

```bash
curl -X GET 'http://localhost:8080/measurements?idSensor=1&timestampFrom=1525222900000&timestampTo=1530493100000&aggregationType=D'
```
## Assumptions
To complete this project, I had to do some assumptions:
- How to deal with possible duplicates? If, for some reason, the client send a measurement but the client does not receive the status OK/ACCEPTED, and the clients assumes that it needs to resend the same measurement, I decided that the system will override the measurement. To do so, the primary key of the measurement entity is a composite key: idSensor - timestamp.
- Scales: I have decided to include several scales that the client could use. Because of that, I have also decided to set one of those scales as default, Celsius in this case.
- Aggregation: I have decided to set the aggregation by hourly by default and to make this parameter as optional.
