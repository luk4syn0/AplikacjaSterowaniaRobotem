#include "BluetoothSerial.h" 
#include <dht11.h>
#include <string.h>

BluetoothSerial ESP_BT; 

/* Piny dla sterownika silnikow */
#define IA1   19            //PWM
#define IA2   21            //DIGITAL
#define IB1   22            //PWM
#define IB2   23            //DIGITAL

/* Kanały PWM */
#define PWMA_Ch    0
#define PWMA_Res   8
#define PWMA_Freq  1000
#define PWMB_Ch    1
#define PWMB_Res   8
#define PWMB_Freq  1000

/* Piny dla czujnikow odleglosci */
#define trigPin1 32 //Przedni czujnik
#define echoPin1 34 
#define trigPin2 33 //Czujnik dolny
#define echoPin2 35

/*Pin dla czujnika temperatury*/
#define DHT11PIN 25
 
int DATA;
int sterowanie;
int swiatla;
float OdlegloscDol = 100.00;
float OdlegloscPrzod = 10.00;
float OdlegloscPrzodu; //Nie rozumiem dlaczego ale tak musi być :)
dht11 DHT11;

void setup()
{
  // Bluetooth i konsola
  Serial.begin(9600);
  delay(500);
  ESP_BT.begin("ESP32_Samochodzik");
  delay(500);


  // Silniki
  pinMode(IA1, OUTPUT);
  pinMode(IA2, OUTPUT);
  pinMode(IB1, OUTPUT);
  pinMode(IB2, OUTPUT);

  ledcAttachPin(IA1, PWMA_Ch);
  ledcSetup(PWMA_Ch, PWMA_Freq, PWMA_Res);
  ledcAttachPin(IB1, PWMB_Ch);
  ledcSetup(PWMB_Ch, PWMB_Freq, PWMB_Res);

  // Sonary
  pinMode(trigPin1, OUTPUT); //Pin, do którego podłączymy trig jako wyjście
  pinMode(echoPin1, INPUT); //a echo, jako wejście
  pinMode(trigPin2, OUTPUT); //Pin, do którego podłączymy trig jako wyjście
  pinMode(echoPin2, INPUT); //a echo, jako wejście


}
 
void loop()
{
  // Jeżeli w outputStream dla BT znajdzie się pakiet (4 bajty) to znajdz początek i wykonaj przypisanie
  if (ESP_BT.available() >= 4) 
  {
    bool okejka = false;
    while (!okejka) {
      DATA = ESP_BT.read();
      // Znak " ` "
      if (DATA = 96) 
      {
        sterowanie = ESP_BT.read();
        swiatla = ESP_BT.read();
        ESP_BT.read();
        okejka = true;
      }      
    }
  }
    
    //
    // Serial.println(DATA);
    // Serial.println(sterowanie);
    // Serial.println(swiatla);
    // Serial.println(sterowanie + " " + swiatla);
    delay(5);
  Serial.println(OdlegloscDol);
  Serial.println(OdlegloscPrzodu);
  if ((OdlegloscDol <= 10) && (OdlegloscPrzodu >= 25))
  {
    switch (sterowanie) {
    case 48:
      Freeze();
      break;
    case 49: 
      Jazda_na_wprost();
      break;

    case 50:  
      Jazda_na_wprost_lewo();
      break;

    case 51:  
      Jazda_na_wprost_prawo();
      break;

    case 52:
      Obrot_lewo();
      break;
    case 53:
      Obrot_prawo();
      break;
    case 54:
      Jazda_do_tylu_lewo();
      break;
    case 55:
      Jazda_do_tylu_prawo();
      break; 
    case 56:
      Jazda_do_tylu();
      break;
    }
  }
  else
  {
    Serial.println("Niebezpiecznstwo");
    Freeze();
  }
  


  if (!ESP_BT.connected())
  {
    Freeze();
  }
  // delay(50);

  int chk = DHT11.read(DHT11PIN);

  // Serial.print("Read sensor: ");
  switch (chk)
  {
    case DHTLIB_OK: 
      // Serial.println("OK"); 
      break;
    case DHTLIB_ERROR_CHECKSUM: 
      // Serial.println("Checksum error"); 
      break;
    case DHTLIB_ERROR_TIMEOUT: 
      // Serial.println("Time out error"); 
      break;
    default: 
      // Serial.println("Unknown error"); 
      break;
  }
  
  float Wilgotnosc = (float)DHT11.humidity;
  // Serial.print("Humidity (%): ");
  // Serial.println(Wilgotnosc, 2);

  float Temperatura  = (float)DHT11.temperature;
  // Serial.print("Temperature (°C): ");
  // Serial.println(Temperatura, 2);

  // delay(1000);

  float OdlegloscPrzod = zmierzOdleglosc(trigPin1, echoPin1);
  
  Serial.println(OdlegloscPrzod);
  OdlegloscPrzodu = OdlegloscPrzod;
  
  // Serial.println("OdlegloscPrzod");
  // Serial.println(OdlegloscPrzod);
  if (OdlegloscPrzod >= 200)
  {
    OdlegloscPrzod = 200;
  }
  
  OdlegloscDol = zmierzOdleglosc(trigPin2, echoPin2);
  // Serial.println("OdlegloscTyl");
  // Serial.println(OdlegloscTyl);
  if (OdlegloscDol >= 200)
  {
    OdlegloscDol = 200;
  }
  if ((OdlegloscDol >= 10) || (OdlegloscPrzod <= 25))
  {
    Freeze();
  }

  char PakietWysylka[32];


  sprintf(PakietWysylka, "`%.2f|%.2f|%.2f|%.2f~", OdlegloscPrzod,OdlegloscDol,Wilgotnosc,Temperatura);

 
  Serial.println("Pakiet");
  // Serial.println(PakietWysylka);
  
  ESP_BT.println(PakietWysylka);

  delay(5); 
}

