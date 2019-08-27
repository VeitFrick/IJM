# README  #

## Building

In order to get started the following steps have to be performed:

1. Checkout the repository.
2. Open a commandline window and navigate to the checkout location.
3. Build the project using `mvn clean install`

Java 9 or higher is necessary for building.

## Running

The built jar directly supports the following way(s) of running:

Running example for IJM using JDT:
`java -jar at.aau.softwaredynamics.runner-1.0-SNAPSHOT-jar-with-dependencies.jar -src ~/test/IJM/Test_old.java -dst ~/test/IJM/Test.java -c None -m IJM -w FS -g OTG`

Running example for IJM using Spoon:
`java -jar at.aau.softwaredynamics.runner-1.0-SNAPSHOT-jar-with-dependencies.jar -src ~/test/IJM/Test_old.java -dst ~/test/IJM/Test.java -c Java -m IJM_Spoon -w FS -g SPOON`

`-src` and `-dst` give the paths to the two files to compare
`-m` defines the matcher
`-w` defines the output (FS is file system - database is also possible but has to be set up)
`-g` defines the tree generator.

If the tree generator is spoon the matcher has to be `-m IJM_Spoon` and the `-c` parameter has to be set to `-c Java`
If the tree generator is otg (jdt based ijm) the matcher has to be `-m IJM` and the `-c` parameter has to be set to `-c None`