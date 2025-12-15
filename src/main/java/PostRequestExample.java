import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PostRequestExample {

    public static List<String> getBusinessesIds(int id, String token, String user, int networkId) throws IOException, InterruptedException {
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
                // Use readValue() to deserialize the JsonNode's content stream
                businessList = mapper.readValue(businesses.traverse(), typeRef);
                businessesIds = businessList.stream().map(businessMap -> (String) businessMap.get("businessID")).collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return businessesIds;
    }
    public static String searchBusinessesIdByName(int id, String token,String user, boolean skip,String deppartmentName,List<String> businessesIds,String workerSort) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();

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
                    businessName = dataNode.get("general_info").get("name").asText();
                    System.out.println("businessName: " + businessName);
                    String [] splitted=businessName.split("-");
                    if(splitted[1].trim().equals(deppartmentName)){
                        busId=businessId;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return busId;
    }


    // THIS FUNCTION TO GET TIME TABLES FOR:
    //BUSINESS,RESOURCES,TAXONOMIES
    public static JsonNode getBusinessesTimetable(int id, String token, String user, String businessId, boolean skip,String workerSort) throws IOException, InterruptedException {
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

    public static List<Map<String, JsonNode>> getBusinessesResourcesTimetable(int id, String token, String user, String businessId, boolean skip,String workerSort) throws IOException, InterruptedException {
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

    public static List<Map<String, JsonNode>> getBusinessesResourcesTaxonomiesIds(int id, String token, String user, String businessId, boolean skip,String workerSort) throws IOException, InterruptedException {
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

    public static List<Map<String, JsonNode>> getBusinessesTaxonomies(int id, String token, String user, String businessId, boolean skip,String workerSort) throws IOException, InterruptedException {
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
                    JsonNode taxonomiesTimeWeekNode = null;
                    if (timeTable != null && timeTable.isObject()) {
                        taxonomiesTimeWeekNode = timeTable.get("week");
                    }
                    valueObject.put("alias", taxonomiesAliasNode);
                    valueObject.put("timetable", taxonomiesTimeWeekNode);
                    valueObject.put("extraId", taxonomiesExtraIdNode);
                    valueObject.put("siteId", taxonomiesSiteIdkNode);
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
    public static List<Map<String, JsonNode>> getBusinessesResources(int id, String token, String user, String businessId, boolean skip,String workerSort) throws IOException, InterruptedException {
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
                    String fullName=name.asText()+" "+ surname.asText();
                    JsonNode taxonomies = resourceNode.get("taxonomies");
                    JsonNode timetable = resourceNode.get("timetable");
                    JsonNode taxonomiesTimeWeekNode = null;
                    if (timetable != null && timetable.isObject()) {
                        taxonomiesTimeWeekNode = timetable.get("week");
                    }
//                    valueObject.put("name", name);
//                    valueObject.put("surname", surname);
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

    public static String searchTaxonomyIdInListTaxonomiesObject(String taxonomyName, List<Map<String, JsonNode>> listTaxonomies) {
        for (Map<String, JsonNode> resourceMap : listTaxonomies) {
            for (Map.Entry<String, JsonNode> entry : resourceMap.entrySet()) {
                JsonNode valueObject = entry.getValue();
                JsonNode aliasNode = null;
                if (valueObject != null && valueObject.isObject()) {
                    aliasNode = valueObject.get("alias");
                }
                if (aliasNode != null && aliasNode.isTextual()) {
                    if (aliasNode.asText().equals(taxonomyName)) {
                        System.out.println("taxonomy: " + entry.getKey());
                        return entry.getKey();
                    }
                }
            }
        }
        return "";
    }
    public static String searchResourceIdInListResourcesObject(String resourceName, List<Map<String, JsonNode>> listResources) {
        for (Map<String, JsonNode> resourceMap : listResources) {
            for (Map.Entry<String, JsonNode> entry : resourceMap.entrySet()) {
                JsonNode valueObject = entry.getValue();
                JsonNode fullNameNode = null;
                if (valueObject != null && valueObject.isObject()) {
                    fullNameNode = valueObject.get("fullName");
                }
                if (fullNameNode != null && fullNameNode.isTextual()  ) {
                    if (fullNameNode.asText().equals(resourceName)) {
                        System.out.println("source: " + entry.getKey());
                        return entry.getKey();
                    }
                }
            }
        }
        return "";
    }
    public static JsonNode searchTaxonomyTimeTableInListTaxonomiesObject(String taxonomyId, List<Map<String, JsonNode>> listTaxonomies) {
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
    public static List<Map<String, List<JsonNode>>> getCRACResourcesAndRooms(int id, String token, String user, String businessId, String timezone, String resourcesId, int duration, String taxonomies, String dateFrom, String dateTo) throws IOException, InterruptedException {
        List<Map<String, List<JsonNode>>> dateCutSlotsList = new ArrayList<>();
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
        JsonNode slots = null;
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
    public static List<Map<String, String>> getFirstAvailableDay(int id, String token, String user, String businessId, String timezone, List<String> resourcesId, int duration, String taxonomies) throws IOException, InterruptedException {
        List<Map<String, List<JsonNode>>> dateCutSlotsList = new ArrayList<>();
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
        JsonNode slots = null;
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

    public static String reserveAppointment(int id, String token, String user,String startDateTime,int duration,int amount,String currency,String businessId,String taxonomies,String client_appear, String resourceId ) throws IOException, InterruptedException {
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
        JsonNode slots = null;
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
    public static String addPatient(int id, String token, String user,String businessId,String name,String surname,String country_code,String area_code,String number,String email ) throws IOException, InterruptedException {
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
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("addPatient "+patientId);
        return patientId;
    }
    public static String confirm(int id, String token, String user,String appointmentId,String clientId) throws IOException, InterruptedException {
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
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("addPatient "+status);
        return status;
    }


    public static void submit_appointment(int id, String token, String user, int networkId, boolean skip,String deppartmentName,String taxonomyName,String dateFromStr, String dateToStr, int duration,String timeZone,String resourceFullName,String workerSort ) throws IOException, InterruptedException {
        List<String> businessesIds = getBusinessesIds(id, token, user, networkId);
        System.out.println("businessList: " + businessesIds);
        System.out.println("---------------------------------------------------------------------------------------");
        String businessId = searchBusinessesIdByName( id,token, user, skip,deppartmentName, businessesIds,workerSort);
        System.out.println("businessId: " + businessId);
        System.out.println("---------------------------------------------------------------------------------------");
        getBusinessesTimetable(id,token, user, businessId, skip,workerSort);
        System.out.println("---------------------------------------------------------------------------------------");
        getBusinessesResourcesTimetable(id,token, user, businessId, skip,workerSort);
        System.out.println("---------------------------------------------------------------------------------------");
        getBusinessesResourcesTaxonomiesIds(id,token, user, businessId, skip,workerSort);
        System.out.println("---------------------------------------------------------------------------------------");
        //timeTable just for request with credentials (token and user)
        List<Map<String, JsonNode>> listTaxonomies = getBusinessesTaxonomies(id, "e0197978f235b1fbe2ecc386af12ddf5c1594219", "67e16c3d5ce3f65a969705a0", businessId, skip,workerSort);
        System.out.println("---------------------------------------------------------------------------------------");
        List<Map<String, JsonNode>> listResources = getBusinessesResources(id,token, user, businessId, skip,workerSort);
        System.out.println("---------------------------------------------------------------------------------------");
        String taxonomyId=searchTaxonomyIdInListTaxonomiesObject(taxonomyName, listTaxonomies);
        System.out.println("---------------------------------------------------------------------------------------");
        String resourceId=searchResourceIdInListResourcesObject(resourceFullName, listResources);
        System.out.println("---------------------------------------------------------------------------------------");
         searchTaxonomyTimeTableInListTaxonomiesObject( taxonomyId,  listTaxonomies);
        System.out.println("---------------------------------------------------------------------------------------");
//        String dateFromStr = "2025-12-11T00:00:00.000Z";
//        String dateToStr = "2025-12-15T00:00:00.000Z";
        getCRACResourcesAndRooms(id,token, user, businessId, timeZone, resourceId,duration,taxonomyId, dateFromStr, dateToStr);
        System.out.println("---------------------------------------------------------------------------------------");
        getFirstAvailableDay(id,token, user, businessId, timeZone, Arrays.asList("66e6b953c4d4e0c52afac243"),  duration,taxonomyId);
        System.out.println("---------------------------------------------------------------------------------------");
        String appointmentId=reserveAppointment(id,token, user,"2025-12-12T11:00:00.000Z",duration,0,"ILS",businessId,taxonomyId,"NONE", resourceId );
        System.out.println("---------------------------------------------------------------------------------------");
        String clientId=addPatient(id, "e0197978f235b1fbe2ecc386af12ddf5c1594219", "67e16c3d5ce3f65a969705a0",businessId,"farouk6","sh6","972","54","1277777","example6@hotmail.com");
        System.out.println("---------------------------------------------------------------------------------------");
        confirm(1, "e0197978f235b1fbe2ecc386af12ddf5c1594219", "67e16c3d5ce3f65a969705a0",appointmentId, clientId);

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String dateFromStr = "2025-12-11T00:00:00.000Z";
        String dateToStr = "2025-12-30T00:00:00.000Z";
          submit_appointment(1,"e0197978f235b1fbe2ecc386af12ddf5c1594219", "67e16c3d5ce3f65a969705a0" ,  456, true,"קרדיולוגיה", "CONSULTATION CARDIOLOGY CLINIC", dateFromStr,  dateToStr,  30, "Europe/Moscow","מרפאה קרדיאלית" , "workload");

        }
}