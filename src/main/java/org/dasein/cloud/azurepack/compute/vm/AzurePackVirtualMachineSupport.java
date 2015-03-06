package org.dasein.cloud.azurepack.compute.vm;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.azurepack.AzurePackCloud;
import org.dasein.cloud.azurepack.compute.vm.model.WAPVirtualMachineModel;
import org.dasein.cloud.azurepack.compute.vm.model.WAPVirtualMachinesModel;
import org.dasein.cloud.azurepack.utils.AzurePackRequester;
import org.dasein.cloud.compute.*;
import org.dasein.cloud.dc.DataCenter;
import org.dasein.cloud.util.requester.DriverToCoreMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmunthiu on 3/4/2015.
 */
public class AzurePackVirtualMachineSupport extends AbstractVMSupport {
    private AzurePackCloud provider;

    public AzurePackVirtualMachineSupport(@Nonnull AzurePackCloud provider) {
        super(provider);
        this.provider = provider; }
    @Nonnull
    @Override
    public VirtualMachineCapabilities getCapabilities() throws InternalException, CloudException {
        return new AzurePackVirtualMachineCapabilities(provider);
    }

    @Override
    public boolean isSubscribed() throws CloudException, InternalException {
        return false;
    }

    @Nonnull
    @Override
    public VirtualMachine launch(@Nonnull VMLaunchOptions withLaunchOptions) throws CloudException, InternalException {
        WAPVirtualMachineModel virtualMachineModel = new WAPVirtualMachineModel();
        virtualMachineModel.setName(withLaunchOptions.getFriendlyName());
        virtualMachineModel.setVirtualHardDiskId(withLaunchOptions.getMachineImageId());
        virtualMachineModel.setCloudId(provider.getContext().getRegionId());
        virtualMachineModel.setStampId(withLaunchOptions.getDataCenterId());

        HttpUriRequest createRequest = new AzurePackVMRequests(provider).createVirtualMachine(virtualMachineModel).build();
        return new AzurePackRequester(provider, createRequest).withJsonProcessor(new DriverToCoreMapper<WAPVirtualMachineModel, VirtualMachine>() {
            @Override
            public VirtualMachine mapFrom(WAPVirtualMachineModel entity) {
                return virtualMachineFrom(entity);
            }
        }, WAPVirtualMachineModel.class).execute();
    }

    @Override
    public void stop( @Nonnull String vmId, boolean force ) throws InternalException, CloudException {
        updateVMState(vmId, "Shutdown");
    }

    @Override
    public void start( @Nonnull String vmId ) throws InternalException, CloudException {
        updateVMState(vmId, "Start");
    }

    @Override
    public void terminate(@Nonnull String vmId, String explanation) throws InternalException, CloudException {
        VirtualMachine virtualMachine = getVirtualMachine(vmId);
        if(virtualMachine == null)
            throw new CloudException("Virtual machine does not exisit");

        HttpUriRequest deleteRequest = new AzurePackVMRequests(provider).deleteVirtualMachine(virtualMachine.getProviderVirtualMachineId(), virtualMachine.getProviderDataCenterId()).build();
        new AzurePackRequester(provider, deleteRequest).execute();
    }

    @Override
    public @Nullable VirtualMachine getVirtualMachine( @Nonnull String vmId ) throws InternalException, CloudException {
        if(vmId == null)
            throw new InternalException("Invalid virtual machine id.");

        List<DataCenter> dataCenters = new ArrayList(this.provider.getDataCenterServices().listDataCenters(this.provider.getContext().getRegionId()));

        HttpUriRequest getVMRequest = new AzurePackVMRequests(provider).getVirtualMachine(vmId, dataCenters.get(0).getProviderDataCenterId()).build();

        return new AzurePackRequester(this.provider, getVMRequest).withJsonProcessor(new DriverToCoreMapper<WAPVirtualMachineModel, VirtualMachine>() {
            @Override
            public VirtualMachine mapFrom(WAPVirtualMachineModel entity) {
                return virtualMachineFrom(entity);
            }
        },WAPVirtualMachineModel.class).execute();
    }

    @Override
    public @Nonnull Iterable<VirtualMachine> listVirtualMachines() throws InternalException, CloudException {
        HttpUriRequest listVMRequest = new AzurePackVMRequests(provider).listVirtualMachines().build();
        WAPVirtualMachinesModel virtualMachinesModel = new AzurePackRequester(this.provider, listVMRequest).withJsonProcessor(WAPVirtualMachinesModel.class).execute();

        final List<VirtualMachine> virtualMachines = new ArrayList<VirtualMachine>();

        CollectionUtils.forAllDo(virtualMachinesModel.getVirtualMachines(), new Closure() {
            @Override
            public void execute(Object input) {
                WAPVirtualMachineModel virtualMachineModel = (WAPVirtualMachineModel) input;
                virtualMachines.add(virtualMachineFrom(virtualMachineModel));
            }
        });

        return virtualMachines;
    }

    private VirtualMachine virtualMachineFrom(WAPVirtualMachineModel virtualMachineModel){
        VirtualMachine virtualMachine = new VirtualMachine();
        virtualMachine.setProviderVirtualMachineId(virtualMachineModel.getID());
        virtualMachine.setProviderRegionId(virtualMachineModel.getCloudId());
        virtualMachine.setProviderDataCenterId(virtualMachineModel.getStampId());
        virtualMachine.setName(virtualMachineModel.getName());

        return virtualMachine;
    }

    private void updateVMState(String vmId, String operation) throws InternalException, CloudException {
        List<DataCenter> dataCenters = new ArrayList(this.provider.getDataCenterServices().listDataCenters(this.provider.getContext().getRegionId()));
        String dataCenterId = dataCenters.get(0).getProviderDataCenterId();

        HttpUriRequest getVMRequest = new AzurePackVMRequests(provider).getVirtualMachine(vmId, dataCenterId).build();

        WAPVirtualMachineModel virtualMachineModel = new AzurePackRequester(this.provider, getVMRequest).withJsonProcessor(WAPVirtualMachineModel.class).execute();
        virtualMachineModel.setOperation(operation);

        HttpUriRequest updateVMRequest = new AzurePackVMRequests(provider).updateVirtualMachine(vmId, dataCenterId, virtualMachineModel).build();
        new AzurePackRequester(provider, updateVMRequest).execute();
    }
}