/* Zrewitalizowane funkcje strujące poruszaniem się robota */
void Freeze()
{
  //Zero z prawej
  ledcWrite(PWMA_Ch,0);
  digitalWrite(IA2,LOW);
  //Zero z lewej
  ledcWrite(PWMB_Ch,0);
  digitalWrite(IB2,LOW);
}
void Jazda_na_wprost()
{
  //Prawa strona do przodu
  ledcWrite(PWMA_Ch,0);
  digitalWrite(IA2,HIGH);

  //Lewa strona do przodu
  ledcWrite(PWMB_Ch,255);
  digitalWrite(IB2,LOW);
}

void Jazda_do_tylu()
{
  //Prawa strona do tyłu
  ledcWrite(PWMA_Ch,255);
  digitalWrite(IA2,LOW);

  //Lewa strona do tyłu
  ledcWrite(PWMB_Ch,0);
  digitalWrite(IB2,HIGH);
}

void Jazda_na_wprost_lewo()
{
  //Prawa strona do przodu
  ledcWrite(PWMA_Ch,0);
  digitalWrite(IA2,HIGH);

  //Lewa strona do przodu ale lekko *******
  ledcWrite(PWMB_Ch,46);
  digitalWrite(IB2,LOW);

}

void Jazda_na_wprost_prawo()
{
  //Prawa strona do przodu ale lekko ********
  ledcWrite(PWMA_Ch,190);
  digitalWrite(IA2,HIGH);



  //Lewa strona do przodu
  ledcWrite(PWMB_Ch,255);
  digitalWrite(IB2,LOW);
}

void Jazda_do_tylu_prawo()
{
  //Lewa strona do tyłu
  ledcWrite(PWMB_Ch,0);
  digitalWrite(IB2,HIGH);

  //Prawa strona do tyłu ale lekko *******
  ledcWrite(PWMA_Ch,46);
  digitalWrite(IA2,LOW);
}

void Jazda_do_tylu_lewo()
{
  //Prawa strona do tyłu
  ledcWrite(PWMA_Ch,255);
  digitalWrite(IA2,LOW);

  //Lewa strona do tyłu ale lekko *******
  ledcWrite(PWMB_Ch,190);
  digitalWrite(IB2,HIGH);
}
//Grubasek się już nie obraca w miejscu, mało mocy :(
void Obrot_lewo()
{
  //Prawa strona do przodu
  ledcWrite(PWMA_Ch,0);
  digitalWrite(IA2,HIGH);

  //Lewa strona do tyłu
  ledcWrite(PWMB_Ch,0);
  digitalWrite(IB2,HIGH);
}

void Obrot_prawo()
{
  //Lewa strona do przodu
  ledcWrite(PWMB_Ch,255);
  digitalWrite(IB2,LOW);

  //Prawa strona do tyłu
  ledcWrite(PWMA_Ch,255);
  digitalWrite(IA2,LOW);
}


int zmierzOdleglosc(int trigPin, int echoPin) {
  long czas, dystans;
 
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
 
  czas = pulseIn(echoPin, HIGH);
  dystans = czas / 58;
 
  return dystans;
}