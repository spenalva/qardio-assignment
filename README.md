This project is the solution to the assignment sent by Qardio. 
Its purpose is to manage the temperature measurements made for one or more clients.

# How to start the project
This section describes how the project can be run locally.

## Requirements
To run the project locally the minium requirement is Java 8.
If the source code should be run an IDE is advisable, JDK and Maven are mandatory.
In case the IDE chosen to run it is Eclipse, [Lombok](https://projectlombok.org/) must be installed first.
This project uses the port 8080 to expose the endpoints, so make sure that this port is free before start it.

## Console with JAR
The project can be run directly by executing the jar in the console.
To do so, download the jar file and execute the following command in your console:
```bash
java -jar path/to/jar/file
```

If you want to generate the jar instead of download it, execute the following command:
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
- scale: The scale om which the client wants to retrieve the data. By default, C (Celsius). Available values are C, F (Fahrenheit) and K (Kelvin).
- aggregationType: The aggregation type that will be used to aggregate the data. By default, H (hourly). Available values are H and D (daily).

#### Response
The endpoint will return all data that matches the filter specified. If there was no data matching it, a `404: Not Found` error will be returned.
The format returned will be the following:
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
The payload accepted by this endpoint is the following:
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

Note that the scale parameter is optional and can be removed. In this case, all the measurements will be consider as made on Celsius.