package illumioTest;

import java.io.*;
import java.util.*;

public class FlowLogCondenser {

	public static Map<String, String> loadLookupTable(String path) throws IOException {
		Map<String, String> lookupTable = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		br.readLine();
		while ((line = br.readLine()) != null) {

			String lineParts[] = line.split(",");
			String port = lineParts[0].trim();
			String protocol = lineParts[1].trim();
			String tag = lineParts[2].trim();
			lookupTable.put((port + "," + protocol).toLowerCase(), tag);
		}

		return lookupTable;
	}

	public static void parseFlowLogs(String flowLogPath, Map<String, String> lookupTable,
			Map<Integer, String> protocolLookup, Map<String, Integer> tagCounts,
			Map<String, Integer> portProtocolCounts) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(flowLogPath));
		String line;
		while ((line = br.readLine()) != null) {
			String[] logParts = line.split("\\s+");
			String dstPort = logParts[5].trim();
			int protocolNum = Integer.parseInt(logParts[7].trim());
			String protocol = protocolLookup.get(protocolNum);

			String key = dstPort + "," + protocol;
			String tag = lookupTable.getOrDefault(key, "Untagged");
			tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);

			portProtocolCounts.put(key, portProtocolCounts.getOrDefault(key, 0) + 1);

		}

	}

	public static void writeTagCountsToFile(Map<String, Integer> tagCounts, String outputPath) throws IOException {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

			writer.write("Tag,Count\n");

			for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
				writer.write(entry.getKey() + "," + entry.getValue() + "\n");
			}

			System.out.println("Tag counts written to " + outputPath);
		}
	}

	public static void writePortProtocolCounts(Map<String, Integer> portProtocolCounts, String outputPath)
			throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
			
			writer.write("Port,Protocol,Count\n");
			
			for (Map.Entry<String, Integer> entry : portProtocolCounts.entrySet()) {
				writer.write(entry.getKey() + "," + entry.getValue() + "\n");
			}
			
			System.out.println("Port,Protocol counts written to " + outputPath);
		}

	}
	public static Map<String, Integer> loadExistingTagCounts(String filePath) throws IOException {
	    Map<String, Integer> existingCounts = new HashMap<>();

	    File file = new File(filePath);
	    if (!file.exists()) {
	        return existingCounts; 
	    }

	    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	    	
	        String line;
	        reader.readLine(); 

	        while ((line = reader.readLine()) != null) {
	            String[] parts = line.split(",");
	            
	            String key = parts[0].trim();
	            int count = Integer.parseInt(parts[1].trim());
	            existingCounts.put(key, count);
	        }
	    }
	    return existingCounts;
	}
	
	public static Map<String, Integer> loadExistingProtocolCounts(String filePath) throws IOException {
	    Map<String, Integer> existingCounts = new HashMap<>();

	    File file = new File(filePath);
	    if (!file.exists()) {
	        return existingCounts; 
	    }

	    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	        String line;
	        reader.readLine(); 

	        while ((line = reader.readLine()) != null) {
	            String[] parts = line.split(",");
	            
	            String key = parts[0].trim()+","+parts[1].trim();
	            int count = Integer.parseInt(parts[2].trim());
	            existingCounts.put(key, count);
	        }
	    }
	    return existingCounts;
	}
	
	public static void mergeCounts(Map<String, Integer> existingCounts, Map<String, Integer> newCounts) {
	    for (Map.Entry<String, Integer> entry : newCounts.entrySet()) {
	        String key = entry.getKey();
	        int newCount = entry.getValue();

	        
	        existingCounts.put(key, existingCounts.getOrDefault(key, 0) + newCount);
	    }
	}
	
	public static Properties loadProperties(String filePath) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(filePath);
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }

	public static void main(String[] args) throws IOException {
		String configFilePath = "D:\\pj\\illumio\\src\\illumioTest\\config.properties";
        Properties properties = loadProperties(configFilePath);
        
		String lookupTablePath = properties.getProperty("lookupTablePath");
		String flowLogPath =  properties.getProperty("flowLogPath");
		String outputTagCountsFile = properties.getProperty("outputTagCountsFile");
		String outputPortProtocolFile = properties.getProperty("outputPortProtocolFile");

		Map<Integer, String> protocolLookup = ProtocolLookup.getProtocolLookup();
		Map<String, String> lookupTable = loadLookupTable(lookupTablePath);
		Map<String, Integer> tagCounts = new HashMap<>();
		Map<String, Integer> portProtocolCounts = new HashMap<>();

		Map<String,Integer> existingTagCounts=loadExistingTagCounts(outputTagCountsFile);
		Map<String,Integer> existingPortProtocolCounts=loadExistingProtocolCounts(outputPortProtocolFile);
		
		parseFlowLogs(flowLogPath, lookupTable, protocolLookup, tagCounts, portProtocolCounts);

		mergeCounts(existingTagCounts, tagCounts);
        mergeCounts(existingPortProtocolCounts, portProtocolCounts);

		writeTagCountsToFile(existingTagCounts, outputTagCountsFile);
		writePortProtocolCounts(existingPortProtocolCounts, outputPortProtocolFile);


	}

}
