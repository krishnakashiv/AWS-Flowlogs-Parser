
# Flow Log Condenser and Protocol Lookup

This project contains three Java classes: `FlowLogCondenser`, `FlowLogMultithreadedCondenser` and `ProtocolLookup`. The project demonstrates how to manage and access protocol information through Java Maps and perform operations on flow logs.

## Prerequisites

Make sure you have the following installed:
- Java Development Kit (JDK) 8 or higher
- Git

## Getting Started

### Cloning the Repository

You can clone this repository using either HTTPS or SSH, depending on your preference.

#### Using HTTPS:
```bash
git clone https://github.com/krishnakashiv/AWS-Flowlogs-Parser.git
```

#### Using SSH:
```bash
git clone git@github.com:krishnakashiv/AWS-Flowlogs-Parser.git
```

Once cloned, navigate to the project directory:
```bash
cd AWS-Flowlogs-Parser
```

### Modifying `config.properties`

The configuration properties need to be modified, such as file paths. Open the `config.properties` file located in the project directory `/src/illumioTest/` and update the relevant values. Example:
```properties
# config.properties

lookupTablePath=/your/path/here/AWS-Flowlogs-Parser/lookup-table.csv
flowLogPath=/your/path/here/AWS-Flowlogs-Parser/example-flow-logs.txt
outputTagCountsFile=/your/path/here/AWS-Flowlogs-Parser/tag-counts.csv
outputPortProtocolFile=/your/path/here/AWS-Flowlogs-Parser/port_protocol_counts.csv
```

### Modifying `config.properties` path in main function:
```bash
String configFilePath = "/your/path/here/AWS-Flowlogs-Parser/src/illumioTest/config.properties";
```

Make sure the paths are valid for your operating system. Additionally the user should have access permissions for this path to create new files and access existing ones.

### Compiling the Java Classes
## 1. For non-multithreaded program
To compile the Java classes, navigate to the root of the project directory `(/your/path/here/AWS-Flowlogs-Parser/src/)` and run the following command:

```bash
javac illumioTest/FlowLogCondenser.java illumioTest/ProtocolLookup.java
```

This will compile the `FlowLogCondenser` and `ProtocolLookup` classes, generating `.class` files in the `illumioTest` directory.

### Running the Code

Once the classes are compiled, you can run the `FlowLogCondenser` class with the following command:

```bash
java illumioTest.FlowLogCondenser
```
## 2. For multithreaded program
To compile the Java classes, navigate to the root of the project directory `(/your/path/here/AWS-Flowlogs-Parser/src/)` and run the following command:

```bash
javac illumioTest/FlowLogMultithreadedCondenser.java illumioTest/ProtocolLookup.java
```

This will compile the `FlowLogMultithreadedCondenser` and `ProtocolLookup` classes, generating `.class` files in the `illumioTest` directory.

### Running the Code

Once the classes are compiled, you can run the `FlowLogMultithreadedCondenser` class with the following command:

```bash
java illumioTest.FlowLogMultithreadedCondenser
```

***NOTE: Make sure to run the command from the project root, as the package structure (`illumioTest`) needs to be respected.***

# About the program

### Assumptions:
The program currently supports only the default AWS VPC log format i.e. version 2. 

## Key Functionalities

- **Configurable**: File paths for input files (lookup table, flow logs) and output files (tag counts, port-protocol counts) are configurable via a `config.properties` file.
- **Incremental Processing**: Supports merging new counts with previously saved counts, allowing for incremental updates without losing past data.

The main functionalities of the code include:

1. **Loading a Lookup Table (loadLookupTable)**:
   - Reads a CSV file containing port, protocol, and tag mappings.
   - Creates a key (port, protocol) for each line and associates it with a tag in a HashMap.
   - The map is case-insensitive, storing keys in lowercase.

2. **Parsing Flow Logs (parseFlowLogs)**:
   - **Default Implementation**:
     - Reads a flow log file with information about network flows (e.g., source/destination IP, ports, protocols).
     - Extracts the destination port and protocol from each line and looks up a tag from the loaded lookup table.
     - Updates two maps:
       - `tagCounts`: Tracks occurrences for each tag.
       - `portProtocolCounts`: Tracks occurrences for each port-protocol pair.
  
   - **Multithreaded Implementation**:
     - Reads the same flow log file and stores the lines in a list.
     - Divides the log entries into chunks based on the number of threads `numThreads` specified.
     - For each chunk, a separate thread (instance of `FlowLogTask`) is created and submitted for processing.
     - Each thread extracts the destination port and protocol, looks up the corresponding tag, and updates the same two maps (`tagCounts` and `portProtocolCounts`) in a thread-safe manner using synchronization to avoid race conditions.

3. **Writing Counts to Files**:
   - `writeTagCountsToFile`: Writes tag counts to a CSV file.
   - `writePortProtocolCounts`: Writes port-protocol pair counts to a CSV file.

4. **Loading Existing Counts**:
   - `loadExistingTagCounts` and `loadExistingProtocolCounts`: Loads previous counts from files to update them.

5. **Merging Existing and New Counts (mergeCounts)**:
   - Merges existing counts from previous runs with new counts obtained from parsing the current flow log.

6. **Main Method**:
   - Orchestrates the program by:
     - Loading properties from a `config.properties` file, containing file paths.
     - Loading the lookup table, flow logs, and existing counts.
     - Parsing the flow logs to update tag and port-protocol counts.
     - Merging new and existing counts.
     - Writing the updated counts to their respective files.

### Since HashMaps have been used to lookup, the time complexity for searching a tag or protocol-port is `O(1)`
