package com.example.androidtablayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class PhotosActivity extends Activity{
	    TextView txtArduino,txtGround,txtleft,txtRight,txtLatt,txtLong,txtTime,txtRecord,txtProvider,txtAccuracy;
	    //Button btn;
	    
	    //declaring location manager
	    private LocationManager locationManager;
	    
	 // wake-lock
	 PowerManager.WakeLock wL;
	 			
	//declaring media player
	MediaPlayer gvoice,lvoice,rvoice;

	//progress dialogue
	private ProgressDialog pDialog;
					
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
					
	private static final String insert_url_location = "http://192.168.193.1/blindlocation/locationinsert.php";  //I used this before for Server which was Laptop via Wi-Fi

	//private static final String insert_url_location = "http://myeyemateproject.fh2web.com/blindlocation/insert.php";
	// JSON Node names
						private static final String TAG_SUCCESS = "success";
						private static final String TAG_LOCATION = "location";
						private static final String TAG_LATTITUDE = "lattitude";
						private static final String TAG_LONGITUDE = "longitude";
						private static final String TAG_TIME = "time";
						
	/*These three strings are global variables */					
	String latt,longi,time;
						
						/*Daclaring calender for Getting current time*/
						Calendar c;
	
						//phoneNumber
						String number="01947717212";
						
						private static final String TAG = "eyemate";
						
						protected static final int RESULT_SPEECH = 1;
						
						static TextToSpeech ttobj;

						/*These three flags are used for speech sound*/
						boolean leftFlag = true, rightFlag = true, groundFlag = true;
						
						/*This boolean flag is for calling */
						boolean callflag=false;
						
						/*These Handlers are used for main bluetooth data receive handler h and timer calling thread handler timerhandler*/
						Handler h,timerhandler;
						
						//declaring variable for media button
						private AudioManager mAudioManager;
					    private ComponentName mAudioReceiver;
						static Handler receiverHandler;
						static final int RECEIVER_MESSAGE = 1;
						
						/* End of the necessary declaration for Media Button*/

						/*Necessary Declaration for Bluetooth*/
						private BluetoothAdapter btAdapter = null;
						private BluetoothSocket btSocket = null;
						private StringBuilder sb = new StringBuilder();

						private ConnectedThread mConnectedThread;

						// SPP UUID service
						private static final UUID MY_UUID = UUID
								.fromString("00001101-0000-1000-8000-00805F9B34FB"); // bluetooth
																						// connection
																						// er unique
																						// id

						// MAC-address of Bluetooth module
						private static String address = "98:D3:31:20:04:41";

						final int RECIEVE_MESSAGE = 1;		// Status  for Handler
						
						Boolean datacheck = false;  //This boolean variable will check that is there any data is received or not...
						
						//Declaring databse class object
						DBAdapter db = new DBAdapter(this);
						
						
						//TimerTask
						Timer timer;
						TimerTask timerTask;
						static final int TIMER_MESSAGE = 1;
						//we are going to use a handler to be able to run in our TimerTask
						private Handler handler;
						
				@Override
				protected void onCreate(Bundle savedInstanceState) {
					// wake-lock
					PowerManager pM = (PowerManager) getSystemService(Context.POWER_SERVICE);
					WakeLock wL = pM.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,"whatever");

					super.onCreate(savedInstanceState);
					wL.acquire();
					setContentView(R.layout.photos_layout);
					
					txtArduino = (TextView) findViewById(R.id.txtArduino);
					txtGround = (TextView) findViewById(R.id.textViewGround);
					txtleft = (TextView) findViewById(R.id.textViewLeft);
					txtRight = (TextView)findViewById(R.id.textViewRight);
					txtLatt = (TextView) findViewById(R.id.lattitudetext);
					txtLong = (TextView) findViewById(R.id.longitudetext);
					txtTime = (TextView) findViewById(R.id.timeText);
					txtRecord = (TextView)findViewById(R.id.records);
					txtProvider =(TextView)findViewById(R.id.providertxt);
					txtAccuracy = (TextView) findViewById(R.id.txtAccuracy);
					
					//btn = (Button)findViewById(R.id.tbutton1);
					
					locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

					//defing the media player
					gvoice = MediaPlayer.create(PhotosActivity.this, R.raw.obstacleinground);
					lvoice = MediaPlayer.create(PhotosActivity.this, R.raw.obstacleinleft);
					rvoice = MediaPlayer.create(PhotosActivity.this, R.raw.obstacleinright);
					
					
					ttobj = new TextToSpeech(getApplicationContext(),
							new TextToSpeech.OnInitListener() {
								@Override
								public void onInit(int status) {
									if (status != TextToSpeech.ERROR) {
										ttobj.setLanguage(Locale.CANADA);
									}
								}
							});
					
					
					h = new Handler() {
						public void handleMessage(android.os.Message msg) {
							switch (msg.what) {
							case RECIEVE_MESSAGE: // if receive massage
								byte[] readBuf = (byte[]) msg.obj;
								String strIncom = new String(readBuf, 0, msg.arg1); // create
																					// string
																					// from
																					// bytes
																					// array
								sb.append(strIncom); // append string
								int endOfLineIndex = sb.indexOf("\r\n"); // determine the
																			// end-of-line
								if (endOfLineIndex > 0) { // if end-of-line,
									String sbprint = sb.substring(0, endOfLineIndex); // extract
																						// string
									sb.delete(0, sb.length()); // and clear
									txtArduino.setText("Data from Arduino: " + sbprint); // update
																							// TextView

									// newly edited

									if (sbprint.equals("Ground")) {
										if (groundFlag) {
											Log.d("bbbbbbbbbbbbbb", "ground on");
											strIncom = "Obstacle in the ground";
											gvoice.start();
										}

										else
											strIncom = "";
									}
									if (sbprint.equals("  Left")) {
										if (leftFlag) {
											Log.d("bbbbbbbbbbbbbb", "left on");
											strIncom = "Obstacle in the upper Left";
										    lvoice.start();
										}

										else
											strIncom = "";
									}
									if (sbprint.equals("  Right")) {
										if (rightFlag) {
											Log.d("bbbbbbbbbbbbbb", "right on");
											strIncom = "Obstacle in the upper Right";
										rvoice.start();
										} else
											strIncom = "";
									}
									//ttobj.speak(strIncom, TextToSpeech.QUEUE_FLUSH, null); // newly
																							// edited
			                      if(callflag==true){
			                    	  Log.v("CALL_FROM_INSIDE_h","I am being called from inside of the h handler.....");

			                    	  makeCall();
			                      }
								}
								// Log.d(TAG, "...String:"+ sb.toString() + "Byte:" +
								// msg.arg1 + "...");
								break;
							}
						};
					};
					
					/*Audio manager for Headset Button action*/
					mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
			        mAudioReceiver =  new ComponentName(getPackageName(),
			            MediaButtonIntentReceiver.class.getName());
			        
			        /*Headset Button action thread controller */
			        receiverHandler = new Handler(){
						
						public void handleMessage(android.os.Message msg) {
							// TODO Auto-generated method stub
							switch (msg.what) {
							case RECEIVER_MESSAGE:
							                     
								//Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
			     				sendCommand();
			     				
			     				
			                     break;
							}
						}
					};
					
					/*Timer thread controller*/
					timerhandler = new Handler(){
						
						public void handleMessage(android.os.Message msg) {
							// TODO Auto-generated method stub
							switch (msg.what) {
							case TIMER_MESSAGE:
								Calendar c = Calendar.getInstance();
								int seconds = c.get(Calendar.SECOND);
						        int minutes = c.get(Calendar.MINUTE);
						        int hour = c.get(Calendar.HOUR);
						        int am_pm = c.get(Calendar.AM_PM);
						        String meridian;
						        if(am_pm==0){
						        	meridian = "am";
						        }else{
						        	meridian = "pm";
						        }
						        String currenttime = hour+":"+minutes+":"+seconds+" "+meridian ;

			                     
								 //lattText.setText(""+Lattitude);
								 //longText.setText(""+Longitude);
			                     //time.setText(""+currenttime);
			                     //Log.e("HANDLER_TAG", "HANDLER is showing your currnt lattitude: "+Lattitude+" and longitude: "+Longitude);

			                     //latt = lattText.getText().toString();
			                     //longi = longText.getText().toString();
			                     //String str = "Latitude: "+lattText.getText().toString()+"Longitude: "+longText.getText().toString();

			                     new InsertLocation().execute();   //inserting the location
			          	         //Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
			     				
			     				
			     				
			                     break;
							}
						}
					};
					
					

					btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth
																		// adapter
					checkBTState();
					
					/*
					btn.setOnLongClickListener(new OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							Intent intent = new Intent(
									RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

							intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

							try {
								startActivityForResult(intent, RESULT_SPEECH);
								// txtText.setText("");
							} catch (ActivityNotFoundException a) {
								Toast.makeText(getApplicationContext(), "Dont Support",
										Toast.LENGTH_LONG).show();
							}
							return false;
						}
					});	
					*/
				}
				
				
				
				@Override
				protected void onActivityResult(int requestCode, int resultCode, Intent data) {
					super.onActivityResult(requestCode, resultCode, data);

					switch (requestCode) {
					case RESULT_SPEECH: {
						if (resultCode == RESULT_OK && null != data) {

							ArrayList<String> text = data
									.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
							// txtText.setText(text.get(0));
							Log.d("aaaaaaaaaaaaa", text.get(0));
							ttobj.speak(text.get(0), TextToSpeech.QUEUE_FLUSH, null);
							if (text.get(0).toLowerCase().equals("system on")) {
								groundFlag = true;
								leftFlag = true;
								rightFlag = true;
								txtGround.setText("Ground enabled");
								txtleft.setText("Left enabled");
								txtRight.setText("Right enabled");
								Log.d("aaaaaaaaaaaaa", text.get(0));
							} else if (text.get(0).toLowerCase().equals("left on") || text.get(0).toLowerCase().equals("lift on")) {
								leftFlag = true;
								txtleft.setText("Left enabled");
								Log.d("aaaaaaaaaaaaa", text.get(0));
							} else if (text.get(0).toLowerCase().equals("right on") || text.get(0).toLowerCase().equals("light on")
									|| text.get(0).toLowerCase().equals("write on")) {
								rightFlag = true;
								txtRight.setText("Right enabled");
								Log.d("aaaaaaaaaaaaa", text.get(0));
							} else if (text.get(0).toLowerCase().equals("ground on")) {
								groundFlag = true;
								txtGround.setText("Ground enabled");
								Log.d("aaaaaaaaaaaaa", text.get(0));
							} else if (text.get(0).toLowerCase().equals("system off") || text.get(0).toLowerCase().equals("system of")) {
								groundFlag = false;
								leftFlag = false;
								rightFlag = false;
								txtGround.setText("Ground disabled");
								txtleft.setText("Left disabled");
								txtRight.setText("Right disabled");
								Log.d("aaaaaaaaaaaaa", text.get(0));
							} else if (text.get(0).toLowerCase().equals("left off") || text.get(0).toLowerCase().equals("left of")
									|| text.get(0).toLowerCase().equals("lift off") || text.get(0).toLowerCase().equals("lift of")) {
								leftFlag = false;
								txtleft.setText("Left disabled");
								Log.d("aaaaaaaaaaaaa", text.get(0));
							} else if (text.get(0).toLowerCase().equals("right off") || text.get(0).toLowerCase().equals("right of")
									|| text.get(0).toLowerCase().equals("write off") || text.get(0).toLowerCase().equals("write of")
									|| text.get(0).toLowerCase().equals("light off") || text.get(0).toLowerCase().equals("light of")) {
								rightFlag = false;
								txtRight.setText("Right disabled");
								Log.d("aaaaaaaaaaaaa", text.get(0));
							} else if (text.get(0).toLowerCase().equals("ground off") || text.get(0).toLowerCase().equals("ground of")) {
								groundFlag = false;
								txtGround.setText("Ground disabled");
								Log.d("aaaaaaaaaaaaa", text.get(0));
							} else if(text.get(0).toLowerCase().equals("i need help") || text.get(0).toLowerCase().equals("I need him")){
								callflag=true;
								
								if(datacheck!=true){   //if no data is received through bluetooth then we call from out side of the h handler..
									Log.v("CALL_FROM_OUTSIDE_h","I am being called from outside of the h handler.....");
									makeCall();
								}
							}
							
						}
						break;
					}

					}
				}
				
				/*Call to the specific number saved in the SQLite database*/
				protected void makeCall() {
					   
					  new getData().execute();
					   
				      Log.i("Make call", "");
			           
				       Toast.makeText(getApplicationContext(), ""+number, Toast.LENGTH_SHORT).show();
				      
			          Intent phoneIntent = new Intent(Intent.ACTION_CALL);
				      phoneIntent.setData(Uri.parse("tel:"+number));

				      try {
				         startActivity(phoneIntent);
				         callflag = false;
				         
				         Log.i("Finished making a call...", "");
				      } catch (android.content.ActivityNotFoundException ex) {
				         Toast.makeText(PhotosActivity.this, 
				         "Call faild, please try again later.", Toast.LENGTH_SHORT).show();
				      }
				   }
				
				public static class MediaButtonIntentReceiver extends BroadcastReceiver {

			        @Override
			        public void onReceive(Context context, Intent intent) {
			            //Log.d("SA", "ON RECEIVE");
			            Log.e("TAGCLICK", "ON Receive");
			            Toast.makeText(context, "Hi all", Toast.LENGTH_SHORT).show();
			            //...
			            ttobj.speak("say ur command", TextToSpeech.QUEUE_FLUSH, null); // newly
			            
			            receiverHandler.obtainMessage(RECEIVER_MESSAGE).sendToTarget();
						//sendCommand();
			            abortBroadcast();
			        }
			    }
				
				public void sendCommand(){
			    	Intent intent = new Intent(
							RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
			    	Log.e("sendCommandTAG","I am being called...");
			    	try {
						startActivityForResult(intent, RESULT_SPEECH);
						// txtText.setText("");
					} catch (ActivityNotFoundException a) {
						Toast.makeText(getBaseContext(), "Dont Support",
								Toast.LENGTH_LONG).show();
					}
			    }
				
				private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
						throws IOException {
					if (Build.VERSION.SDK_INT >= 10) {
						try {
							final Method m = device.getClass().getMethod(
									"createInsecureRfcommSocketToServiceRecord",
									new Class[] { UUID.class });
							return (BluetoothSocket) m.invoke(device, MY_UUID);
						} catch (Exception e) {
							Log.e(TAG, "Could not create Insecure RFComm Connection", e);
						}
					}
					return device.createRfcommSocketToServiceRecord(MY_UUID);
				}
				
				@Override
				protected void onStart() {
					super.onStart();
					boolean gpsEnabled = locationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER);
					if (!gpsEnabled) {
						Toast.makeText(getApplicationContext(),
								"Please consider enabling GPS", Toast.LENGTH_LONG).show();
					}
				}

				@Override
				protected void onResume() {
					super.onResume();
					setUp();
					
					Log.d(TAG, "...onResume - try connect...");

					//registering the mAudioManager for BroadCastReceiver 
					mAudioManager.registerMediaButtonEventReceiver(mAudioReceiver);

					// Set up a pointer to the remote node using it's address.
					BluetoothDevice device = btAdapter.getRemoteDevice(address);

					// Two things are needed to make a connection:
					// A MAC address, which we got above.
					// A Service ID or UUID. In this case we are using the
					// UUID for SPP.

					try {
						btSocket = createBluetoothSocket(device);
					} catch (IOException e) {
						datacheck = false;
						errorExit("Fatal Error", "In onResume() and socket create failed: "
								+ e.getMessage() + ".");
					}

					/*
					 * try { btSocket = device.createRfcommSocketToServiceRecord(MY_UUID); }
					 * catch (IOException e) { errorExit("Fatal Error",
					 * "In onResume() and socket create failed: " + e.getMessage() + "."); }
					 */

					// Discovery is resource intensive. Make sure it isn't going on
					// when you attempt to connect and pass your message.
					btAdapter.cancelDiscovery();

					// Establish the connection. This will block until it connects.
					Log.d(TAG, "...Connecting...");
					try {
						btSocket.connect();
						Log.d(TAG, "....Connection ok...");
					} catch (IOException e) {
						datacheck = false;   //no data is being received then it is false ...
						try {
							btSocket.close();
						} catch (IOException e2) {
							errorExit("Fatal Error",
									"In onResume() and unable to close socket during connection failure"
											+ e2.getMessage() + ".");
						}
					}

					// Create a data stream so we can talk to server.
					Log.d(TAG, "...Create Socket...");

					mConnectedThread = new ConnectedThread(btSocket);
					mConnectedThread.start();
					
					/* Now Starting the Timer to get the location after 1 minutes.....*/
					startTimer();
					Log.v("TIMER_START", "TIMER has been started...");
				}
				
				/*Here is the core definition of the Timer thread...*/
				public void startTimer() {
			        //set a new Timer
			        timer = new Timer();
			        //initialize the TimerTask's job

			        initializeTimerTask();

			        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms

			        timer.schedule(timerTask, 5000, 300000); //

			    }
			/*
				public void stoptimertask(View v) {

			        //stop the timer, if it's not already null

			        if (timer != null) {

			            timer.cancel();

			            timer = null;
			        }
			}
			*/

			public void initializeTimerTask() {
			        timerTask = new TimerTask() {
			            public void run() {
			                //use a handler to run a toast that shows the current timestamp

			                //handler.post(new Runnable() {

			                   // public void run() {
			                      /*
			                    	//startActivity(new Intent(Main.this, GetLocationWithGPS.class));
			       	        	 if (!locationMgr.isProviderEnabled(
			       				         android.location.LocationManager.GPS_PROVIDER)) {  
			       				         AlertDialog.Builder builder = 
			       				            new AlertDialog.Builder(Main.this);              
			       				         builder.setTitle("GPS is not enabled").setMessage("Would you like to go to the location settings and enable GPS?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			       				        	 public void onClick(DialogInterface dialog, int id) {
			       				                     startActivity(
			       				                        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));     
			       				                     }
			       				                  })
			       				            .setNegativeButton("No",new DialogInterface.OnClickListener() {
			       				                   public void onClick(DialogInterface dialog, int id) {
			       				                       dialog.cancel();
			       				                         finish();
			       				                      }
			       				                   });
			       				          AlertDialog alert = builder.create();
			       				          alert.show();
			       				       } else {
			       				    	   
			       				       }
			       				       */
			       				          
			                    	c = Calendar.getInstance();
									int seconds = c.get(Calendar.SECOND);
							        int minutes = c.get(Calendar.MINUTE);
							        int hour = c.get(Calendar.HOUR);
							        int am_pm = c.get(Calendar.AM_PM);
							        String meridian;
							        if(am_pm==0){
							        	meridian = "am";
							        }else{
							        	meridian = "pm";
							        	
							        }
							        if(hour==0){
							        	hour=12;
							        }
							        time = hour+":"+minutes+":"+seconds+" "+meridian ;
							        
									latt = txtLatt.getText().toString();
									longi = txtLong.getText().toString();									
									Log.e("TIMER_FIRED", "Lattitude: "+latt+"\n Longitude: "+longi+"\nTime: "+time);
									
									timerhandler.obtainMessage(TIMER_MESSAGE).sendToTarget();

												       				                                 
			       				      // }

			                    }

			                //});

			            //}

			        };

			    }

				public void setUp() {
					Location gpsLocation = null;
					Location networkLocation = null;

					locationManager.removeUpdates(listener);
					gpsLocation = requestUpdateFromProvider(LocationManager.GPS_PROVIDER);
					networkLocation = requestUpdateFromProvider(LocationManager.NETWORK_PROVIDER);

					if (gpsLocation != null && networkLocation != null) {
						Location myLocation = getBetterLocation(gpsLocation,
								networkLocation);
						setValuesInUI(myLocation);
					} else if (gpsLocation != null) {
						setValuesInUI(gpsLocation);
					} else if (networkLocation != null) {
						setValuesInUI(networkLocation);
					} else {
						txtProvider.setText("No data availble");
						// no data
					}
				}

				private void setValuesInUI(Location loc) {
					txtLatt.setText("" + loc.getLatitude());
					txtLong.setText("" + loc.getLongitude());
					txtAccuracy.setText("Accuracy: " + loc.getAccuracy());
					txtProvider.setText("Provider: " + loc.getProvider());
				}

				private Location getBetterLocation(Location newLocation,
						Location currentBestLocation) {
					if (currentBestLocation == null) {
						// A new location is always better than no location
						return newLocation;
					}

					// Check whether the new location fix is newer or older
					long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
					boolean isSignificantlyNewer = timeDelta > 60000;
					boolean isSignificantlyOlder = timeDelta < 60000;
					boolean isNewer = timeDelta > 0;

					// If it's been more than two minutes since the current location, use
					// the new location
					// because the user has likely moved.
					if (isSignificantlyNewer) {
						return newLocation;
						// If the new location is more than two minutes older, it must be
						// worse
					} else if (isSignificantlyOlder) {
						return currentBestLocation;
					}

					// Check whether the new location fix is more or less accurate
					int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation
							.getAccuracy());
					boolean isLessAccurate = accuracyDelta > 0;
					boolean isMoreAccurate = accuracyDelta < 0;

					// Determine location quality using a combination of timeliness and
					// accuracy
					if (isMoreAccurate) {
						return newLocation;
					} else if (isNewer && !isLessAccurate) {
						return newLocation;
					}
					return currentBestLocation;
				}

				private Location requestUpdateFromProvider(String provider) {
					Location location = null;
					if (locationManager.isProviderEnabled(provider)) {
						locationManager
								.requestLocationUpdates(provider, 10000, 10, listener);
						location = locationManager.getLastKnownLocation(provider);

					} else {
						Toast.makeText(getApplicationContext(),
								provider + " is not enabled.", Toast.LENGTH_LONG).show();
					}
					return location;
				}

				@Override
				protected void onPause() {
					super.onPause();
					locationManager.removeUpdates(listener);
					

					Log.d(TAG, "...In onPause()...");

					//unregister the mAudiomanager to Unregister the BroadCasstReceiver
					mAudioManager.unregisterMediaButtonEventReceiver(mAudioReceiver);
					
					try {
						btSocket.close();
					} catch (IOException e2) {
						errorExit("Fatal Error", "In onPause() and failed to close socket."
								+ e2.getMessage() + ".");
					}
					
				}

				@Override
				protected void onStop() {
					super.onStop();
				}
				
				@Override
				public void onBackPressed() {
					// TODO Auto-generated method stub
					//super.onBackPressed();
					Toast.makeText(getApplicationContext(), "Backpressed", Toast.LENGTH_SHORT).show();
					
				}
				
				private void checkBTState() {
					// Check for Bluetooth support and then check to make sure it is turned
					// on
					// Emulator doesn't support Bluetooth and will return null
					if (btAdapter == null) {
						errorExit("Fatal Error", "Bluetooth not support");
					} else {
						if (btAdapter.isEnabled()) {
							Log.d(TAG, "...Bluetooth ON...");
						} else {
							// Prompt user to turn on Bluetooth
							Intent enableBtIntent = new Intent(
									BluetoothAdapter.ACTION_REQUEST_ENABLE);
							startActivityForResult(enableBtIntent, 1);
						}
					}
				}
				
				private void errorExit(String title, String message) {
					Toast.makeText(getBaseContext(), title + " - " + message,
							Toast.LENGTH_LONG).show();
					finish();
				}
				
				private class ConnectedThread extends Thread {
					private final InputStream mmInStream;
					private final OutputStream mmOutStream;

					public ConnectedThread(BluetoothSocket socket) {
						InputStream tmpIn = null;
						OutputStream tmpOut = null;

						// Get the input and output streams, using temp objects because
						// member streams are final
						try {
							tmpIn = socket.getInputStream();
							tmpOut = socket.getOutputStream();
						} catch (IOException e) {
						}

						mmInStream = tmpIn;
						mmOutStream = tmpOut;
					}

					public void run() {
						byte[] buffer = new byte[256]; // buffer store for the stream
						int bytes; // bytes returned from read()

						// Keep listening to the InputStream until an exception occurs
						while (true) {
							try {
								// Read from the InputStream
								bytes = mmInStream.read(buffer); // Get number of bytes and
									datacheck = true;								// message in "buffer"
								h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer)
										.sendToTarget(); // Send to message queue Handler
							} catch (IOException e) {
								break;
							}
						}
					}

					/* Call this from the main activity to send data to the remote device */
					public void write(String message) {
						Log.d(TAG, "...Data to send: " + message + "...");
						byte[] msgBuffer = message.getBytes();
						try {
							mmOutStream.write(msgBuffer);
						} catch (IOException e) {
							Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
						}
					}
				}
				
				class InsertLocation extends AsyncTask<String, String, String>{
					  /**
						 * Before starting background thread Show Progress Dialog
						 * */
						
						@Override
						protected void onPreExecute() {
							// TODO Auto-generated method stub
							super.onPreExecute();
							pDialog = new ProgressDialog(PhotosActivity.this);
							pDialog.setMessage("Creating Product....Please wait..");
							pDialog.setIndeterminate(false);
							pDialog.setCancelable(true);
							pDialog.show();
							
						}
						/**
						 * Creating product
						 * */
						
					@Override
					protected String doInBackground(String... args) {
						
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair(TAG_LATTITUDE, latt));
						params.add(new BasicNameValuePair(TAG_LONGITUDE, longi));
						params.add(new BasicNameValuePair(TAG_TIME, time));
						
						// sending modified data through http request
						// Notice that update product url accepts POST method
						JSONObject json = jsonParser.makeHttpRequest(insert_url_location,"POST", params);
						
						// check json success tag
						try{
							int success = json.getInt(TAG_SUCCESS);
							Log.e("TAG_SUCCESS_iMATE", ""+success);
							if(success==1){
								// successfully updated
								Intent i = getIntent();
								// send result code 100 to notify about product update
								setResult(100, i);
								
							}
							else{
								Log.e("Update Error", "I am sorry to update........");
							}
						}catch (JSONException e) {
							e.printStackTrace();
						}
						
						return null;
					}
					
					@Override
					protected void onPostExecute(String result) {
						// dismiss the dialog once product deleted
						pDialog.dismiss();
						txtLatt.setText(""+latt);
						txtLong.setText(""+longi);
						txtTime.setText(""+time);
						
					}
					  
				  }
				
				LocationListener listener = new LocationListener() {

					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onProviderEnabled(String provider) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onProviderDisabled(String provider) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onLocationChanged(Location location) {
						setValuesInUI(location);
						/*
						c = Calendar.getInstance();
						int seconds = c.get(Calendar.SECOND);
				        int minutes = c.get(Calendar.MINUTE);
				        int hour = c.get(Calendar.HOUR);
				        int am_pm = c.get(Calendar.AM_PM);
				        String meridian;
				        if(am_pm==0){
				        	meridian = "am";
				        }else{
				        	meridian = "pm";
				        	
				        }
				        if(hour==0){
				        	hour=12;
				        }
				        time = hour+":"+minutes+":"+seconds+" "+meridian ;
				        
						latt = txtLatt.getText().toString();
						longi = txtLong.getText().toString();
						
						new InsertLocation().execute();
						*/

					}
				};
				
				
				/*
				 * Retrieving data in background
				 * 
				 */
				
				class getData extends AsyncTask<String, String, String>{
			     String  hisname,hisphone;
					@Override
					protected String doInBackground(String... args) {
						// TODO Auto-generated method stub
						db.open();
						Cursor c = db.getAllContacts();
						if(c.getCount()==0){
							//records.setText("No records found.....");
						}
						else{
							StringBuffer buffer=new StringBuffer();
							while(c.moveToNext()){
								/*
								buffer.append("name: "+c.getString(0)+"\n");
				    			buffer.append("phone: "+c.getString(1)+"\n\n");
				    			*/
				    			hisname = c.getString(0);
								hisphone = c.getString(1);
								number = hisphone;
							}
							
							/*
							//records.setText(""+buffer.toString());
							Builder builder=new Builder(SongsActivity.this);
					    	builder.setCancelable(true);
					    	builder.setTitle("Records");
					    	builder.setMessage(""+buffer.toString());
					    	builder.show();
							//records.setText("records found.....");
					    	*/
							db.close();
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(String result) {
						// TODO Auto-generated method stub
						super.onPostExecute(result);
						//name.setText(hisname);
						txtRecord.setText(hisphone);
						
					}
					
				}
}