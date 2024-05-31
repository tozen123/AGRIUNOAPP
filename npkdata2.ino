#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESPAsyncTCP.h>
#include <ESPAsyncWebServer.h>
#include <SoftwareSerial.h>

const char* ssid = "ZTE_2.4G_LW9scU";
const char* password = "X3N4cZW7";

const byte N[] = {0x01, 0x03, 0x00, 0x1e, 0x00, 0x01, 0xE4, 0x0c};
const byte P[] = {0x01, 0x03, 0x00, 0x1f, 0x00, 0x01, 0xb5, 0xcc};
const byte K[] = {0x01, 0x03, 0x00, 0x20, 0x00, 0x01, 0x85, 0xc0};

byte values[11];

SoftwareSerial mod(D6,D7);

#define CONTROL_LED D5
#define LED_BLINK_INTERVAL 5000 // 5 seconds

AsyncWebServer server(80);

uint16_t nitrogenValue = 0;
uint16_t phosphorusValue = 0;
uint16_t potassiumValue = 0;

int calibration_value = 5.8;

unsigned long previousMillis = 0;
bool ledState = LOW;



// HTML content
const char index_html[] PROGMEM = R"rawliteral(
<!DOCTYPE HTML><html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <style>
    html {
     font-family: Arial;
     display: inline-block;
     margin: 0px auto;
     text-align: center;
    }
    h2 { font-size: 3.0rem; }
    p { font-size: 3.0rem; }
    .units { font-size: 1.2rem; }
    .dht-labels{
      font-size: 1.5rem;
      vertical-align:middle;
      padding-bottom: 15px;
    }
  </style>
</head>
<body>
  <h2>Agriuno NPK Device 2</h2>
  <p>
    <span class="reading-labels">Nitrogen</span> 
    <span id="nitrogen">%NITROGEN%</span>
    <sup class="units">ppm</sup>
  </p>
  <p>
    <span class="reading-labels">Phosphorus</span>
    <span id="phosphorus">%PHOSPHORUS%</span>
    <sup class="units">ppm</sup>
  </p>
  <p>
    <span class="reading-labels">Potassium</span>
    <span id="potassium">%POTASSIUM%</span>
    <sup class="units">ppm</sup>
  </p>
</body>
<script>

setInterval(function ( ) {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      document.getElementById("nitrogen").innerHTML = this.responseText;
    }
  };
  xhttp.open("GET", "/nitrogen", true);
  xhttp.send();
}, 1000 ) ;

setInterval(function ( ) {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      document.getElementById("phosphorus").innerHTML = this.responseText;
    }
  };
  xhttp.open("GET", "/phosphorus", true);
  xhttp.send();
}, 1000 ) ;

setInterval(function ( ) {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      document.getElementById("potassium").innerHTML = this.responseText;
    }
  };
  xhttp.open("GET", "/potassium", true);
  xhttp.send();
}, 1000 ) ;
</script>
</html>)rawliteral";


String processor(const String& var){
  if(var == "NITROGEN"){
    return String(nitrogenValue);
  }
  else if(var == "PHOSPHORUS"){
    return String(phosphorusValue);
  }
  else if(var == "POTASSIUM"){
    return String(potassiumValue);
  }
  return String();
}

void setup(){
  Serial.begin(115200);
  mod.begin(9600);
  
  
  pinMode(CONTROL_LED, OUTPUT);
  
  digitalWrite(CONTROL_LED, HIGH);
  

  Serial.print("Connecting to WiFi…");
  
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    digitalWrite(CONTROL_LED, LOW);
    delay(100);
    digitalWrite(CONTROL_LED, HIGH);
    delay(100);
    
    Serial.print(".");
  }
  Serial.println("");
  Serial.println(WiFi.status());

  
  Serial.println("");
  Serial.print("Connected to WiFi. IP address: ");
  Serial.println(WiFi.localIP());
server.on("/deviceName", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send_P(200, "text/plain", "Agriuno NPK Device 2");
});
server.on("/deviceIP", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send_P(200, "text/plain", "192.168.1.251");
});
  server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send_P(200, "text/html", index_html, processor);
  });
  server.on("/nitrogen", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send_P(200, "text/plain", String(nitrogenValue).c_str());
  });

  server.on("/phosphorus", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send_P(200, "text/plain", String(phosphorusValue).c_str());
  });

  server.on("/potassium", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send_P(200, "text/plain", String(potassiumValue).c_str());
  });

  server.on("/readNow", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send(200, "text/plain", "N: " + String(nitrogenValue) + ", P: " + String(phosphorusValue) + ", K: " + String(potassiumValue));
  });

  server.begin();
  Serial.println("HTTP server started");
}

void loop(){
  nitrogenValue = getValue(N, sizeof(N));
  phosphorusValue = getValue(P, sizeof(P));
  potassiumValue = getValue(K, sizeof(K));

  Serial.println(nitrogenValue);
  Serial.println(phosphorusValue);
  Serial.println(potassiumValue);

  if (WiFi.status() == WL_CONNECTED) {
      digitalWrite(CONTROL_LED, LOW);
      delay(1000);
      digitalWrite(CONTROL_LED, HIGH);
      delay(1000);
  } 
  
  delay(500);
}

uint16_t getValue(const byte* inArray, size_t size) {
  delay(50);
  
  if (mod.write(inArray, size) == 8) {
    for (byte i = 0; i < 7; i++) {
      values[i] = mod.read();
      Serial.print(values[i], HEX);
    }
    Serial.println();
  }
  uint16_t rValue = (values[3] << 8) | values[4];
  if (rValue == 65535) {
    return 0;
  }
  else if (rValue > 255) {
    return 0;
  }
  return rValue;
//  uint16_t rValue = (values[3] << 8) | values[4];
//  if (rValue == 65535) {
//    return (rValue  + calibration_value * random(1, 16)) * random(0, 3);
//  }
//  else if (rValue > 255) {
//    return (rValue  + calibration_value * random(1, 16)) * random(0, 3);
//  }
//  return rValue;
}
