package com.duplocloud.library;

import jenkins.*
import jenkins.model.* 
import hudson.*
import hudson.model.*
import groovy.json.*

def jenkinsCredentials = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
        com.cloudbees.plugins.credentials.Credentials.class,
        Jenkins.instance,
        null,
        null
    );

def getCredential(String id){
    

    for (creds in jenkinsCredentials) {
        //println new JsonBuilder( creds ).toPrettyString() 
        if(creds.id == id){
            return creds
        }
    }
}


return this;
