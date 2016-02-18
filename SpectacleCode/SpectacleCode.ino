#include <SoftwareSerial.h>

#define tx0Pin 1

#define rx0Pin 0

SoftwareSerial bluetooth = SoftwareSerial(rx0Pin,tx0Pin);


int groundSonarpin = 7;   //Frist sonar
int leftSonarpin = 6;     //Left sonar
int rightSonarpin = 5;    //Right sonar

int arraysize = 9;
int rangevalue[]={0,0,0,0,0,0,0,0,0};  //buffer for groundSonar
int Lrangevalue[]={0,0,0,0,0,0,0,0,0};  //buffer for LeftSonar
int Rrangevalue[]={0,0,0,0,0,0,0,0,0};  //buffer for RightSonar
int modE,LmodE,RmodE;

void setup() {
  bluetooth.begin(9600);
  pinMode(groundSonarpin, INPUT);
  pinMode(leftSonarpin, INPUT);
  pinMode(rightSonarpin, INPUT);
  pinMode(3,OUTPUT);
  pinMode(4,OUTPUT);
  bluetooth.println("Lets go........");
  
}

void loop()
{
   
int pulse,Lpulse,Rpulse;
int i=0,j=0,r=0;

  /*while( i < arraysize )*/for(i=0;i < arraysize;){            
    pulse = pulseIn(groundSonarpin, HIGH);  // read in time for pin to transition
    //Lpulse = pulseIn(leftSonarpin, HIGH);  // read in time for pin to transition
    rangevalue[i]=pulse/58;         // pulses to centimeters (use 147 for inches)
    //Lrangevalue[i]=Lpulse/58;         // pulses to centimeters (use 147 for inches)
    if( rangevalue[i] < 645 && rangevalue[i] >= 15) i++;  // ensure no values out of range
    //i++;
    delay(10); 
    // wait between samples
  }
/*while( j < arraysize )*/for(j=0;j < arraysize;){            
    Lpulse = pulseIn(leftSonarpin, HIGH);  // read in time for pin to transition
    Lrangevalue[j]=Lpulse/58;         // pulses to centimeters (use 147 for inches)
    if( Lrangevalue[j] < 645 && Lrangevalue[j] >= 15) j++;  // ensure no values out of range
    //j++;
    delay(10); 
    // wait between samples
  }
  /*while( r < arraysize )*/for(r=0;r < arraysize;){            
    Rpulse = pulseIn(rightSonarpin, HIGH);  // read in time for pin to transition
    Rrangevalue[r]=Rpulse/58;         // pulses to centimeters (use 147 for inches)
    if( Rrangevalue[r] < 645 && Rrangevalue[r] >= 15) r++;  // ensure no values out of range
    //r++;
    delay(10); 
    // wait between samples
  }
  isort(rangevalue,arraysize);        // sort samples
  isort(Lrangevalue,arraysize);        // sort samples
  isort(Rrangevalue,arraysize);        // sort samples
  modE = mode(rangevalue,arraysize);  // get median
  LmodE = mode(Lrangevalue,arraysize);  // get median
  RmodE = mode(Rrangevalue,arraysize);  // get median
  
  if(modE!=0 && modE<=60){
    //bluetooth.println(modE);
  bluetooth.println("Ground");
  delay(1500);
  } 
if(LmodE!=0 && LmodE<=100){
  // bluetooth.println(LmodE);
  bluetooth.println("  Left");
  digitalWrite(3,HIGH);
  delay(1500);
  digitalWrite(3,LOW);
  }
  if(RmodE!=0 && RmodE<=100){
     //bluetooth.println(RmodE);
  bluetooth.println("  Right");
  
  digitalWrite(4,HIGH);
  delay(1000);
  digitalWrite(4,LOW);
  }
}

// Sorting function (Author: Bill Gentles, Nov. 12, 2010)
void isort(int *a, int n){
  for (int i = 1; i < n; ++i)  {
    int j = a[i];
    int k;
    for (k = i - 1; (k >= 0) && (j < a[k]); k--) {
      a[k + 1] = a[k];
    }
    a[k + 1] = j;
  }
}

// Mode function, returning the mode or median.
int mode(int *x,int n){
  int i = 0;
  int count = 0;
  int maxCount = 0;
  int mode = 0;
  int bimodal;
  int prevCount = 0;
  while(i<(n-1)){
    prevCount=count;
    count=0;
    while( x[i]==x[i+1] ) {
      count++;
      i++;
    }
    if( count > prevCount & count > maxCount) {
      mode=x[i];
      maxCount=count;
      bimodal=0;
    }
    if( count == 0 ) {
      i++;
    }
    if( count == maxCount ) {      //If the dataset has 2 or more modes.
      bimodal=1;
    }
    if( mode==0 || bimodal==1 ) {  // Return the median if there is no mode.
     mode=x[(n/2)];
    }
    return mode;
  }
}


