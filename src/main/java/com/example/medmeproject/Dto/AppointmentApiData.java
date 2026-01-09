package com.example.medmeproject.Dto;

public class AppointmentApiData {
    private String appointmentApiId;
    private String dateTimeApi;
    private String resource_id;
    private String taxonomy_id;


    public String getAppointmentApiId() {
        return appointmentApiId;
    }

    public void setAppointmentApiId(String appointmentApiId) {
        this.appointmentApiId = appointmentApiId;
    }

    public String getDateTimeApi() {
        return dateTimeApi;
    }

    public void setDateTimeApi(String dateTimeApi) {
        this.dateTimeApi = dateTimeApi;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    public String getTaxonomy_id() {
        return taxonomy_id;
    }

    public void setTaxonomy_id(String taxonomy_id) {
        this.taxonomy_id = taxonomy_id;
    }

    @Override
    public String toString() {
        return "AppointmentApiData{" +
                "appointmentApiId='" + appointmentApiId + '\'' +
                ", dateTimeApi='" + dateTimeApi + '\'' +
                ", resource_id='" + resource_id + '\'' +
                ", taxonomy_id='" + taxonomy_id + '\'' +
                '}';
    }
}
