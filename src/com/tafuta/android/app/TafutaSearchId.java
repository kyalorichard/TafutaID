package com.tafuta.android.app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TafutaSearchId extends Activity {
	private static EditText firstname,middlename, lastname, idnumber;
	static String FIRSTNAME,MIDDLENAME,LASTNAME,IDNUMBER,ID_TYPE;
	private Button submit;
	private Spinner  IDtype;
	private MediaPlayer mp;
	private String mErrorMessage = "";
	private boolean mError = false;	
	HttpPost httpPost;
	StringBuffer buffer;
	HttpResponse response;
	HttpClient httpclient;
	InputStream inputstream;
	ArrayList<NameValuePair> namevaluepairs;
	InputStream is = null;
	String result = "",Y,N;
	JSONObject jArray = null;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_id);
        
        firstname=(EditText)findViewById(R.id.firstname);
        middlename=(EditText)findViewById(R.id.middlename);
        lastname=(EditText)findViewById(R.id.lastname);
        idnumber=(EditText)findViewById(R.id.idnumber);
        submit = (Button)findViewById(R.id.search);
        
        IDtype = (Spinner) findViewById(R.id.spn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        IDtype.setAdapter(adapter);
        
        initComponents();
    }


	private void initComponents() {
		firstname.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(firstname.getText())) {
                    	firstname.setError(getString(R.string.empty_firstname));
                    }
                }

            }

        });
		middlename.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(middlename.getText())) {
                    	middlename.setError(getString(R.string.empty_middlename));
                    }
                }

            }

        });
		lastname.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(lastname.getText())) {
                    	lastname.setError(getString(R.string.empty_lastname));
                    }
                }

            }

        });
		idnumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(idnumber.getText())) {
                    	idnumber.setError(getString(R.string.empty_idnumber));
                    }
                }

            }

        });
		submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (Util.isConnected(TafutaSearchId.this)) {
            	ID_TYPE = (String) IDtype.getSelectedItem();
            	mError = false;
            	if(ID_TYPE.equals("Select ID Type"))
                {
                	mErrorMessage = getString(R.string.empty_type)+"\n";
                    mError = true;	
                }
                if (TextUtils.isEmpty(firstname.getText())) {
                    mErrorMessage += getString(R.string.empty_firstname)+"\n";
                    mError = true;
                }
                if (TextUtils.isEmpty(middlename.getText())) {
                    mErrorMessage += getString(R.string.empty_middlename)+"\n";
                    mError = true;
                }
                if (TextUtils.isEmpty(lastname.getText())) {
                    mErrorMessage += getString(R.string.empty_lastname)+"\n";
                    mError = true;
                }
                
                if (TextUtils.isEmpty(idnumber.getText())) {
                    mErrorMessage += getString(R.string.empty_idnumber);
                    mError = true;
                }
                if (!mError) {
                	
                	new searchID(){
                		public void onPostExecute(String result) {
                			if(result.equals("Y")){
                				this.progressDialog.dismiss();

    							Toast.makeText(TafutaSearchId.this,
    					      	         "\nCould not Find That ID Card \n However it is already listed in the system as missing\n", Toast.LENGTH_LONG).show();
                				
    							return;
                				
                			}
                			if(result.equals("N")){
                				this.progressDialog.dismiss();
                				mp = MediaPlayer.create(getApplicationContext(), R.raw.pling);
                            	mp.start();
    							AlertDialog.Builder clearBuilder = new AlertDialog.Builder(TafutaSearchId.this);
                                clearBuilder.setMessage(getString(R.string.confirm_listing))
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.status_yes),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                    	
                                                    	 Intent intent = new Intent(TafutaSearchId.this, TafutaSearchAppeal.class);
                                            	         startActivity (intent); 
                                                        
                                                    }
                                                })
                                        .setNegativeButton(getString(R.string.status_no),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        Toast.makeText(TafutaSearchId.this,
                           					      	         "\nSearch appeal process has been prematurely terminated \n ID Card was not submitted\n ", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                AlertDialog clearDialog = clearBuilder.create();
                                clearDialog.show();
    							
    							return;
                			}
                			else
                			{
                				try{
                			    	
                		            jArray = new JSONObject(result);            
                			      }
                	            catch(JSONException e){
                			            Log.e("log_tag", "Error parsing kupitisha data "+e.toString());
                			       }
                	            if(jArray!=null){
        							TafutaDisplayID.ServerSide(jArray);
        							startActivity (new Intent(getApplicationContext(), TafutaDisplayID.class));
        							this.progressDialog.dismiss();
                	            }
        							else
        							{
        							this.progressDialog.dismiss();
        							Toast.makeText(TafutaSearchId.this,
        				      	         "\nSomething went wrong.....\n Please Restart the Application", Toast.LENGTH_LONG).show(); 
        							}
        							return;
        						}
					}
					
				}.execute();
        		    
                }
                else {
                    final Toast t = Toast.makeText(TafutaSearchId.this, "Error!\n" + mErrorMessage,
                            Toast.LENGTH_LONG);
                    t.show();
                    mErrorMessage = "";
                  
                }
            	
            }
            	else{
              	  Toast.makeText(TafutaSearchId.this, " No internet Connection Detected", Toast.LENGTH_LONG).show();
                }
            }
		});
	}
		
		public static void Data(Context context) {
			
			FIRSTNAME =firstname.getText().toString();
			MIDDLENAME =middlename.getText().toString();
			LASTNAME =lastname.getText().toString();
			IDNUMBER =idnumber.getText().toString();	
			}

			public class searchID extends AsyncTask<String, Integer, String> {
				  ProgressDialog progressDialog = new ProgressDialog(TafutaSearchId.this);
				 
				protected void onPreExecute() {
					progressDialog.setMessage("Searching for ID Card....");
			        progressDialog.show();
				}
				@Override
				protected String doInBackground(String... params) {
					FIRSTNAME =firstname.getText().toString();
		        	MIDDLENAME =middlename.getText().toString();
		        	LASTNAME =lastname.getText().toString();
		        	IDNUMBER =idnumber.getText().toString();
		        	
		        	 try{
				            HttpClient httpclient = new DefaultHttpClient();
				            httpPost = new HttpPost("http://akajaymo.kodingen.com/tafutasearch.php");
				            namevaluepairs = new ArrayList<NameValuePair>(4);
							namevaluepairs.add (new BasicNameValuePair("firstname",FIRSTNAME));
							namevaluepairs.add (new BasicNameValuePair("middlename",MIDDLENAME));
							namevaluepairs.add (new BasicNameValuePair("lastname",LASTNAME));
							namevaluepairs.add (new BasicNameValuePair("idnumber",IDNUMBER));
							httpPost.setEntity (new UrlEncodedFormEntity(namevaluepairs));
				            HttpResponse response = httpclient.execute(httpPost);
				            HttpEntity entity = response.getEntity();
				            is = entity.getContent();
				            
				            

				       }catch(Exception e){
				            Log.e("log_tag", "Error in http connection "+e.toString());
				      }
				 
				      try{
				            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				            StringBuilder sb = new StringBuilder();
				            String line = null;
				            while ((line = reader.readLine()) != null) {
				                    sb.append(line + "\n");
				            }
				            is.close();
				            result=sb.toString().trim();
				      }
				      catch(Exception e){
				            Log.e("log_tag", "Error converting result "+e.toString());
				       }
		           
					  return result;
		        	
					
				}
			
				
		}

			
		}
