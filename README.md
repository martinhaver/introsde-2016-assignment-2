# introsde-2016-assignment-2
**ALESSIO SPALLINO | Assignment2 | University of Trento**

## Project structure
The project is divided into 6 packages:

* ```introsde.rest.client```: it contains ```ClientAssignment2.java```, which performs requests to the server deployed on Heroku;
* ```introsde.rest.ehealth```: it contains ```App.java``` and ```MyApplicationConfig.java``` to run the standalone server;
* ```introsde.rest.ehealth.dao```: it contains ```LifeCoachDao.java``` that manages the connection to the database;
* ```introsde.rest.ehealth.model```: it contains ```HealthMeasureHistory.java```, ```LifeStatus.java```, ```MeasureDefinition.java``` and ```Person.java``` that represent the corresponding tables in the database. They also contain methods to query the database;
* ```introsde.rest.ehealth.resources```: it contains ```MeasureTypeResource.java```, ```PersonCollectionResource.java```, ```HistoriesMidResource.java```, ```HistoriesResource.java``` and ```PersonResource.java```. These classes declare which CRUD operations are allowed and how to perform them;

## Setup

In order to clone the project and run it against the server deployed on Heroku:
* git clone ```https://github.com/AlessioSpallino/introsde-2016-assignment-2.git```

#### Run client with Heroku
 
My HEROKU server link: https://fathomless-lowlands-81350.herokuapp.com/sdelab 

For using it, execute: 
* ```cd introsde-2016-assignment-2```
* ```ant execute.client```

After running the client, you will see two new files:
- client-server-json-heroku.log
- client-server-xml-heroku.log
