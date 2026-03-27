package com.payment_service.dto;

import java.util.List;

public class PlanDetails {
    private String planName;
    private Integer durationMonths;
    private String accessLevel;
    private List<String> includedPerks;

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
    public List<String> getIncludedPerks() { return includedPerks; }
    public void setIncludedPerks(List<String> includedPerks) { this.includedPerks = includedPerks; }
}