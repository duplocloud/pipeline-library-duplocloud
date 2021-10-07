#!/usr/bin/env groovy
import com.duplocloud.library.*;

class ReplicationController implements Serializable {                   
   String name; 
   String image;
   int replicas; 
   Boolean replicaCollocationAllowed = false;
   Boolean lBSyncedDeployment = false;
   String asgName; 
   int agentPlatform = 0;
   String allocationTags;
   Boolean daemonset  = false;

   def toBody(){
       return ''' 
        {
            "image": "${name}", 
            "Replicas": "${replicas}", 
            "Name": "${name}"},
            "ReplicaCollocationAllowed": "${replicaCollocationAllowed}",
            "LBSyncedDeployment": "${lBSyncedDeployment}",
            "ReplicasMatchingAsgName": "${asgName}",
            "AgentPlatform": "${agentPlatform}",
            "AllocationTags": "${allocationTags}",
            "Daemonset": "${daemonset}"
        }
       '''
   }

}

class ServiceUpdateInput implements Serializable   {
    String tenant;
    String token;
    String duploUrl;
    ReplicationController service;
}

def call(Map input) {
    call(new ServiceUpdateInput(input))
}

def call(ServiceUpdateInput input) {
    echo "Tenant Name: ${input.tenant}"
    assert input.tenant  : "Param 'tenant' should be defined."
    assert input.token  : "Param 'token' should be defined."
    assert input.duploUrl  : "Param 'duploUrl' should be defined."
    assert input.service  : "Param 'service' should be defined."
    assert input.service.name : "Param 'service.name' should be defined."
    assert input.service.image : "Param 'service.image' should be defined."

    def flow = new com.duplocloud.library.RestClient()

    def body =input.service.toBody();

    res = flow.post(input.duploUrl + "subscriptions/${input.tenant}/ReplicationControllerChange", input.token, body);

    assert res : "Error while calling Duplo Portal API"

     echo "Duplo API Response: ${res}"

    return res
}