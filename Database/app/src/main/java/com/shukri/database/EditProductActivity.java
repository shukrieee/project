package com.shukri.database;

/**
 * Created by 108160 on 12/8/2015.
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditProductActivity extends Activity {

    TextView txtName;
    TextView txtProfessor;
    TextView txtDays;
    TextView txtTime;
    TextView txtLocation;

    Button btnSave;


    String pid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single product url
    private static final String url_product_details = "http://classdays.x10host.com/get_product_details.php";

    // url to update product
    private static final String url_update_product = "http://classdays.x10host.com/update_product.php";

    // url to delete product
    private static final String url_delete_product = "http://classdays.x10host.com/delete_product.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CLASS = "class";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PROFESSOR = "professor";
    private static final String TAG_DAYS = "days";
    private static final String TAG_HOUR_START="hour_start";
    private static final String TAG_MINUTE_START="minute_start";
    private static final String TAG_YEAR_START="year_start";
    private static final String TAG_MONTH_START="month_start";
    private static final String TAG_DAY_START="day_start";
    private static final String TAG_HOUR_END="hour_end";
    private static final String TAG_MINUTE_END="minute_end";
    private static final String TAG_YEAR_END="year_end";
    private static final String TAG_MONTH_END="month_end";
    private static final String TAG_DAY_END="day_end";
    private static final String TAG_LOCATION="location";
    String name;
    String professor;
    String days;
    String time;
    String hour_start;
    String minute_start;
    String year_start;
    String month_start;
    String day_start;
    String hour_end;
    String minute_end;
    String year_end;
    String month_end;
    String day_end;
    String location;
    String until="";
    GregorianCalendar cal1;
    GregorianCalendar cal2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);

        // save button
        btnSave = (Button) findViewById(R.id.btnSave);


        // getting product details from intent
        Intent i = getIntent();

        // getting product id (pid) from intent
        pid = i.getStringExtra(TAG_PID);

        // Getting complete product details in background thread
        new GetProductDetails().execute();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update product
                 name = txtName.getText().toString();
                location=txtLocation.getText().toString();
                  professor= txtProfessor.getText().toString();
                 days = txtDays.getText().toString();
                cal1 = new GregorianCalendar(Integer.parseInt(year_start), Integer.parseInt(month_start), Integer.parseInt(day_start),
                        Integer.parseInt(hour_start), Integer.parseInt(minute_start));
                cal2 = new GregorianCalendar(Integer.parseInt(year_start), Integer.parseInt(month_start), Integer.parseInt(day_start),
                        Integer.parseInt(hour_end), Integer.parseInt(minute_end));
                until=until+year_end+month_end+day_end;
                Log.d("until",until);

                addEvent(name,location,cal1,cal2,until);

                // new SaveProductDetails().execute();
            }
        });

        // Delete button click event




    }
    public void addEvent(String title, String location, Calendar begin, Calendar end ,String last) {


        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setType("vnd.android.cursor.item/event")

                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.RRULE,"FREQ=WEEKLY;BYDAY="+days+";UNTIL="+last)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        begin.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        end.getTimeInMillis());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    /**
     * Background Async Task to Get complete product details
     * */
    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Loading product details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String[] params) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                params2.add(new BasicNameValuePair("pid", pid));

                // getting product details by making HTTP request
                // Note that product details url will use GET request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_product_details, "GET", params2);

                // check your log for json response
                Log.d("Single Product Details", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received product details
                            Iterator x = json.keys();
                            JSONArray productObj = new JSONArray();

                          while (x.hasNext()){
                              String key = (String) x.next();
                               productObj.put(json.get(key));
                            }


                             productObj = json.getJSONArray(TAG_CLASS); // JSON Array


                    JSONObject product = productObj.getJSONObject(0);


                    Log.i("JSONArray: ", productObj.toString());

                    // get first product object from JSON Array

                    // product with this pid found
                    // Edit Text
                    txtName = (TextView) findViewById(R.id.inputName);
                    txtProfessor = (TextView) findViewById(R.id.inputProfessor);
                    txtDays = (TextView) findViewById(R.id.inputDays);
                    txtTime=(TextView) findViewById(R.id.inputTime);
                    txtLocation=(TextView)findViewById(R.id.location);
                    name=product.getString(TAG_NAME);
                    professor=product.getString(TAG_PROFESSOR);
                    days=product.getString(TAG_DAYS);
                    year_start = product.getString(TAG_YEAR_START);
                    year_end = product.getString(TAG_YEAR_END);
                    month_start=product.getString(TAG_MONTH_START);
                    month_end=product.getString(TAG_MONTH_END);
                    day_start=product.getString(TAG_DAY_START);
                    day_end=product.getString(TAG_DAY_END);
                    hour_start=product.getString(TAG_HOUR_START);
                    hour_end=product.getString(TAG_HOUR_END);
                    minute_start=product.getString(TAG_MINUTE_START);
                    minute_end=product.getString(TAG_MINUTE_END);
                    time=hour_start+":"+minute_start+" - "+hour_end+":"+minute_end;
                    location=product.getString(TAG_LOCATION);




                }else{
                    // product with pid not found
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
            txtName.setText(name);
            txtProfessor.setText(professor);
            txtDays.setText(days);
            txtTime.setText(time);
            txtLocation.setText(location);


        }
    }

    /**
     * Background Async Task to  Save product Details
     * */

}