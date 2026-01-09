package com.example.medmeproject.Service;

import com.example.medmeproject.Dto.AppointmentApiData;
import com.example.medmeproject.Exception.ResourceNotFoundException;
import com.example.medmeproject.Model.PatientTable;
import com.example.medmeproject.repository.PatientRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MedMeApiService {
    @Autowired
    PatientRepository patientRepository;

    @Autowired
    UserService userService;

    public  List<String> getBusinessesIds(int id, String token, String user, int networkId) throws IOException, InterruptedException {
        String jsonBody = String.format("""
                    {
                        "jsonrpc": "2.0",
                        "id": %d,
                        "cred": {
                            "token": "%s",
                            "user": "%s"
                        },
                        "method": "business.get_network_data",
                        "params": {
                            "networkID": %d
                        }
                    }
                """, id, token, user, networkId);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.med.me/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        //used for convert result from jsonNode to list of map
        TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {
        };
        List<Map<String, Object>> businessList = new ArrayList<>();
        List<String> businessesIds = new ArrayList<String>();
        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.path("result");
            if (dataNode != null) {
                JsonNode businesses = dataNode.path("businesses");
                businessList = mapper.readValue(businesses.traverse(), typeRef);
                businessesIds = businessList.stream().map(businessMap -> (String) businessMap.get("businessID")).collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return businessesIds;
    }
    public List<Map<String, String>> searchBusinessesIdByName(int id, String token,String user, boolean skip,String department,String workerSort, int networkId) throws IOException, InterruptedException {
        System.out.printf("Received From python searchBusinessesIdByName service: id: %s ,token: %s ,user: %s ,department: %s ,workerSort: %s ,networkId: %d",id,token,user,department,workerSort,networkId);
        ObjectMapper mapper = new ObjectMapper();
        List<String> businessesIds=getBusinessesIds( id,  token,  user,networkId);
        List<Map<String, String>> matches = new ArrayList<>();
        List<Map<String, String>> tempMatches = new ArrayList<>();
        String businessBoName = "";
        String businessName = "";
        String busId="";
        for (String businessId : businessesIds) {
            String jsonBody = String.format("""
                        {
                            "jsonrpc": "2.0",
                            "id": %d,
                            "cred": {
                                "token": "%s",
                                "user": "%s"
                            },
                            "method": "business.get_profile_by_id",
                            "params": {
                               "business": {
                                   "id":"%s"
                               },
                               "skip_worker_sorting": %b,
                               "worker_sorting_type": "%s"
                           }
                        }
                    """, id, token, user, businessId, skip,workerSort);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://apiv2.med.me/rpc"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            try {
                JsonNode rootNode = mapper.readTree(response.body());
                JsonNode dataNode = rootNode.path("result").path("business");
                if (dataNode != null) {
                    businessName = dataNode.path("general_info").path("name").asText().toLowerCase();
                    businessBoName = dataNode.path("general_info").path("boName").asText().toLowerCase();
                    System.out.println("businessBoName: " + businessBoName);
                    if(businessBoName.equals("null")|| businessBoName.isEmpty()){
                        businessBoName=businessName;
                    }
                    String [] splitted=businessBoName.split(" - ");
                    Map<String, String> match = new HashMap<>();
                    match.put("businessId", businessId);
                    match.put("businessName", splitted[1]);
                    tempMatches.add(match);
//                    if(splitted[1].trim().equals(department.toLowerCase().trim())){
                    if(splitted[1].trim().toLowerCase().contains(department.toLowerCase().trim())){
                        matches.add(match);

//                        busId=businessId;
//                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("busineesID: "+ busId);
        }
//        return busId;
        if(matches.isEmpty()){
            matches.addAll(tempMatches);
        }
        return matches;
    }
    // THIS FUNCTION TO GET TIME TABLES FOR:
    //BUSINESS,RESOURCES,TAXONOMIES
    public JsonNode getBusinessesTimetable(int id, String token, String user, String businessId, boolean skip,String workerSort) throws IOException, InterruptedException {
        String jsonBody = String.format("""
                    {
                        "jsonrpc": "2.0",
                        "id": %d,
                        "cred": {
                            "token": "%s",
                            "user": "%s"
                        },
                        "method": "business.get_profile_by_id",
                        "params": {
                           "business": {
                               "id":"%s"
                           },
                           "skip_worker_sorting": %b,
                          "worker_sorting_type": "%s"

                       }
                    }
                """, id, token, user, businessId, skip,workerSort);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.med.me/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode businessTimetable = null;
        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.path("result").path("business");
            if (dataNode != null) {
                businessTimetable = dataNode.path("general_info").path("timetable").path("week");
                System.out.println("businessTimetable: " + businessTimetable);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return businessTimetable;
    }

    public List<Map<String, JsonNode>> getBusinessesResourcesTimetable(int id, String token, String user, String businessId, boolean skip,String workerSort) throws IOException, InterruptedException {
        String jsonBody = String.format("""
                    {
                        "jsonrpc": "2.0",
                        "id": %d,
                        "cred": {
                            "token": "%s",
                            "user": "%s"
                        },
                        "method": "business.get_profile_by_id",
                        "params": {
                           "business": {
                               "id":"%s"
                           },
                           "skip_worker_sorting": %b,
                           "worker_sorting_type": "%s"
                           
                       }
                    }
                """, id, token, user, businessId, skip,workerSort);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.med.me/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, JsonNode>> businessResourcesListWeek = new ArrayList<Map<String, JsonNode>>();
        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.path("result").path("business").path("resources");
            if (dataNode != null && dataNode.isArray()) {

                for (JsonNode resourceNode : dataNode) {
                    Map<String, JsonNode> temp = new HashMap<>();
                    JsonNode idNode = resourceNode.path("id");
                    if (idNode == null || !idNode.isTextual()) {
                        continue;
                    }
                    String resourceId = idNode.asText();
                    JsonNode timeTableNode = resourceNode.path("timetable").path("week");
                    if (timeTableNode != null) {
                        temp.put(resourceId, timeTableNode);
                        businessResourcesListWeek.add(temp);
                    }
                }
                System.out.println("BusinessesResourcesTimetable: " + businessResourcesListWeek);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return businessResourcesListWeek;
    }

    public List<Map<String, JsonNode>> getBusinessesResourcesTaxonomiesIds(int id, String token, String user, String businessId, boolean skip,String workerSort) throws IOException, InterruptedException {
        String jsonBody = String.format("""
                    {
                        "jsonrpc": "2.0",
                        "id": %d,
                        "cred": {
                            "token": "%s",
                            "user": "%s"
                        },
                        "method": "business.get_profile_by_id",
                        "params": {
                           "business": {
                               "id":"%s"
                           },
                           "skip_worker_sorting": %b,
                            "worker_sorting_type": "%s"
                           
                       }
                    }
                """, id, token, user, businessId, skip,workerSort);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.med.me/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode taxonomiesIdsNode = null;
        List<Map<String, JsonNode>> businessResourcesTaxonomiesIds = new ArrayList<Map<String, JsonNode>>();
        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.path("result").path("business").path("resources");
            if (dataNode != null && dataNode.isArray()) {

                for (JsonNode resourceNode : dataNode) {
                    Map<String, JsonNode> temp = new HashMap<>();
                    JsonNode idNode = resourceNode.path("id");
                    if (idNode == null || !idNode.isTextual()) {
                        continue;
                    }
                    String resourceId = idNode.asText();
                    taxonomiesIdsNode = resourceNode.path("taxonomies");
                    if (taxonomiesIdsNode != null) {
                        temp.put(resourceId, taxonomiesIdsNode);
                        businessResourcesTaxonomiesIds.add(temp);
                    }
                }
                System.out.println("BusinessesResourcesTaxonomiesIds: " + businessResourcesTaxonomiesIds);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return businessResourcesTaxonomiesIds;
    }

    public List<Map<String, JsonNode>> getBusinessesTaxonomies(int id, String token, String user, String businessId, boolean skip,String workerSort) throws IOException, InterruptedException {

        String jsonBody = String.format("""
                    {
                        "jsonrpc": "2.0",
                        "id": %d,
                        "cred": {
                            "token": "%s",
                            "user": "%s"
                        },
                        "method": "business.get_profile_by_id",
                        "params": {
                           "business": {
                               "id":"%s"
                           },
                           "skip_worker_sorting": %b,
                          "worker_sorting_type": "%s"
                           
                       }
                    }
                """, id, token, user, businessId, skip,workerSort);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.med.me/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, JsonNode>> businessTaxonomies = new ArrayList<Map<String, JsonNode>>();
        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.path("result").path("business").path("taxonomies");
            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode resourceNode : dataNode) {

                    ObjectNode valueObject = mapper.createObjectNode();
                    JsonNode idNode = resourceNode.path("id");
                    JsonNode timeTable = resourceNode.path("timetable");
                    JsonNode taxonomiesExtraIdNode = resourceNode.path("extraId");
                    JsonNode taxonomiesSiteIdkNode = resourceNode.path("siteId");
                    JsonNode taxonomiesAliasNode = resourceNode.path("alias").path("en-us");
                    JsonNode taxonomiesDurationNode = resourceNode.path("duration");
                    JsonNode taxonomiesTimeWeekNode = null;
                    if (timeTable != null && timeTable.isObject()) {
                        taxonomiesTimeWeekNode = timeTable.path("week");
                    }
                    valueObject.put("alias", taxonomiesAliasNode.asText().toLowerCase());
                    valueObject.put("timetable", taxonomiesTimeWeekNode);
                    valueObject.put("extraId", taxonomiesExtraIdNode);
                    valueObject.put("siteId", taxonomiesSiteIdkNode);
                    valueObject.put("duration", taxonomiesDurationNode);
                    Map<String, JsonNode> temp = new HashMap<>();

                    if (idNode == null || !idNode.isTextual()) {
                        continue;
                    }
                    String resourceId = idNode.asText();
                    temp.put(resourceId, valueObject);
                    businessTaxonomies.add(temp);
                }
                System.out.println("BusinessesTaxonomies: " + businessTaxonomies);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return businessTaxonomies;
    }
    public List<Map<String, JsonNode>> getBusinessesResources(int id, String token, String user, String businessId, boolean skip,String workerSort) throws IOException, InterruptedException {
        String jsonBody = String.format("""
                    {
                        "jsonrpc": "2.0",
                        "id": %d,
                        "cred": {
                            "token": "%s",
                            "user": "%s"
                        },
                        "method": "business.get_profile_by_id",
                        "params": {
                           "business": {
                               "id":"%s"
                           },
                           "skip_worker_sorting": %b,
                            "worker_sorting_type": "%s"
                           
                       }
                    }
                """, id, token, user, businessId, skip,workerSort);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.med.me/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, JsonNode>> businessResources = new ArrayList<Map<String, JsonNode>>();
        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.path("result").path("business").path("resources");
            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode resourceNode : dataNode) {

                    ObjectNode valueObject = mapper.createObjectNode();
                    JsonNode idNode = resourceNode.path("id");
                    JsonNode name = resourceNode.path("name");
                    JsonNode surname = resourceNode.path("surname");
                    String fullName=name.asText().toLowerCase()+" "+ surname.asText().toLowerCase();
                    JsonNode taxonomies = resourceNode.path("taxonomies");
                    JsonNode timetable = resourceNode.path("timetable");
                    JsonNode taxonomiesTimeWeekNode = null;
                    if (timetable != null && timetable.isObject()) {
                        taxonomiesTimeWeekNode = timetable.path("week");
                    }
                    valueObject.put("fullName", fullName);
                    valueObject.put("timetable", taxonomiesTimeWeekNode);
                    valueObject.put("taxonomies", taxonomies);
                    valueObject.put("timetable", timetable);
                    Map<String, JsonNode> temp = new HashMap<>();

                    if (idNode == null || !idNode.isTextual()) {
                        continue;
                    }
                    String resourceId = idNode.asText();
                    temp.put(resourceId, valueObject);
                    businessResources.add(temp);
                }
                System.out.println("businessResources: " + businessResources);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return businessResources;
    }

    public  List<Map<String, String>> searchTaxonomyIdInListTaxonomiesObject(String taxonomyName,int id, String token, String user, String businessId, boolean skip,String workerSort ) throws IOException, InterruptedException {
        List<Map<String, JsonNode>> listTaxonomies=getBusinessesTaxonomies(id,token, user,businessId, skip, workerSort);
        List<Map<String, String>> matches = new ArrayList<>();
        List<Map<String, String>> tempMatches = new ArrayList<>();
        for (Map<String, JsonNode> resourceMap : listTaxonomies) {
            for (Map.Entry<String, JsonNode> entry : resourceMap.entrySet()) {
                JsonNode valueObject = entry.getValue();
                JsonNode aliasNode = null;
                if (valueObject != null && valueObject.isObject()) {
                    aliasNode = valueObject.path("alias");
                }
                if (aliasNode != null && aliasNode.isTextual()) {
//                    Map<String, String> match = new HashMap<>();
//                    match.put("taxonomyId", entry.getKey());
//                    match.put("taxonomyName", aliasNode.asText());
//                    tempMatches.add(match);
                    if (aliasNode.asText().trim().toLowerCase().contains(taxonomyName.toLowerCase().trim())) {
                        Map<String, String> match = new HashMap<>();
                        match.put("taxonomyId", entry.getKey());
                        match.put("taxonomyName", aliasNode.asText());
                        matches.add(match);
                    }
                }
            }
        }
//        if(matches.isEmpty()){
//            matches.addAll(tempMatches);
//        }
        System.out.println("matches--------------------------------:"+matches);
        return matches;
    }
    public int getDuration(String taxonomyId,int id, String token, String user, String businessId, boolean skip,String workerSort) throws IOException, InterruptedException {
        List<Map<String, JsonNode>> listTaxonomies = getBusinessesTaxonomies(id, token, user, businessId, skip, workerSort);
        int duration = 0;
        for (Map<String, JsonNode> mapEntry : listTaxonomies) {
            for (Map.Entry<String, JsonNode> entry : mapEntry.entrySet()) {
                String Id = entry.getKey();
                if(Id.equals(taxonomyId)){
                    JsonNode valueObject = entry.getValue();
                    JsonNode durationNode = valueObject.path("duration");
                    if (durationNode != null && durationNode.isNumber()) {
                        duration = durationNode.asInt();
                    } else if (durationNode != null && durationNode.isTextual()) {
                        try {
                            duration = Integer.parseInt(durationNode.asText());
                        } catch (NumberFormatException e) {
                            System.err.println("Warning: Duration for ID " + Id + " is not a valid number.");
                        }
                }

                }
            }
        }
        return duration;
    }
    public  List<Map<String, String>> searchResourceIdInListResourcesObject(String resourceName,int id, String token, String user, String businessId, boolean skip,String workerSort  ) throws IOException, InterruptedException {
        List<Map<String, JsonNode>> listResources=getBusinessesResources(id,  token,  user,  businessId,  skip, workerSort);
        List<Map<String, String>> matches = new ArrayList<>();
        List<Map<String, String>> tempMatches = new ArrayList<>();

        for (Map<String, JsonNode> resourceMap : listResources) {
            for (Map.Entry<String, JsonNode> entry : resourceMap.entrySet()) {
                JsonNode valueObject = entry.getValue();
                JsonNode fullNameNode = null;
                if (valueObject != null && valueObject.isObject()) {
                    fullNameNode = valueObject.path("fullName");
                }
                if (fullNameNode != null && fullNameNode.isTextual()) {
                    Map<String, String> match = new HashMap<>();
                    match.put("resourceId", entry.getKey());
                    match.put("resourceName", fullNameNode.asText());
                    tempMatches.add(match);
                    if (resourceName.trim().toLowerCase().contains(fullNameNode.asText().trim().toLowerCase())) {
                        System.out.println("resource: " + entry.getKey());
                        matches.add(match);
//                        return entry.getKey();
                    }
                }
            }
        }
        if(matches.isEmpty()){
            matches.addAll(tempMatches);
        }
//        return "resource: is null";
        return matches;
    }
    public JsonNode searchTaxonomyTimeTableInListTaxonomiesObject(String taxonomyId, List<Map<String, JsonNode>> listTaxonomies) {
        JsonNode taxonomy = null;
        JsonNode timeTable = null;
        for (Map<String, JsonNode> resourceMap : listTaxonomies) {
            JsonNode valueObject = resourceMap.get(taxonomyId);
            if (valueObject != null && valueObject.isObject()) {
                timeTable = valueObject.path("timetable");
                if (timeTable!=null){
                    break;
                }
            }
        }
        System.out.println("searchTaxonomyTimeTableInListTaxonomiesObject: "+timeTable);
        return timeTable;
    }

    //timezone example ==> "Asia/Jerusalem"
    public List<Map<String, List<JsonNode>>> getCRACResourcesAndRooms(int id, String token, String user, String businessId, String timezone, String resourcesId,String taxonomies, String dateFrom, String dateTo,boolean skip,String workerSort ) throws IOException, InterruptedException {
        List<Map<String, List<JsonNode>>> dateCutSlotsList = new ArrayList<>();
        int duration =getDuration( taxonomies, id, token,  user,  businessId,skip, workerSort);
        System.out.println("(getCRACResourcesAndRooms Service) duration: " +duration);
        if(dateFrom.equals(dateTo)||dateTo.equals("")){
            ZonedDateTime from = ZonedDateTime.parse(dateFrom);
            ZonedDateTime toMidnight = from.plusDays(1).with(LocalTime.MIDNIGHT);
            dateTo = toMidnight.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        }

        String jsonBody = String.format("""
        {
           "jsonrpc": "2.0",
           "id": %d,
           "cred":{
                "token": "%s",
                "user": "%s"
           },
           "method": "CracSlots.GetCRACResourcesAndRooms",
           "params":
              {
                 "business":{
                    "id": "%s",
                    "widget_configuration": {
                       "cracServer": "CRAC_PROD3",
                       "mostFreeEnable": true
                    },
                    "general_info": {
                       "timezone": "%s"
                    }
                 },
                 "filters":{
                    "resources":[
                       {"id": "%s", "duration": %d}
                    ],
                    "taxonomies":[
                       "%s"
                    ],
                    
                    "date":{
                       "from": "%s", 
                       "to": "%s"
                    }
                 }
              }
        }
        """, id, token, user, businessId, timezone, resourcesId,duration, taxonomies, dateFrom, dateTo);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://cracslots.gbooking.ru/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.path("result").path("slots");


            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode slot : dataNode) {
                    Map<String, List<JsonNode>> dateMap = new HashMap<>();
                    List<JsonNode> aggregatedCutSlots = new ArrayList<>();
                    JsonNode dateNode = slot.path("date");
                    String dateKey = (dateNode != null && dateNode.isTextual()) ? dateNode.asText() : "UnknownDate";
                    JsonNode resourcesNode = slot.path("resources");
                    if (resourcesNode != null && resourcesNode.isArray()) {
                        for (JsonNode resourceNode : resourcesNode) {
                            JsonNode cutSlotsNode = resourceNode.path("cutSlots");
                            if (cutSlotsNode != null && cutSlotsNode.isArray()) {
                                for (JsonNode cutSlot : cutSlotsNode) {
                                    if(cutSlot.path("available").asText().equals("true")){
                                        aggregatedCutSlots.add(cutSlot);
                                    }
                                }
                            }
                        }
                    }
                    if (!aggregatedCutSlots.isEmpty()) {
                        dateMap.put(dateKey, aggregatedCutSlots);
                        dateCutSlotsList.add(dateMap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("getCRACResourcesAndRooms "+dateCutSlotsList);
        return dateCutSlotsList;
    }
    public List<Map<String, String>> getFirstAvailableDay(int id, String token, String user, String businessId, String timezone, List<String> resourcesId,String taxonomies,boolean skip,String workerSort) throws IOException, InterruptedException {

        int duration =getDuration( taxonomies, id, token,  user,  businessId,skip, workerSort);
        String resourcesArrayString = resourcesId.stream().map(resource -> "\"" + resource + "\"").collect(java.util.stream.Collectors.joining(", "));
        System.out.println("resourcesArrayString: " + resourcesArrayString);
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        String jsonBody = String.format("""
                        {
                           "jsonrpc": "2.0",
                           "id": %d,
                           "cred":{
                                "token": "%s",
                                "user": "%s"
                            },
                            "method":"Crac.CRACResourcesFreeByDateV2",
                            "params":[
                                {
                                    "business": {
                                        "id": "%s"
                                    },
                                    "taxonomy":{
                                        "id":"%s"
                                    },
                                    "resources":[
                                        %s
                                    ],
                                    "duration":%d,
                                    "location": "%s"
                                }
                            ]
                        }
                """, id, token, user, businessId,taxonomies,resourcesArrayString,duration, timezone);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://crac-prod3.gbooking.ru/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        String resourceId="";
        String date="";
        String maxFreeMinutes="";
        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.path("result").path("Free");


            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode freeSlotNode : dataNode) {
                    resourceId = freeSlotNode.path("resource").asText();
                    date = freeSlotNode.path("date").asText();
                    maxFreeMinutes = freeSlotNode.path("maxFreeMinutes").asText();
                    if (date.equals("0001-01-01T00:00:00Z")){
                        continue;
                    }
                    Map<String, String> slotData = new HashMap<>();
                    slotData.put("resourceId", resourceId);
                    slotData.put("date", date);
                    slotData.put("maxFreeMinutes", maxFreeMinutes);
                    result.add(slotData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("getFirstAvailableDay "+result);
        return result;
    }

    public String reserveAppointment(int id, String token, String user,String startDateTime,int amount,String currency,String businessId,String taxonomies,String client_appear, String resourceId,boolean skip,String workerSort ) throws IOException, InterruptedException {
        int duration =getDuration( taxonomies, id, token,  user,  businessId,skip, workerSort);
        String jsonBody = String.format("""
                        {
                           "jsonrpc": "2.0",
                           "id": %d,
                           "cred":{
                                "token": "%s",
                                "user": "%s"
                            },
                            "method":"appointment.reserve_appointment",
                             "params":{
                                   "appointment":{
                                      "start":"%s",
                                      "duration":%d,
                                      "price":{
                                         "amount":%d,
                                         "currency":"%s"
                                      }
                                   },
                                   "source":"AI",
                                   "business":{
                                      "id":"%s"
                                   },
                                   "taxonomy":{
                                      "id":"%s"
                                   },
                                   "client_appear":"%s",
                                   "resource":{
                                      "id":"%s"
                                   }
                                }
                             }
                """, id, token, user,startDateTime,duration,amount,currency,  businessId,taxonomies,client_appear,resourceId);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.med.me/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        String appointmentId="";

        try {
            JsonNode rootNode = mapper.readTree(response.body());
            System.out.println( "(reserveAppointment)rootNode.path(\"result\"): "+ rootNode.path("result"));
            JsonNode dataNode = rootNode.path("result").path("appointment");
            System.out.println( "(reserveAppointment)rootNode.path(\"result\").path(\"appointment\"): "+ rootNode.path("result").path("appointment"));

            if (dataNode != null ) {
                appointmentId = dataNode.path("id").asText();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("reserveAppointment "+appointmentId);
        return appointmentId;
    }
    public String addPatient(int id, String token, String user,String businessId,String name,String surname,String country_code,String area_code,String number,String email ) throws IOException, InterruptedException {
        PatientTable patient = patientRepository.getPatientByIdentityCard(userService.getUserSession());
        String patientIdMedMeTable="";
        String patientIdMedMe="";
        area_code = area_code.replaceFirst("^0", "");
        if(patient!=null){
            patientIdMedMeTable=patient.getPatientIdMedMe();
            if(! patientIdMedMeTable.equals("")){
                patientIdMedMe=getPatientId(id, token, user , patientIdMedMeTable);
                if(!patientIdMedMe.equals("patient not exist") || !patientIdMedMe.equals("")){
                    return  patientIdMedMe;
                }
            }
        }
        String jsonBody = String.format("""
                     {
                        "jsonrpc":"2.0",
                        "id":"%s",
                        "cred":{
                           "token":"%s",
                           "user":"%s"
                        },
                        "method":"client.add_client",
                        "params":{
                           "business":{
                              "id":"%s"
                           },
                           "client":{
                              "name":"%s",
                              "surname":"%s",
                              "phone":[
                                 {
                                    "country_code":"%s",
                                    "area_code":"%s",
                                    "number":"%s"
                                 }
                              ],
                              "email": ["%s"]
                           }
                        }
                     }
                """, id, token, user,businessId,name,surname,country_code,area_code,number,email);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.med.me/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        String patientId="";

        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.path("result").path("client");
            System.out.println("dataNode  "+dataNode);
            if (dataNode != null) {
                patientId = dataNode.path("id").asText();
                if(patient!=null){
                    patient.setPatientIdMedMe(patientId);
                    patientRepository.save(patient);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("addPatient "+patientId);
        return patientId;
    }
    public String confirm(int id, String token, String user,String appointmentId,String clientId,String datetime,String businessId,String taxonomyId,String resourcesId) throws IOException, InterruptedException {
        System.out.println("(confirm service) data received from python : id:)"+ id+" ,token: "+token+" ,user: "+user+" ,appointmentId: "+appointmentId+" ,clientId: "+clientId+" ,datetime: "+datetime+" ,businessId: "+businessId+" ,taxonomyId: "+taxonomyId+" ,resourcesId: "+resourcesId);

        String jsonBody = String.format("""
                 {
                    "jsonrpc":"2.0",
                    "id":"%s",
                    "cred":{
                           "token":"%s",
                           "user":"%s"
                    },
                    "method":"appointment.client_confirm_appointment",
                    "params": {
                      "appointment": {
                        "id": "%s"
                      },
                      "client": {
                        "id": "%s"
                      }
                    }
                 }
                """, id, token, user,appointmentId,clientId);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.med.me/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        String status="";

        try {
            JsonNode rootNode = mapper.readTree(response.body());
            System.out.println( "(confirm)rootNode: "+ rootNode);
            if(!rootNode.path("error").isMissingNode()){
                return "The appointment not confirmed "+rootNode.path("error").path("message").asText() ;
            }
            JsonNode dataNode = rootNode.path("result");

            if (dataNode != null) {
                System.out.println( "(confirm)rootNode.path(\"result\"): "+ rootNode.path("result"));
                status = dataNode.path("status").asText();
                System.out.println( "(confirm)dataNode.path(\"status\").asText(): "+ dataNode.path("status").asText());

                PatientTable patient = patientRepository.getPatientByIdentityCard(userService.getUserSession());
                //save the data to use it if we want to cancel/update the appointment for each patient
                Map<String, AppointmentApiData> appointmentData=new HashMap<String,AppointmentApiData>();
                AppointmentApiData data=new AppointmentApiData();
                data.setAppointmentApiId(appointmentId);
                data.setDateTimeApi(datetime);
                data.setResource_id(resourcesId);
                data.setTaxonomy_id(taxonomyId);
                appointmentData.put(businessId,data);
                if(patient != null){
                    patient.getListMapAppointmentIdsMedMe().add(appointmentData);
                    patientRepository.save(patient);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("confirm "+status);
        return status;
    }
    // i used this function to get client id request to check the the client id value stored in db is exist in gbooking server
    public String getPatientId(int id, String token, String user ,String clientId) throws IOException, InterruptedException {
        System.out.println("(getPatientId service) data received from python : id:)"+ id+" ,token: "+token+" ,user: "+user+" ,clientId: "+clientId);

        String jsonBody = String.format("""
                  {
                      "jsonrpc": "2.0",
                      "id": "%s",
                      "cred": {
                          "token": "%s",
                          "user": "%s"
                      },
                      "method": "client.get_client",
                      "params": {
                          "client": {
                              "id": "%s"
                          }
                      }
                  }
                """, id, token, user,clientId);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.med.me/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        String patientId="";

        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.path("result").path("client");
            System.out.println("dataNode  "+dataNode);
            if (dataNode != null) {
                patientId = dataNode.path("id").asText();
                System.out.println("(getPatientId service) found patientId:"+ patientId);

            }
            else {
                patientId="patient not exist";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Patient ID "+patientId);
        return patientId;
    }
    //search appointment id in specific business by dateTime to cancel or update appointment
    public String searchAppointmentIdByDateTime(String dateTime,String businessId,String taxonomyId,String resourcesId){
        System.out.println("(searchAppointmentIdByDateTime service) data received from python : dateTime:)"+ dateTime+" ,businessId: "+businessId+" ,taxonomyId: "+taxonomyId+" ,resourcesId: "+resourcesId);

        PatientTable patient = patientRepository.getPatientByIdentityCard(userService.getUserSession());
        List<Map<String, AppointmentApiData>> listAppointments=new ArrayList<Map<String, AppointmentApiData>>();
        boolean businessExists = false;
        if(patient != null) {
            listAppointments = patient.getListMapAppointmentIdsMedMe();
            System.out.println("(searchAppointmentIdByDateTime service) listAppointments:"+listAppointments.toString());
            AppointmentApiData data;
            for (Map<String, AppointmentApiData> appointment : listAppointments) {
                if (appointment.containsKey(businessId)) {
                    businessExists = true;
                     data = appointment.get(businessId);
                    if (data != null && data.getDateTimeApi().equals(dateTime)&&data.getTaxonomy_id().equals(taxonomyId)&&data.getResource_id().equals(resourcesId)) {
                        System.out.println("(searchAppointmentIdByDateTime service) getAppointmentApiId : )"+ data.getAppointmentApiId());
                        return data.getAppointmentApiId();
                    }
                }
            }
            if (!businessExists) {
                throw new ResourceNotFoundException("Business with ID '" + businessId + "' not found in patient history.");
            } else {
                throw new ResourceNotFoundException("Business '" + businessId + "' found, but no appointment matches time: " + dateTime);
            }
        }
        return "null";
    }
    public String getClientId(){
        PatientTable patient = patientRepository.getPatientByIdentityCard(userService.getUserSession());
        if(patient != null) {
            return patient.getPatientIdMedMe();
        }
        return "null";
    }
    public String cancelAppointment(int id, String token, String user ,String appointmentId,String clientId) throws IOException, InterruptedException {
        System.out.println("(cancelAppointment service) data received from python : id:)"+ id+" ,token: "+token+" ,user: "+user+" ,appointmentId: "+appointmentId+" ,clientId: "+clientId);

        String jsonBody = String.format("""
                {
                    "jsonrpc": "2.0",
                    "id": %d,
                    "cred": {
                        "token": "%s",
                        "user": "%s"
                    },
                    "method": "appointment.cancel_appointment_by_client",
                    "params": {
                        "appointment": {
                            "id": "%s"
                        },
                        "client": {
                            "clientID": "%s"
                        }
                    }
                }
                """, id, token, user,appointmentId,clientId);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.gbooking.ru/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.path("result");
            System.out.println("(cancelAppointment service) rootNode.path(\"result\"): "+dataNode);
            System.out.println("dataNode != null:"+dataNode != null);
            System.out.println("!dataNode.isNull():"+!dataNode.isNull());
            System.out.println("dataNode.asBoolean():"+dataNode.asBoolean());
            if (dataNode != null && !dataNode.isNull() && dataNode.asBoolean()) {
                PatientTable patient = patientRepository.getPatientByIdentityCard(userService.getUserSession());
                if(patient != null) {
                    List<Map<String, AppointmentApiData>>list=patient.getListMapAppointmentIdsMedMe();
                    list.removeIf(map -> map.values().stream().anyMatch(data -> data.getAppointmentApiId().equals(appointmentId.trim())));
                    patientRepository.save(patient);
                }
                System.out.println("(cancelAppointment service) ----------------------SUCCESS");

                return "SUCCESS";
            } else {
                JsonNode errorNode = rootNode.path("error");
                if (errorNode != null) {
                    return "API_ERROR: " + errorNode.path("message").asText();
                }
                return "FAILURE";
            }
        } catch (IOException e) {
            return "PARSING_ERROR";
        }
    }
    public String unReserveAppointment(int id, String token, String user ,String appointmentId,String businessId) throws IOException, InterruptedException {
        System.out.println("(unReserveAppointment service) data received from python : id:"+ id+" ,token: "+token+" ,user: "+user+" ,appointmentId: "+appointmentId+" ,businessId: "+businessId);
        String jsonBody = String.format("""
                {
                    "jsonrpc": "2.0",
                    "id": %d,
                    "cred": {
                        "token": "%s",
                        "user": "%s"
                    },
                    "method": "appointment.client_remove_empty_appointment",
                    "params": {
                        "appointment": {
                            "id": "%s"
                        },
                        "business": {
                            "id": "%s"
                        }
                    }
                }
                """, id, token, user,appointmentId,businessId);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apiv2.gbooking.ru/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(response.body());
            if (rootNode.has("error") && !rootNode.path("error").isMissingNode()) {
                String errorMessage = rootNode.path("error").path("message").asText();
                int errorCode = rootNode.path("error").path("code").asInt();
                System.out.println("Unreserve failed: " + errorMessage + " (Code: " + errorCode + ")");
                return "ERROR: " + errorMessage;
            }
            JsonNode resultNode = rootNode.path("result");
            if (resultNode.isBoolean() && resultNode.asBoolean()) {
                System.out.println("Unreserve successful: Slot is released.");
                return "success";
            } else {
                return "ERROR: Unknown response from server";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Exception during parsing: " + e.getMessage();
        }
    }
}
