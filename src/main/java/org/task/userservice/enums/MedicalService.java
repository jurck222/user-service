package org.task.userservice.enums;

public enum MedicalService {
    GENERAL_CHECKUP("General checkup"),
    DENTAL_CLEANING("Dental cleaning"),
    CARDIOLOGY_CONSULTATION("Cardiology consultation"),
    DERMATOLOGY_CONSULTATION("Dermatology consultation"),;

    private String value;
    MedicalService(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public static MedicalService fromString(String text) {
        for (MedicalService service : MedicalService.values()) {
            if (service.value.equalsIgnoreCase(text)) {
                return service;
            }
        }
        return null;
    }
}

