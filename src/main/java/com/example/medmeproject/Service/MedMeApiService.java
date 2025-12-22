package com.example.medmeproject.Service;

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
            JsonNode dataNode = rootNode.get("result");
            if (dataNode != null) {
                JsonNode businesses = dataNode.get("businesses");
                businessList = mapper.readValue(businesses.traverse(), typeRef);
                businessesIds = businessList.stream().map(businessMap -> (String) businessMap.get("businessID")).collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return businessesIds;
    }
    public String searchBusinessesIdByName(int id, String token,String user, boolean skip,String department,String workerSort, int networkId) throws IOException, InterruptedException {
        System.out.printf("Received From python searchBusinessesIdByName service: id: %s ,token: %s ,user: %s ,department: %s ,workerSort: %s ,networkId: %d",id,token,user,department,workerSort,networkId);
        ObjectMapper mapper = new ObjectMapper();
        List<String> businessesIds=getBusinessesIds( id,  token,  user,networkId);
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
                JsonNode dataNode = rootNode.get("result").get("business");
                if (dataNode != null) {
                    businessName = dataNode.get("general_info").get("name").asText().toLowerCase();
                    businessBoName = dataNode.get("general_info").get("boName").asText().toLowerCase();
                    System.out.println("businessBoName: " + businessBoName);
                    if(businessBoName=="null"){
                        businessBoName=businessName;
                    }
                    String [] splitted=businessBoName.split(" - ");
                    if(splitted[1].trim().equals(department.toLowerCase())){
                        busId=businessId;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("busineesID: "+ busId);
        }
        return busId;
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
            JsonNode dataNode = rootNode.get("result").get("business");
            if (dataNode != null) {
                businessTimetable = dataNode.get("general_info").get("timetable").get("week");
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
            JsonNode dataNode = rootNode.get("result").get("business").get("resources");
            if (dataNode != null && dataNode.isArray()) {

                for (JsonNode resourceNode : dataNode) {
                    Map<String, JsonNode> temp = new HashMap<>();
                    JsonNode idNode = resourceNode.get("id");
                    if (idNode == null || !idNode.isTextual()) {
                        continue;
                    }
                    String resourceId = idNode.asText();
                    JsonNode timeTableNode = resourceNode.get("timetable").get("week");
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
            JsonNode dataNode = rootNode.get("result").get("business").get("resources");
            if (dataNode != null && dataNode.isArray()) {

                for (JsonNode resourceNode : dataNode) {
                    Map<String, JsonNode> temp = new HashMap<>();
                    JsonNode idNode = resourceNode.get("id");
                    if (idNode == null || !idNode.isTextual()) {
                        continue;
                    }
                    String resourceId = idNode.asText();
                    taxonomiesIdsNode = resourceNode.get("taxonomies");
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
            JsonNode dataNode = rootNode.get("result").get("business").get("taxonomies");
            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode resourceNode : dataNode) {

                    ObjectNode valueObject = mapper.createObjectNode();
                    JsonNode idNode = resourceNode.get("id");
                    JsonNode timeTable = resourceNode.get("timetable");
                    JsonNode taxonomiesExtraIdNode = resourceNode.get("extraId");
                    JsonNode taxonomiesSiteIdkNode = resourceNode.get("siteId");
                    JsonNode taxonomiesAliasNode = resourceNode.get("alias").get("en-us");
                    JsonNode taxonomiesDurationNode = resourceNode.get("duration");
                    JsonNode taxonomiesTimeWeekNode = null;
                    if (timeTable != null && timeTable.isObject()) {
                        taxonomiesTimeWeekNode = timeTable.get("week");
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
            JsonNode dataNode = rootNode.get("result").get("business").get("resources");
            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode resourceNode : dataNode) {

                    ObjectNode valueObject = mapper.createObjectNode();
                    JsonNode idNode = resourceNode.get("id");
                    JsonNode name = resourceNode.get("name");
                    JsonNode surname = resourceNode.get("surname");
                    String fullName=name.asText().toLowerCase()+" "+ surname.asText().toLowerCase();
                    JsonNode taxonomies = resourceNode.get("taxonomies");
                    JsonNode timetable = resourceNode.get("timetable");
                    JsonNode taxonomiesTimeWeekNode = null;
                    if (timetable != null && timetable.isObject()) {
                        taxonomiesTimeWeekNode = timetable.get("week");
                    }
                    valueObject.put("fullName", fullName);
                    valueObject.put("timetable", taxonomiesTimeWeekNode);
                    valueObject.put("extraId", taxonomies);
                    valueObject.put("siteId", timetable);
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
        for (Map<String, JsonNode> resourceMap : listTaxonomies) {
            for (Map.Entry<String, JsonNode> entry : resourceMap.entrySet()) {
                JsonNode valueObject = entry.getValue();
                JsonNode aliasNode = null;
                if (valueObject != null && valueObject.isObject()) {
                    aliasNode = valueObject.get("alias");
                }
                if (aliasNode != null && aliasNode.isTextual()) {
                    if (aliasNode.asText().toLowerCase().contains(taxonomyName.toLowerCase())) {
                        Map<String, String> match = new HashMap<>();
                        match.put("taxonomyId", entry.getKey());
                        match.put("taxonomyName", aliasNode.asText());
                        matches.add(match);
                    }
                }
            }
        }
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
                    JsonNode durationNode = valueObject.get("duration");
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
    public String searchResourceIdInListResourcesObject(String resourceName,int id, String token, String user, String businessId, boolean skip,String workerSort  ) throws IOException, InterruptedException {
        List<Map<String, JsonNode>> listResources=getBusinessesResources(id,  token,  user,  businessId,  skip, workerSort);
        for (Map<String, JsonNode> resourceMap : listResources) {
            for (Map.Entry<String, JsonNode> entry : resourceMap.entrySet()) {
                JsonNode valueObject = entry.getValue();
                JsonNode fullNameNode = null;
                if (valueObject != null && valueObject.isObject()) {
                    fullNameNode = valueObject.get("fullName");
                }
                if (fullNameNode != null && fullNameNode.isTextual()  ) {
                    if (resourceName.trim().toLowerCase().contains(fullNameNode.asText().trim().toLowerCase())) {
                        System.out.println("source: " + entry.getKey());
                        return entry.getKey();
                    }
                }
            }
        }
        return "resource: is null";
    }
    public JsonNode searchTaxonomyTimeTableInListTaxonomiesObject(String taxonomyId, List<Map<String, JsonNode>> listTaxonomies) {
        JsonNode taxonomy = null;
        JsonNode timeTable = null;
        for (Map<String, JsonNode> resourceMap : listTaxonomies) {
            JsonNode valueObject = resourceMap.get(taxonomyId);
            if (valueObject != null && valueObject.isObject()) {
                timeTable = valueObject.get("timetable");
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
            JsonNode dataNode = rootNode.get("result").get("slots");


            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode slot : dataNode) {
                    Map<String, List<JsonNode>> dateMap = new HashMap<>();
                    List<JsonNode> aggregatedCutSlots = new ArrayList<>();
                    JsonNode dateNode = slot.get("date");
                    String dateKey = (dateNode != null && dateNode.isTextual()) ? dateNode.asText() : "UnknownDate";
                    JsonNode resourcesNode = slot.get("resources");
                    if (resourcesNode != null && resourcesNode.isArray()) {
                        for (JsonNode resourceNode : resourcesNode) {
                            JsonNode cutSlotsNode = resourceNode.get("cutSlots");
                            if (cutSlotsNode != null && cutSlotsNode.isArray()) {
                                for (JsonNode cutSlot : cutSlotsNode) {
                                    if(cutSlot.get("available").asText().equals("true")){
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
            JsonNode dataNode = rootNode.get("result").get("Free");


            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode freeSlotNode : dataNode) {
                    resourceId = freeSlotNode.get("resource").asText();
                    date = freeSlotNode.get("date").asText();
                    maxFreeMinutes = freeSlotNode.get("maxFreeMinutes").asText();
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
            JsonNode dataNode = rootNode.get("result").get("appointment");

            if (dataNode != null ) {
                appointmentId = dataNode.get("id").asText();
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
            JsonNode dataNode = rootNode.get("result").get("client");
            System.out.println("dataNode  "+dataNode);
            if (dataNode != null) {
                patientId = dataNode.get("id").asText();
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
    public String confirm(int id, String token, String user,String appointmentId,String clientId,String datetime) throws IOException, InterruptedException {
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
            JsonNode dataNode = rootNode.get("result").get("appointment");

            if (dataNode != null) {
                status = dataNode.get("status").asText();
                PatientTable patient = patientRepository.getPatientByIdentityCard(userService.getUserSession());
                Map<String, String> appointment=new HashMap<String,String>();
                appointment.put(datetime,appointmentId);
                if(patient != null){
                    patient.getListMapAppointmentIdsMedMe().add(appointment);
                    patientRepository.save(patient);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("addPatient "+status);
        return status;
    }
    public String getPatientId(int id, String token, String user ,String clientId) throws IOException, InterruptedException {
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
            JsonNode dataNode = rootNode.get("result").get("client");
            System.out.println("dataNode  "+dataNode);
            if (dataNode != null) {
                patientId = dataNode.get("id").asText();
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
    public String cancelAppointment(int id, String token, String user ,String appointmentId,String clientId) throws IOException, InterruptedException {
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
                .uri(URI.create(" https://apiv2.gbooking.ru/rpc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        String patientId="";

        try {
            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode dataNode = rootNode.get("result").get("client");
            System.out.println("dataNode  "+dataNode);
            if (dataNode != null) {
                patientId = dataNode.get("id").asText();
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
}
