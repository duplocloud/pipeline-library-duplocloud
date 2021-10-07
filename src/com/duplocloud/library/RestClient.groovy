package com.duplocloud.library;

import groovy.json.*

def post(String url, String token, Object body){
  def apiUrl = new URL(url)
  echo "Making a request at: ${apiUrl}"
  def res = null;
  def HttpURLConnection connection;
  try {
    connection = apiUrl.openConnection()
    connection.setRequestProperty("Authorization", "Bearer ${token}")
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)
    connection.connect()
    echo "Request body: ${body}\n"
    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())
    writer.write(body);
    writer.flush();
    stream = new InputStreamReader(connection.getInputStream(),"UTF-8");
    res = stream?.text;
    connection.disconnect()
    echo "HTTP Status: ${connection.responseCode}"
    echo "Recieved Resonse:  ${r1es}"
    return res

  } catch (err) {
     error= connection.getErrorStream()?.text;
     def error = "";
     if(connection){
        echo "HTTP Status:  ${connection?.responseCode}"
        echo "Error:  ${error}"
     }
     throw new Exception(error || "Error while calling API", err);
  }
}


return this;