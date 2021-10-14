#!/usr/bin/env groovy
import com.duplocloud.library.*;
import groovy.json.*

class ReplicationController implements Serializable {                   
   String name; 
   String image;
   Integer replicas; 
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
            "Name": "${name}",
            "AgentPlatform" : "${agentPlatform}"
           
        }
       """
   }

}

class ServiceUpdateInput implements Serializable   {
    String tenant;
    String token;
    String duploToken;
    String duploUrl;
    String tokenId;
    ReplicationController service;
}

def call(Map input) {
    call(new ServiceUpdateInput(input))
}

def call(ServiceUpdateInput input) {
    assert input.tenant  : "Param 'tenant' should be defined."
    assert input.service  : "Param 'service' should be defined."
    assert input.service.name : "Param 'service.name' should be defined."
    assert input.service.image : "Param 'service.image' should be defined."


    creds = credentials('duplo-token')
    
    def duploToken = input.token;
    def duploURL = input.duploUrl;

    def flow = new com.duplocloud.library.RestClient()
    def credsProvider = new com.duplocloud.library.Credentials();
    if(!duploToken){
          def secretId = input.tokenId || "duplo-token"
          def token = credsProvider.getCredential(secretId)
          assert token: "Duplo token Secret not found witj id ${secretId}";

          def jsonSlurper = new JsonSlurper()
          def tokenSecret = jsonSlurper.parseText(token.getSecret().getPlainText());
          duploToken = tokenSecret["token"]
          duploUrl = tokenSecret["url"]
    }
    assert duploToken: "Duplo token not found in secret and even not passed by the caller"
    assert duploUrl: "Duplo url not found in secret and even not passed by the caller"

    def body =input.service.toBody();

    //Fecthing tenant id from the tenant name
    def sTenants = flow.get("${duploUrl}/adminproxy/GetTenantNames", duploToken);
    def jsonSlurper = new JsonSlurper()
    def tenants = jsonSlurper.parseText(sTenants);
    def tenant = tenants.find { t -> t.AccountName == input.tenant } ;
    assert tenant: "No tenant with name ${input.tenant}"
    echo "Found tenant: ${tenant}"


    //Updating service
    def res = flow.post(duploUrl + "/subscriptions/${tenant.TenantId}/ReplicationControllerChange", duploToken, body);
    assert res : "Error while calling Duplo Portal API"
    return res
}
