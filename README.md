# Image Improver Multithreading
Parallel processing application done for college subject.

The app is written in Java using [Apache Maven](https://maven.apache.org/).

## Features

- Image improving process is divided into four subprocesses all running in parallel.
- CountDownLatch implemented to wake up processes that rely on images being present on the first container.
- Semaphore implemented to guarantee no process is working on the same image.
- Log4j used for debug logging and log file creation.

## Setup and Run
No installation is required, attached to the release you can find a zip which contains the executable.

### Easy Way
Simply unzip the folder somewhere in your device and double click 'start.bat' file. This will launch the application.
We recommend setting up a shortcut to 'start.bat' for easy application start-up.

### Recommended Way
The app is better experienced in the Linux console, so we recommend starting it using bash terminal.
For windows, open a bash terminal in the file path and run "java -jar JavaChess-2.0.0.jar"

## Prerequisites
This application requires Java 8 or newer in order to function.
(Optional) For a better experience Git for Windows is recommended.

## Team
- [Fabian Nicolas Hidalgo](https://github.com/Nick07242000)
- [Martina Juri](https://github.com/martinajuri)
- [Wanda Molina](https://github.com/wandamol1405)
- [Agustin Alvarez](https://github.com/AgustinAlvarez2001)
