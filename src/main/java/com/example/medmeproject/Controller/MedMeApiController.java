package com.example.medmeproject.Controller;

import com.example.medmeproject.Service.AppointmentService;
import com.example.medmeproject.Service.MedMeApiService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medme/api/appointment")
@CrossOrigin
public class MedMeApiController {
    @Autowired
    MedMeApiService medMeApiService;

    @PostMapping("/get-business-ids")
    public List<String> getBusinessesIds(@RequestParam int id, @RequestParam String token, @RequestParam String user, @RequestParam int networkId) throws IOException, InterruptedException {
        return medMeApiService.getBusinessesIds(id, token, user, networkId);
    }
    @PostMapping("/search-business-id-by-name")
    public String searchBusinessesIdByName(@RequestParam int id, @RequestParam String token, @RequestParam String user, @RequestParam boolean skip, @RequestParam String department, @RequestParam String workerSort, @RequestParam int networkId) throws IOException, InterruptedException {
        System.out.printf("Received From python: id: %s ,token: %s ,user: %s ,department: %s ,workerSort: %s ,networkId: %d",id,token,user,department,workerSort,networkId);
        return medMeApiService.searchBusinessesIdByName(id, token, user, skip, department, workerSort, networkId);
    }
    @PostMapping("/get-business-time-table")
    public JsonNode getBusinessesTimetable(@RequestParam int id, @RequestParam String token, @RequestParam String user, @RequestParam String businessId, @RequestParam boolean skip, @RequestParam String workerSort) throws IOException, InterruptedException {
        return medMeApiService.getBusinessesTimetable(id, token, user, businessId, skip, workerSort);
    }
    @PostMapping("/get-business-resources-time-table")
    public List<Map<String, JsonNode>> getBusinessesResourcesTimetable(@RequestParam int id, @RequestParam String token, @RequestParam String user, @RequestParam String businessId, @RequestParam boolean skip, @RequestParam String workerSort) throws IOException, InterruptedException {
        return medMeApiService.getBusinessesResourcesTimetable(id, token, user, businessId, skip, workerSort);
    }
    @PostMapping("/get-business-resources-taxonomies-ids")
    public List<Map<String, JsonNode>> getBusinessesResourcesTaxonomiesIds(@RequestParam int id, @RequestParam String token, @RequestParam String user, @RequestParam String businessId, @RequestParam boolean skip, @RequestParam String workerSort) throws IOException, InterruptedException {
        return medMeApiService.getBusinessesResourcesTaxonomiesIds(id, token, user, businessId, skip, workerSort);
    }
    @PostMapping("/get-business-taxonomies")
    public List<Map<String, JsonNode>> getBusinessesTaxonomies(@RequestParam int id, @RequestParam String token, @RequestParam String user, @RequestParam String businessId, @RequestParam boolean skip, @RequestParam String workerSort) throws IOException, InterruptedException {
        return medMeApiService.getBusinessesTaxonomies(id, token, user, businessId, skip, workerSort);
    }
    @PostMapping("/get-business-resources")
    public List<Map<String, JsonNode>> getBusinessesResources(@RequestParam int id, @RequestParam String token, @RequestParam String user, @RequestParam String businessId, @RequestParam boolean skip, @RequestParam String workerSort) throws IOException, InterruptedException {
        return medMeApiService.getBusinessesResources(id, token, user, businessId, skip, workerSort);
    }
//    @PostMapping("/search-taxonomy-id-by-name")
//    public String searchTaxonomyIdInListTaxonomiesObject(@RequestParam String taxonomyName,@RequestParam int id,@RequestParam String token,@RequestParam String user,@RequestParam String businessId,@RequestParam boolean skip,@RequestParam String workerSort ) throws IOException, InterruptedException{
//        return medMeApiService.searchTaxonomyIdInListTaxonomiesObject(taxonomyName,id,token,user,businessId,skip,workerSort);
//    }
    @PostMapping("/search-taxonomy-id-by-name")
    public  List<Map<String, String>> searchTaxonomyIdInListTaxonomiesObject(@RequestParam String taxonomyName,@RequestParam int id,@RequestParam String token,@RequestParam String user,@RequestParam String businessId,@RequestParam boolean skip,@RequestParam String workerSort ) throws IOException, InterruptedException{
        return medMeApiService.searchTaxonomyIdInListTaxonomiesObject(taxonomyName,id,token,user,businessId,skip,workerSort);
    }
    @PostMapping("/search-resource-id-by-name")
    public String searchResourceIdInListResourcesObject(@RequestParam String resourceName,@RequestParam int id,@RequestParam String token,@RequestParam String user,@RequestParam String businessId,@RequestParam boolean skip,@RequestParam String workerSort) throws IOException, InterruptedException{
        return medMeApiService.searchResourceIdInListResourcesObject(resourceName,id,token,user,businessId,skip,workerSort);
    }
    @PostMapping("/search-business-taxonomy-time-table")
    public JsonNode searchTaxonomyTimeTableInListTaxonomiesObject(@RequestParam String taxonomyId, @RequestParam List<Map<String, JsonNode>> listTaxonomies) {
        return medMeApiService.searchTaxonomyTimeTableInListTaxonomiesObject(taxonomyId, listTaxonomies);
    }
    @PostMapping("/get-resource-available-time-slots")
    public List<Map<String, List<JsonNode>>> getCRACResourcesAndRooms(@RequestParam int id, @RequestParam String token, @RequestParam String user, @RequestParam String businessId, @RequestParam String timezone, @RequestParam String resourcesId,@RequestParam String taxonomies, @RequestParam String dateFrom, @RequestParam String dateTo,@RequestParam boolean skip,@RequestParam String workerSort) throws IOException, InterruptedException {
        System.out.printf("(getCRACResourcesAndRooms controller) Received From python: id: %s ,token: %s ,user: %s ,businessId: %s ,workerSort: %s ,resourcesId: %s,taxonomies: %s,dateFrom: %s,dateTo: %s ",id,token,user,businessId,workerSort,resourcesId,taxonomies,dateFrom,dateTo);

        return medMeApiService.getCRACResourcesAndRooms(id, token, user, businessId, timezone, resourcesId, taxonomies, dateFrom, dateTo, skip,workerSort);
    }
    @PostMapping("/get-resource-first-available-day")
    public List<Map<String, String>> getFirstAvailableDay(@RequestParam int id, @RequestParam String token, @RequestParam String user, @RequestParam String businessId, @RequestParam String timezone, @RequestParam List<String> resourcesId, @RequestParam String taxonomies,@RequestParam boolean skip,@RequestParam String workerSort) throws IOException, InterruptedException {
        System.out.printf("(getFirstAvailableDay controller) Received From python: id: %s ,token: %s ,user: %s ,businessId: %s ,workerSort: %s ,resourcesId: %s,taxonomies: %s ",id,token,user,businessId,workerSort,resourcesId,taxonomies);

        return medMeApiService.getFirstAvailableDay(id, token, user, businessId, timezone, resourcesId,taxonomies, skip,workerSort);
    }
    @PostMapping("/reserve-appointment")
    public String reserveAppointment(@RequestParam int id, @RequestParam String token, @RequestParam String user, @RequestParam String startDateTime,  @RequestParam int amount, @RequestParam String currency, @RequestParam String businessId, @RequestParam String taxonomies, @RequestParam String client_appear, @RequestParam String resourceId,@RequestParam boolean skip,@RequestParam String workerSort) throws IOException, InterruptedException {
        System.out.printf("(reserveAppointment controller) Received From python: id: %s ,token: %s ,user: %s ,startDateTime: %s,amount:%d,currency:%s, businessId: %s ,taxonomies: %s,client_appear:%s ,resourceId:%s,workerSort: %s  ",id,token,user,startDateTime,amount,currency,businessId,taxonomies,client_appear,resourceId,workerSort);

        return medMeApiService.reserveAppointment(id, token, user, startDateTime, amount, currency, businessId, taxonomies, client_appear, resourceId, skip,workerSort);
    }
    @PostMapping("/add-patient")
    public String addPatient(@RequestParam int id,@RequestParam String token,@RequestParam String user,@RequestParam String businessId,@RequestParam String name,@RequestParam String surname,@RequestParam String country_code,@RequestParam String area_code,@RequestParam String number,@RequestParam String email) throws IOException, InterruptedException {
        System.out.printf("(addPatient controller) Received From python: id: %s ,token: %s ,user: %s ,businessId: %s ,name: %s ,surname: %s,country_code: %s,area_code: %s,number: %s,email: %s ",id,token,user,businessId,name,surname,country_code,area_code,number,email);

        return medMeApiService.addPatient(id, token, user, businessId, name, surname, country_code, area_code, number, email);
    }
    @PostMapping("/confirm")
    public String confirm(@RequestParam int id,@RequestParam String token,@RequestParam String user,@RequestParam String appointmentId,@RequestParam String clientId,@RequestParam String datetime) throws IOException, InterruptedException{
        System.out.printf("(confirm controller) Received From python: id: %s ,token: %s ,user: %s ,appointmentId: %s ,clientId: %s,datetime: %s",id,token,user,appointmentId,clientId,datetime);

        return medMeApiService.confirm(id, token, user, appointmentId, clientId,datetime);
    }
}