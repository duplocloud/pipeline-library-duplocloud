import groovy.json.*

def post(String url, String token, Object body){
  def apiUrl = new URL(url)
  echo "Making a request at: ${apiUrl}"
  try {
    def HttpURLConnection connection = apiUrl.openConnection()
    connection.setRequestProperty("Authorization", "Bearer ${token}")
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)
    connection.connect()

    body =  new JsonBuilder( body).toPrettyString()

    echo "Request body: ${body}\n"

    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())
    writer.write(body);
    writer.flush();

    assert postConnection.responseCode == 200


    // execute the POST request
    def rs = new JsonSlurper().parse(new InputStreamReader(connection.getInputStream(),"UTF-8"))

    responseCode 

    connection.disconnect()
   
    echo "Recieved Resonse:  ${rs}"
    return res

  } catch (err) {
     echo "ERROR  ${err}"
     return
  }
}