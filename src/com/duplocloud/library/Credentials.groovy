import jenkins.*
import jenkins.model.* 
import hudson.*
import hudson.model.*
def jenkinsCredentials = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
        com.cloudbees.plugins.credentials.Credentials.class,
        Jenkins.instance,
        null,
        null
);

def getCredential(id){
    for (creds in jenkinsCredentials) {
        println new JsonBuilder( creds ).toPrettyString() 
        if(creds.id == id){
            return creds
        }
    }
}
return this;
