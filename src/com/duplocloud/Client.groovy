package com.duplocloud;

import com.duplocloud.library.*;
import groovy.json.*

class Client {
  def baseUrl
  def token
  def restClient

  Client(String credentialsId = "duplo-token") {
    this.restClient = new com.duplocloud.library.RestClient();

    // Find the credentials.
    def credsProvider = new com.duplocloud.library.Credentials();
    def token = credsProvider.getCredential(credentialsId)
    assert token: "Duplo token Secret not found with id ${credentialsId}";

    // Parse the credentials.
    def jsonSlurper = new JsonSlurper()
    def tokenSecret = jsonSlurper.parseText(token.getSecret().getPlainText());
    this.token = tokenSecret["token"]
    this.baseUrl = tokenSecret["url"]
    assert this.token: "Credentials ${credentialsId}: token: missing JSON key"
    assert this.baseUrl: "Credentials ${credentialsId}: url: missing JSON key"

    // Ensure we have an ending slash.
    if (! this.baseUrl.endsWith("/")) {
      this.baseUrl = "${this.baseUrl}/"
    }
  }

  private doGet(String path) {
    def response = this.restClient.get("${this.baseUrl}${path}", this.token);
    def jsonSlurper = new JsonSlurper()
    return jsonSlurper.parseText(response);
  }

  public listTenants() {
    return this.doGet("adminproxy/GetTenantsForUser")
  }

  public getTenant(String id) {
    return this.listTenants().find { t -> t.TenantId == id }
  }

  public getTenantByName(String name) {
    return this.listTenants().find { t -> t.AccountName == name }
  }
}
