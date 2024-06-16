package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;


import org.json.JSONArray;
import org.json.JSONObject;

@Service
@RequiredArgsConstructor
public class TripleService {

    private final TripleRepository tripleRepository;

    public Map<String, Integer> syncMap = new HashMap<>();

    public static Integer logCount = 0;

    //Setting the syncMap
    @Autowired
    public void setSyncMap(Map<String, Integer> syncMap) {
        Map<String, Integer> newMap = new HashMap<>();
        newMap.putAll(syncMap);
        this.syncMap = newMap;
    }

    public Integer getNextLogCount()
    {
        logCount = logCount + 1;
        return logCount;
    }



    public Triple convertToTripleModel(TripleEntity tripleEntity)
    {
        return new Triple().builder()
                .subject(tripleEntity.getId().getFirstKey())
                .predicate(tripleEntity.getId().getSecondKey())
                .object(tripleEntity.getObject())
                .build();
    }

    public TripleEntity convertToTripleEntity(Triple triple)
    {
        return new TripleEntity().builder()
                .id(new TripleKey(triple.getSubject(),triple.getPredicate()))
                .object(triple.getObject())
                .build();

    }

    public static String concatenateWithSeparator(String s1, String s2, String separator) {
        StringBuilder sb = new StringBuilder();
        sb.append(s1).append(separator);
        sb.append(s2);
        return sb.toString();
    }

    public List<Triple> query(String subject)
    {
        Optional<List<TripleEntity>> tripleEntities = Optional.ofNullable(tripleRepository.findBySubject(subject));    //retrieving the case with the given case ID
        ArrayList<Triple> triples = new ArrayList<>();

        if(tripleEntities.isPresent())
        {

            for(int i=0;i<tripleEntities.get().size();i++)
            {
                triples.add(convertToTripleModel(tripleEntities.get().get(i)));
            }
            return triples;
        }

        return triples;
    }


