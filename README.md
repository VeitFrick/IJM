# README  #
 Branch: refactor 
 
[![CircleCI](https://circleci.com/bb/Xifiggam/softwaredynamics/tree/refactor.svg?style=svg&circle-token=2f66f81aa0e32d92a3d31d676e97a637f519838b)](https://circleci.com/bb/Xifiggam/softwaredynamics/tree/refactor)

## Building

In order to get started the following steps have to be performed:

1. Checkout the repository.
2. Open a commandline window and navigate to the checkout location.
3. Build the project using `mvn clean package`

If for some reason the local maven repo is missing or has been deleted, you can recreate it using `install-custom-libs.bat`


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