package illumioTest;

import java.util.HashMap;
import java.util.Map;

public class ProtocolLookup {

    // Create and return a protocol lookup map
    public static Map<Integer, String> getProtocolLookup() {
        Map<Integer, String> protocolLookup = new HashMap<>();
        
        protocolLookup.put(6, "tcp");  
        protocolLookup.put(17, "udp"); 
        protocolLookup.put(1, "icmp"); 
        protocolLookup.put(41, "ipv6"); 
        // Add more protocol numbers and names as needed

        return protocolLookup;
    }
}