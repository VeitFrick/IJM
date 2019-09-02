# README  #
 Branch: siesta19 
 

## Building

In order to get started the following steps have to be performed:

1. Checkout the repository.
2. Open a commandline window and navigate to the checkout location.
3. Build the project using `mvn clean intstall`


## Running 
Run the CmdRunner with the following arguments:
`-src path\to\the\file\example2source.test	-dst path\to\the\file\example2destination.test	-c None -m IJM -w FS -g OTG`
(java -cp ijm.jar at.aau.softwaredynamics.runner.CmdRunner is needed here for jar)
Choice of classifier, matcher, writer and tree-generator
For Spoon based version use: `-c Java -m IJM_Spoon -g SPOON`

For JDT/Classical IJM version use: `-c None -m IJM -g OTG`

Run the FullProjectAnalyzer with the following arguments: `-p path\to\project -pw password -usr postgres -conn jdbc:postgresql://localhost:5432/depdatabase`



## Build Docker Image of WebService

After building the whole project, move into the `softwaredynamics.diffws` folder.
To build the the docker images the current **version** (e.g. 0.5.0) of the built webservice has to be provided 
(You can check the version when you run ``ls ./target | grep '\<at.*jar\>$'``  and check the suffix of the .jar).

To build to image run following command, substituting ``<currentVersion>`` with your version:

```
$ docker build -t swdyn_ws --build-arg version=<currentVersion> .
```

### Running Docker Container

To run a container from your image, run the following:

```
$ docker run -d -p 8080:8080 --name=swdyn_ws swdyn_ws

```

## Deploying

To prepare for deployment build and push the project to the private registry:


```
docker build -t  swdyn-isys.aau.at:5000/tgrassau/swdyn_ws:<currentVersion> --build-arg version=<currentVersion> .
docker push  swdyn-isys.aau.at:5000/tgrassau/swdyn_ws:<currentVersion>
```