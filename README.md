# Java DataFrame Library - DevOps Project

![DevOps Logo](logo.png)

A Java implementation of a DataFrame library similar to Python's pandas, designed for a DevOps course project. The library provides tabular data manipulation capabilities with support for CSV/TSV file parsing and various data operations.

|||
|:-:|:-|
|Runtime|[![Java 17](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)|
|Package|[![Static Badge](https://img.shields.io/badge/JUnit-5-red)](https://junit.org/junit5/) [![Static Badge](https://img.shields.io/badge/Maven-3.6-green)](https://maven.apache.org/docs/3.6.3/release-notes.html) [![Static Badge](https://img.shields.io/badge/ANTLR-4.13-orange)](https://www.antlr.org/download.html)|
|`main` branch status|[![.github/workflows/actions.yml](https://github.com/LTBS46/ProjetDevopsIntegration/actions/workflows/actions.yml/badge.svg?branch=main)](https://github.com/LTBS46/ProjetDevopsIntegration/actions/workflows/actions.yml)|
|`dev` branch status|[![.github/workflows/actions.yml](https://github.com/LTBS46/ProjetDevopsIntegration/actions/workflows/actions.yml/badge.svg?branch=dev)](https://github.com/LTBS46/ProjetDevopsIntegration/actions/workflows/actions.yml)|

## Features
- **Tabular Data Structure**: Store and manipulate 2D data with labeled rows and columns
- **File Parsing**: Read CSV and TSV files with automatic type detection
- **Data Operations**:
  - Column selection and subsetting
  - Row/column removal
  - Size and shape queries
  - Data type management
  - Mean, Max and Min of a specific column
  - Write a DataFrame in a csv
- **Type Safety**: Automatic detection and conversion of data types (String, Integer, Float)
## Workflow and tools 

First of all we choose a specific way to manage branches in git to allow continu integration.
The tools are mostly in the badgs of this readme.


## Choices of branches

- **Main** : Contain the latest stable release.
- **Dev**  : Contain the next version which is currently in developement.
- **Other branches**  : The other branches are features branches. 
They are not permanent et will be merge with Dev when the feature is implemented.


# Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/LTBS46/ProjetDevopsIntegration.git
2. Build with Maven:
    ```bash
    mvn clean install
```xml
<dependency>
			<groupId>org.antlr</groupId>
			<artifactId>antlr4-runtime</artifactId>
			<version>4.13.2</version>
</dependency>
```