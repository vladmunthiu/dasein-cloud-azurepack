package org.dasein.cloud.azurepack.network.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vmunthiu on 4/1/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WAPLogicalNetModel {
    @JsonProperty("odata.type")
    private String odataType = "VMM.LogicalNetwork";
    @JsonProperty("odata.metadata")
    private String odataMetadata = null;
    @JsonProperty("ID")
    private String id = "00000000-0000-0000-0000-000000000000";
    @JsonProperty("StampId")
    private String stampId;
    @JsonProperty("CloudId")
    private String cloudId;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("NetworkVirtualizationEnabled")
    private String networkVirtualizationEnabled;

    public String getOdataType() {
        return odataType;
    }

    public void setOdataType(String odataType) {
        this.odataType = odataType;
    }

    public String getOdataMetadata() {
        return odataMetadata;
    }

    public void setOdataMetadata(String odataMetadata) {
        this.odataMetadata = odataMetadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStampId() {
        return stampId;
    }

    public void setStampId(String stampId) {
        this.stampId = stampId;
    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetworkVirtualizationEnabled() {
        return networkVirtualizationEnabled;
    }

    public void setNetworkVirtualizationEnabled(String networkVirtualizationEnabled) {
        this.networkVirtualizationEnabled = networkVirtualizationEnabled;
    }
}
