package com.duplocloud;

import com.duplocloud.library.*;
import groovy.json.*

class Client {
  def baseUrl
  def token
  def restClient

  Client(String baseUrl, String token) {
    this.restClient = new com.duplocloud.library.RestClient()
    this.baseUrl = baseUrl
    this.token = token

    // Ensure we have an ending slash.
    if (! this.baseUrl.endsWith("/")) {
      this.baseUrl = "${this.baseUrl}/"
    }
  }

  public static Client getInstance(String credentialsId = "duplo-token") {

    // Find the credentials.
    def credsProvider = new com.duplocloud.library.Credentials();
    def creds = credsProvider.getCredential(credentialsId)
    assert creds: "Duplo credentials not found with id ${credentialsId}";

    // Parse the credentials.
    def jsonSlurper = new JsonSlurper()
    def credsJson = jsonSlurper.parseText(creds.getSecret().getPlainText());
    def token = credsJson["token"]
    def baseUrl = credsJson["url"]
    assert token: "Credentials ${credentialsId}: token: missing JSON key"
    assert baseUrl: "Credentials ${credentialsId}: url: missing JSON key"

    // Ensure we have an ending slash.
    if (! baseUrl.endsWith("/")) {
      baseUrl = "${this.baseUrl}/"
    }

    return new Client(baseUrl, token)
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