    public Boolean update(Triple triple)
    {
        tripleRepository.save(convertToTripleEntity(triple));


        String log_s1_string = null;
        String log_s1_string_store = null;
        try {
            Path path = Paths.get("/home/kt/Sem_6/NoSQL/Final_Project/demo_postgres_May13/demo/src/main/java/com/example/demo/log_s1.json");
            Path path_store = Paths.get("/home/kt/Sem_6/NoSQL/Final_Project/demo_postgres_May13/demo/src/main/java/com/example/demo/syncUpdate.json");

            log_s1_string = new String(Files.readAllBytes(path));
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<UpdateLog>> typeRef = new TypeReference<List<UpdateLog>>() {};
            List<UpdateLog> log_s1 = null;
            log_s1 = mapper.readValue(log_s1_string, typeRef);

            log_s1_string_store = new String(Files.readAllBytes(path_store));
            ObjectMapper mapper_store = new ObjectMapper();
            TypeReference<List<UpdateLog>> typeRef_store = new TypeReference<List<UpdateLog>>() {};
            List<UpdateLog> log_s1_store = null;
            log_s1_store = mapper_store.readValue(log_s1_string_store, typeRef_store);

            UpdateLog update_details = new UpdateLog();
            Integer entry_id = getNextLogCount();
            update_details.setEntry_id(entry_id);
            update_details.setType("update");
            update_details.setServer_id("1");
            update_details.setUpdate_details(triple);

            Instant now = Instant.now();
            ZoneId zoneId = ZoneId.of("Asia/Kolkata"); // You can change this to your desired time zone
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, zoneId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Customize the format if needed

            update_details.setTimestamp(zonedDateTime.format(formatter));

            log_s1.add(update_details);
            log_s1_store.add(update_details);


            ObjectMapper mapper_json = new ObjectMapper();
            String jsonContent = mapper_json.writeValueAsString(log_s1); // Convert list to JSON string

            ObjectMapper mapper_json_store = new ObjectMapper();
            String jsonContent_store = mapper_json_store.writeValueAsString(log_s1_store); // Convert list to JSON string


            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(jsonContent); // Write the JSON content to the buffered writer
                writer.flush(); // Flush the buffer to ensure data is written to the file immediately
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(path_store)) {
                writer.write(jsonContent_store); // Write the JSON content to the buffered writer
                writer.flush(); // Flush the buffer to ensure data is written to the file immediately
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
        catch(RuntimeException e)
        {
            e.printStackTrace();
        }

        return true;
    }


    public UpdateLog update_without_log(Triple triple)
    {
        tripleRepository.save(convertToTripleEntity(triple));
        UpdateLog update_details = new UpdateLog();
        Integer entry_id = getNextLogCount();
        update_details.setEntry_id(entry_id);
        update_details.setType("update");
        update_details.setServer_id("1");
        update_details.setUpdate_details(triple);

        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("Asia/Kolkata"); // You can change this to your desired time zone
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Customize the format if needed

        update_details.setTimestamp(zonedDateTime.format(formatter));

        return update_details;
    }



    public Boolean merge(String serverID) {
        // Create HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Define the request body (JSON file) - sending the current server's log file in an attempt to synchronize the two databases
        Path path = Paths.get("/home/kt/Sem_6/NoSQL/Final_Project/demo_postgres_May13/demo/src/main/java/com/example/demo/log_s1.json");
        Path jsonFilePath = path;
        String requestBody = null;
        try {
            requestBody = Files.readString(jsonFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

        // Parse the JSON string into a JSON array
        JSONArray jsonArray = new JSONArray(requestBody);

        // Create a new JSON array to store filtered entries
        JSONArray filteredArray = new JSONArray();

        // Define the attribute value to filter by
        String pair1 = concatenateWithSeparator("1", serverID, "|");
        Integer syncPoint = syncMap.get(pair1); // Change to the desired attribute value

        // Iterate over the entries in the JSON array
        for (int i = syncPoint; i < jsonArray.length(); i++) {
            JSONObject entry = jsonArray.getJSONObject(i);

            filteredArray.put(entry);
            System.out.println(entry);
        }

        System.out.println("FilteredArray length");
        System.out.println(filteredArray.length());

        // Write the filtered array to a new JSON file
        try (FileWriter fileWriter = new FileWriter("/home/kt/Sem_6/NoSQL/Final_Project/demo_postgres_May13/demo/src/main/java/com/example/demo/filtered_output_1.json")) {
            fileWriter.write(filteredArray.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Path filteredFilePath = Paths.get("/home/kt/Sem_6/NoSQL/Final_Project/demo_postgres_May13/demo/src/main/java/com/example/demo/filtered_output_1.json");
        String filteredRequestBody = null;
        try {
            filteredRequestBody = Files.readString(filteredFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("filteredRequestBody");
        System.out.println(filteredRequestBody);

        // Build the request
        HttpRequest request = null;

        if(Objects.equals(serverID, "2"))
        {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8090/mongo_triple/mergeRequest?serverID=1"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(filteredRequestBody))
                    .build();
        }
        else if(Objects.equals(serverID, "3"))
        {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8100/neo_triple/mergeRequest?serverID=1"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(filteredRequestBody))
                    .build();
        }

        CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Sent the request.");


        //Applying the following operations on the response object
        response.thenApply(httpResponse -> {
            String jsonBody = httpResponse.body();
            System.out.println(jsonBody);
            ObjectMapper mapper = new ObjectMapper();
            try {
                System.out.println("Response object received.");
                List<UpdateLog> updateLogs = mapper.readValue(jsonBody,  new TypeReference<List<UpdateLog>>() {});

                ObjectMapper mapper_2 = new ObjectMapper();
                String json = mapper_2.writeValueAsString(updateLogs);

                //writing to the current server's log
                try (FileWriter writer = new FileWriter("/home/kt/Sem_6/NoSQL/Final_Project/demo_postgres_May13/demo/src/main/java/com/example/demo/log_s"+serverID+".json")) {
                    writer.write(json);
                } catch (IOException e) {
                    // Handle potential IO exceptions
                    e.printStackTrace();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                //Processing the log file
                String log_s1_string = new String(Files.readAllBytes(path));
                String log_s1_filtered_string = new String(Files.readAllBytes(filteredFilePath));    //sending the filtered part of the log file

                ObjectMapper mapper_log = new ObjectMapper();
                TypeReference<List<UpdateLog>> typeRef = new TypeReference<>() {};
                ObjectMapper mapper_log_2 = new ObjectMapper();
                TypeReference<List<UpdateLog>> typeRef_2 = new TypeReference<>() {};
                List<UpdateLog> log_s1 = mapper_log.readValue(log_s1_string, typeRef);
                List<UpdateLog> log_filtered_s1 = mapper_log_2.readValue(log_s1_filtered_string, typeRef_2);


                if (log_s1 == null) {
                        log_s1 = new ArrayList<>();
                    }

                List<UpdateLog> processed_details = processLogs(log_filtered_s1, updateLogs, serverID);

                log_s1.addAll(processed_details);

                ObjectMapper mapper_json = new ObjectMapper();
                String jsonContent = mapper_json.writeValueAsString(log_s1); // Convert list to JSON string
                Files.write(path, jsonContent.getBytes()); // Write JSON string to file


                return true;
            } catch (IOException e) {
                // Handle potential JSON parsing exception
                throw new RuntimeException("Error parsing response body to UpdateLog objects", e);
            }
        });

        return true;
    }


    public List<UpdateLog> mergeRequest(List<UpdateLog> updateLogs, String serverID)
    {

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<UpdateLog>> typeRef = new TypeReference<List<UpdateLog>>() {};

        //writing the received log file from serverID to a file
        try (FileWriter writer = new FileWriter("/home/kt/Sem_6/NoSQL/Final_Project/demo_postgres_May13/demo/src/main/java/com/example/demo/log_s"+serverID+".json")) {
            mapper.writeValue(writer, updateLogs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //a list to store this server's log file in the form of List<UpdateLog>
        List<UpdateLog> updateLogs_2 = null;
        List<UpdateLog> filtered_updateLog_2 = new ArrayList<>();
        List<UpdateLog> filtered_output = new ArrayList<>();
        try {

            Path path = Paths.get("/home/kt/Sem_6/NoSQL/Final_Project/demo_postgres_May13/demo/src/main/java/com/example/demo/log_s1.json");

            updateLogs_2 = mapper.readValue(new FileReader("/home/kt/Sem_6/NoSQL/Final_Project/demo_postgres_May13/demo/src/main/java/com/example/demo/log_s1.json"), typeRef);


            //Processing the two log files to synchronize the two databases - the function returns a log entry indicating that the sync has been made

            String pair1 = concatenateWithSeparator("1", serverID, "|");
            Integer prev_sync_point = syncMap.get(pair1);

            for (int i = prev_sync_point; i < updateLogs_2.size(); i++) {
                filtered_updateLog_2.add(updateLogs_2.get(i));

            }

            List<UpdateLog> processed_details = processLogs_request(filtered_updateLog_2,updateLogs,serverID);
            updateLogs_2.addAll(processed_details);

            //Writing the updated log file back to the log file
            ObjectMapper mapper_json = new ObjectMapper();
            String jsonContent = mapper_json.writeValueAsString(updateLogs_2); // Convert list to JSON string
            Files.write(path, jsonContent.getBytes()); // Write JSON string to file

            if(Objects.equals(updateLogs_2.get(updateLogs_2.size() - 1).getType(), "sync"))
            {
                updateLogs_2.remove(updateLogs_2.size() - 1);
            }


            for (int i = prev_sync_point; i < updateLogs_2.size(); i++) {
                UpdateLog entry = updateLogs_2.get(i);

                filtered_output.add(entry);
                System.out.println(entry);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return filtered_output;
    }


    public List<UpdateLog> processLogs(List<UpdateLog> list1, List<UpdateLog> list2,String serverID) {
        System.out.println(list2);

        int syncPoint1 = 0;
        int syncPoint2 = 0;

        List<UpdateLog> mergedList = mergeLists(list1, list2, syncPoint1, syncPoint2);

        mergedList.sort(new UpdateLogComparator());

        List<Triple> updates = new ArrayList<>();
        Map<String, UpdateLog> latestBySubjectPredicate = new HashMap<>();
        Map<String,Integer> visited = new HashMap<>();

        for(UpdateLog log: mergedList)
        {
            String key = log.getUpdate_details().getSubject() + "-" + log.getUpdate_details().getPredicate();
            if(latestBySubjectPredicate.containsKey(key))
            {
                continue;
            }
//            UpdateLog existingLog = latestBySubjectPredicate.get(key);
            if(Objects.equals(log.getServer_id(), serverID) && !visited.containsKey(key))
            {
                latestBySubjectPredicate.put(key, log);
            }
            else {
                visited.put(key,1);
            }
        }

        for(Map.Entry<String,UpdateLog> entry: latestBySubjectPredicate.entrySet())
        {
            String key = entry.getKey();
            String[] sub_pred = key.split("-");

            Triple triple = new Triple();
            triple.setSubject(sub_pred[0]);
            triple.setPredicate(sub_pred[1]);

            String object = entry.getValue().getUpdate_details().getObject();
            triple.setObject(object);

            updates.add(triple);
        }


        // Call update function on each Triple in updates list
        List<UpdateLog> update_logs_during_sync = new ArrayList<>();
        for (Triple triple : updates) {
            update_logs_during_sync.add(this.update_without_log(triple));
        }

        UpdateLog sync_details = new UpdateLog();

        Integer entry_id = getNextLogCount();

        sync_details.setEntry_id(entry_id);
        sync_details.setType("sync");
        sync_details.setServer_id("1");

        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("Asia/Kolkata"); // You can change this to your desired time zone
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Customize the format if needed
        sync_details.setTimestamp(zonedDateTime.format(formatter));

        ArrayList<String> serverIDs = new ArrayList<String>();
        serverIDs.add("1");
        serverIDs.add(serverID);
        sync_details.setServer_ids(serverIDs);

        String pair1 = concatenateWithSeparator("1", serverID, "|");
        syncMap.put(pair1, entry_id);

        update_logs_during_sync.add(sync_details);

        return update_logs_during_sync;
    }


    public List<UpdateLog> processLogs_request(List<UpdateLog> list1, List<UpdateLog> list2,String serverID) {

        int syncPoint1 = 0;
        int syncPoint2 = 0;

        List<UpdateLog> mergedList = mergeLists(list1, list2, syncPoint1, syncPoint2);
        mergedList.sort(new UpdateLogComparator());

        List<Triple> updates = new ArrayList<>();
        Map<String, UpdateLog> latestBySubjectPredicate = new HashMap<>();
        Map<String,Integer> visited = new HashMap<>();

        for(UpdateLog log: mergedList)
        {
            String key = log.getUpdate_details().getSubject() + "-" + log.getUpdate_details().getPredicate();
            if(latestBySubjectPredicate.containsKey(key))
            {
                continue;
            }
            if(Objects.equals(log.getServer_id(), serverID) && !visited.containsKey(key))
            {
                latestBySubjectPredicate.put(key, log);
            }
            else
            {
                visited.put(key,1);
            }
        }


        for(Map.Entry<String,UpdateLog> entry: latestBySubjectPredicate.entrySet())
        {
            String key = entry.getKey();
            String[] sub_pred = key.split("-");

            Triple triple = new Triple();
            triple.setSubject(sub_pred[0]);
            triple.setPredicate(sub_pred[1]);

            String object = entry.getValue().getUpdate_details().getObject();
            triple.setObject(object);

            updates.add(triple);
        }


        List<UpdateLog> update_logs_during_sync = new ArrayList<>();
        for (Triple triple : updates) {
            update_logs_during_sync.add(this.update_without_log(triple));
        }

        UpdateLog sync_details = new UpdateLog();
        Integer entry_id = getNextLogCount();
        sync_details.setEntry_id(entry_id);
        sync_details.setType("sync");
        sync_details.setServer_id("1");

        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("Asia/Kolkata"); // You can change this to your desired time zone
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Customize the format if needed
        sync_details.setTimestamp(zonedDateTime.format(formatter));

        ArrayList<String> serverIDs = new ArrayList<String>();
        serverIDs.add("1");
        serverIDs.add(serverID);
        sync_details.setServer_ids(serverIDs);

        String pair1 = concatenateWithSeparator("1", serverID, "|");
        syncMap.put(pair1, entry_id);

        update_logs_during_sync.add(sync_details);

        return update_logs_during_sync;

    }

    private int findSyncPoint1(List<UpdateLog> list) {
        if(list == null)
        {
            return 0;
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            UpdateLog log = list.get(i);
            if (log.getType().equals("sync") &&
                    ((new HashSet<>(log.getServer_ids()).containsAll(Arrays.asList("1", "2")) && log.getServer_ids().size() == 2) ||
                            (new HashSet<>(log.getServer_ids()).containsAll(Arrays.asList("2", "1")) && log.getServer_ids().size() == 2))) {
                return i+1;

            }
        }
        System.out.println("Returning 0, did not find a sync point.");
        return 0;
    }

    private int findSyncPoint2(List<UpdateLog> list) {
        System.out.println("In find sync point");
        if(list == null)
        {
            System.out.println("List sent was null.");
            return 0;
        }

        int one_sync_seen = 0;
        System.out.println("Entering the for loop.");
        System.out.println(list.size());
        for (int i = list.size() - 1; i >= 0; i--) {
            System.out.println("Current index: " + i); // Add this line
            try {
                UpdateLog log = new UpdateLog(getNextLogCount(), list.get(i).getType(),list.get(i).getTimestamp(),list.get(i).getServer_id(),list.get(i).getUpdate_details(),list.get(i).getServer_ids());
                System.out.println("Log = " + log);
                if (log.getType().equals("sync") &&
                        ((new HashSet<>(log.getServer_ids()).containsAll(Arrays.asList("1", "2")) && log.getServer_ids().size() == 2) ||
                                (new HashSet<>(log.getServer_ids()).containsAll(Arrays.asList("2", "1")) && log.getServer_ids().size() == 2))) {
                    if(one_sync_seen == 0)
                    {
                        one_sync_seen = 1;
                    }
                    else
                    {
                        System.out.println("Sync point = "+i+1);
                        return i+1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // Add this line to catch any exceptions
            }
        }
        System.out.println("Returning 0, did not find a sync point.");
        return 0;
    }



    private List<UpdateLog> mergeLists(List<UpdateLog> list1, List<UpdateLog> list2, int syncPoint1, int syncPoint2) {
        System.out.println("In merge lists.");
        List<UpdateLog> mergedList = new ArrayList<>();
        if(list1 != null && !list1.isEmpty())
        {
            mergedList.addAll(list1.subList(syncPoint1, list1.size()));
        }
        if(list2 != null && !list2.isEmpty())
        {
            mergedList.addAll(list2.subList(syncPoint2, list2.size()));
        }

        for(int i=0;i<mergedList.size();i++)
        {
            if(mergedList.get(i).getType().equals("sync"))
            {
                try
                {
                    mergedList.remove(i);
                }
                catch(UnsupportedOperationException | IndexOutOfBoundsException e )
                {
                    e.printStackTrace();
                }

            }
        }

        return mergedList;
    }

    private static class UpdateLogComparator implements Comparator<UpdateLog> {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public int compare(UpdateLog log1, UpdateLog log2) {


            int subjectComparison = log1.getUpdate_details().getSubject().compareTo(log2.getUpdate_details().getSubject());
            if (subjectComparison != 0) {
                return subjectComparison;
            }

            int predicateComparison = log1.getUpdate_details().getPredicate().compareTo(log2.getUpdate_details().getPredicate());
            if (predicateComparison != 0) {
                return predicateComparison;
            }

            LocalDateTime time1 = LocalDateTime.parse(log1.getTimestamp(), formatter);
            LocalDateTime time2 = LocalDateTime.parse(log2.getTimestamp(), formatter);


            return time2.compareTo(time1);
        }
    }

    public Boolean tripleMerge(){
        Boolean result1 = merge("2");
        Boolean result2 = merge("3");
        Boolean result3 = merge("2");  //to ensure that 2 gets updates of 3

        return (result1 && result2 && result3);

    }

    public void writeMapToFileCustomFormat(String filename) throws IOException {
        try (FileWriter fw = new FileWriter(filename);
             BufferedWriter writer = new BufferedWriter(fw)) {
            for (Map.Entry<String, Integer> entry : syncMap.entrySet()) {
                writer.write(String.format("%s:%d\n", entry.getKey(), entry.getValue()));
            }
        }
    }

    public Map<String, Integer> readMapFromFile(String filename) throws IOException {
        Map<String, Integer> loadedMap = new HashMap<>();
        try (FileReader fr = new FileReader(filename);
             BufferedReader reader = new BufferedReader(fr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String key = parts[0];
                int value = Integer.parseInt(parts[1]);
                loadedMap.put(key, value);
            }
        }
            return loadedMap;
        }


    @PreDestroy
    public void cleanUp() throws IOException {
        writeMapToFileCustomFormat("/home/kt/Sem_6/NoSQL/Final_Project/demo_postgres_May13/postgresTriples/src/main/java/com/example/demo/syncMap.txt");
    }


}
