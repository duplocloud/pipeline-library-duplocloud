package com.duplocloud.library;

import jenkins.*
import jenkins.model.* 
import hudson.*
import hudson.model.*
import groovy.json.*
import com.cloudbees.plugins.credentials.*

class Credentials {
  public Credentials getCredential(String id) {
    def jenkinsCredentials = CredentialsProvider.lookupCredentials(
        Credentials.class,
        Jenkins.instance,
        null,
        null
    );

    for (creds in jenkinsCredentials) {
        if(creds.id == id){
            return creds
        }
    }
}
