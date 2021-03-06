package org.dasein.cloud.azurepack.network.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vmunthiu on 3/26/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WAPSubnetModel {
    @JsonProperty("odata.type")
    private String odataType = "VMM.VMSubnet";
    @JsonProperty("odata.metadata")
    private String odataMetadata = null;
    @JsonProperty("ID")
    private String id = "00000000-0000-0000-0000-000000000000";
    @JsonProperty("Name")
    private String name = null;
    @JsonProperty("StampId")
    private String stampId = null;
    @JsonProperty("Subnet")
    private String subnet = null;
    @JsonProperty("VMNetworkId")
    private String vmNetworkId = null;
    @JsonProperty("VMNetworkName")
    private String vmNetworkName = null;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStampId() {
        return stampId;
    }

    public void setStampId(String stampId) {
        this.stampId = stampId;
    }

    public String getSubnet() {
        return subnet;
    }

    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }

    public String getVmNetworkId() {
        return vmNetworkId;
    }

    public void setVmNetworkId(String vmNetworkId) {
        this.vmNetworkId = vmNetworkId;
    }

    public String getVmNetworkName() {
        return vmNetworkName;
    }

    public void setVmNetworkName(String vmNetworkName) {
        this.vmNetworkName = vmNetworkName;
    }
}
