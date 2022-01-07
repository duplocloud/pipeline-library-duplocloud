package com.duplocloud;

import com.duplocloud.library.*
import groovy.json.*

class Client {
  def baseUrl
  def token
  def restClient

  Client(String baseUrl, String token) {
    this.baseUrl = baseUrl
    this.token = token

    // Ensure we have an ending slash.
    if (! this.baseUrl.endsWith("/")) {
      this.baseUrl = "${this.baseUrl}/"
    }
  }

  public static Client getInstance(String credentialsId = "duplo-token") {

    // Find the credentials.
    def credsProvider = new com.duplocloud.library.Credentials()
    def creds = credsProvider.getCredential(credentialsId)
    assert creds: "Duplo credentials not found with id ${credentialsId}"

    // Parse the credentials.
    //
    // NOTE: To make sure the result is serializable, we convert it to a hash map
    // - https://stackoverflow.com/questions/37864542/jenkins-pipeline-notserializableexception-groovy-json-internal-lazymap
    //
    def jsonSlurper = new JsonSlurper()
    def credsJson = new HashMap(jsonSlurper.parseText(creds.getSecret().getPlainText()));
    def token = credsJson["token"]
    def baseUrl = credsJson["url"]
    assert token: "Credentials ${credentialsId}: token: missing JSON key"
    assert baseUrl: "Credentials ${credentialsId}: url: missing JSON key"

    return new Client(baseUrl, token)
  }

  private client() {
    return new com.duplocloud.library.RestClient()
  }

  private doGet(String path) {
    def response = this.client().get("${this.baseUrl}${path}", this.token)

    // NOTE: To make sure the result is serializable, we use the JsonSlurperClassic (we don't know if this is map or a list)
    // - https://stackoverflow.com/questions/37864542/jenkins-pipeline-notserializableexception-groovy-json-internal-lazymap
    def jsonSlurper = new JsonSlurperClassic()
    def result = jsonSlurper.parseText(response);
    return result
  }

  public listTenants() {
    return this.doGet("admin/GetTenantsForUser")
  }

  public getTenant(String id) {
    return this.listTenants().find { t -> t.TenantId == id }
  }

  public getTenantByName(String name) {
    return this.listTenants().find { t -> t.AccountName == name }
  }
}
