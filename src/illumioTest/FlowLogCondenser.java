package illumioTest;

import java.io.*;
import java.util.*;

public class FlowLogCondenser {

	public static Map<String, String> loadLookupTable(String path) throws IOException {
		Map<String, String> lookupTable = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		br.readLine();
		while ((line=br.readLine()) != null) {
			
			String lineParts[] = line.split(",");
			String port = lineParts[0].trim();
			String protocol = lineParts[1].trim();
			String tag = lineParts[2].trim();
			lookupTable.put(port + "," + protocol, tag);
		}

		return lookupTable;
	}

	public static void parseFlowLogs(String flowLogPath, Map<String, String> lookupTable,
			Map<Integer,String> protocolLookup, Map<String, Integer> tagCounts, 
			Map<String, Integer> portProtocolCounts
			) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(flowLogPath));
		String line;
		while((line=br.readLine())!=null) {
			String[] logParts = line.split("\\s+");
			String dstPort = logParts[5].trim();
			int protocolNum = Integer.parseInt(logParts[7].trim());
			String protocol= protocolLookup.get(protocolNum);
			
			String key=dstPort + "," +protocol;
			System.out.println(key);
			String tag = lookupTable.getOrDefault(key,"Unknown");
			tagCounts.put(tag,tagCounts.getOrDefault(tag,0)+1);
			
			portProtocolCounts.put(key, portProtocolCounts.getOrDefault(key, 0) + 1);
			
		}

	}

	public static void main(String[] args) throws IOException {
		String lookupTablePath = "D:\\pj\\illumio\\lookup-table.csv";
		String flowLogPath = "D:\\pj\\illumio\\example-flow-logs.txt";
		String outputTagCountsFile = "D:\\pj\\illumio\\tag_counts.csv";
		String outputPortProtocolFile = "D:\\pj\\illumio\\port_protocol_counts.csv";

		Map<Integer,String> protocolLookup=ProtocolLookup.getProtocolLookup();
		Map<String, Integer> tagCounts = new HashMap<>();
		Map<String, Integer> portProtocolCounts = new HashMap<>();
		
		Map<String, String> lookupTable = loadLookupTable(lookupTablePath);
		
		parseFlowLogs(flowLogPath, lookupTable,protocolLookup, tagCounts, portProtocolCounts);
		
		for (Map.Entry<String,Integer> entry : portProtocolCounts.entrySet()) 
            System.out.println("Key = " + entry.getKey() +
                             ", Value = " + entry.getValue());
		for (Map.Entry<String,Integer> entry : tagCounts.entrySet()) 
            System.out.println("Key = " + entry.getKey() +
                             ", Value = " + entry.getValue());

	}

}
