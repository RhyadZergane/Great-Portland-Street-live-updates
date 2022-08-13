# Great portland street arrival application
An application for polling all tube arrivals to great portland street stationArrivals within a set period of time.
It uses the tfl unified api and a timer to poll at regular intervals; it also uses Google's GSON to parse
the JSON response to objects. It also writes to a file 

## Requirments

1. Maven
2. JDK 11
3. Git

### Installation instructions via cmd line / terminal

1. Clone Project
2. Download and install JDK 11
3. Download and install maven following readme in maven zip file (add to path)
4. Compile with `mvn compile`
5. Generate package with `mvn package`
6. Open a cmd prompt/ terminal window in project root
7. Run `java -jar target/underground-1.0-SNAPSHOT.jar`

## Installation instructons via IntelliJ

### Via get from vcs

1. Click get from vcs
2. Make sure that git is selected for version control
3. Get URL from github by clicking get code "code"
4. When prompted, click trust this project
5. Click edit configuraion
6. Click add application
7. Click on the button next to "Main class"
8. Click Main

### Manually

1. Clone project from git
2. Click open project and find project
4. When prompted, click trust this project
5. Click edit configuraion
6. Click add application
7. Click on the button next to "Main class"
8. Click Main

### Installing from release

1. download JAR from release
2. open a terminal/ command prompt in download location
3. Run `java -jar target/{filename}.jar` filename is currently Great.Portland.Street.Underground

