#!/usr/bin/env groovy
import com.duplocloud.library.*;
import groovy.json.*

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
       return """
        {
            "Image": "${image}", 
            "Replicas": "${replicas}", 
            "Name": "${name}",
           
        }
       """
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
    assert input.tenant  : "Param 'tenant' should be defined."
    assert input.token  : "Param 'token' should be defined."
    assert input.duploUrl  : "Param 'duploUrl' should be defined."
    assert input.service  : "Param 'service' should be defined."
    assert input.service.name : "Param 'service.name' should be defined."
    assert input.service.image : "Param 'service.image' should be defined."


    creds = credentials('duplo-token')
    

    def flow = new com.duplocloud.library.RestClient()
    def credsProvider = new com.duplocloud.library.Credentials();
    credsProvider.getCredential("duplo-token")

    def body =input.service.toBody();

    def sTenants = flow.get("${input.duploUrl}/adminproxy/GetTenantNames",input.token);
    def jsonSlurper = new JsonSlurper()
    def tenants = jsonSlurper.parseText(sTenants);
    def tenant = tenants.find { t -> t.AccountName == input.tenant } ;
    assert tenant: "No tenant with name ${input.tenant}"
    echo "Found tenant: ${tenant}"


    def res = flow.post(input.duploUrl + "/subscriptions/${tenant.TenantId}/ReplicationControllerChange", input.token, body);

    assert res : "Error while calling Duplo Portal API"

    return res
}