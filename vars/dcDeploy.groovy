#!/usr/bin/env groovy

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
    echo "Service Name: ${input.tenant}"
    assert input.tenant  : "Param 'tenant' should be defined."
    assert input.token  : "Param 'token' should be defined."
    assert input.duploUrl  : "Param 'duploUrl' should be defined."
    assert input.service  : "Param 'service' should be defined."
    assert input.service.name : "Param 'service.name' should be defined."
    assert input.service.image : "Param 'service.image' should be defined."
    return input.service
}