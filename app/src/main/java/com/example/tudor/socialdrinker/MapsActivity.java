package com.example.tudor.socialdrinker;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.DriverManager;

public class MapsActivity extends AppCompatActivity
        implements GoogleMap.OnMarkerClickListener,OnMapReadyCallback {


    private GoogleMap mMap;

    RelativeLayout layout;
    TextView text;
    ImageView image;
    Button invite;
    Button all_friends;
    Button near_friends;
    Button custom_friends;

    Bitmap[] iMaps;
    String[] iStrings;
    String[] strings;
    private static final String TAG = MapsActivity.class.getSimpleName();





    LocationListener locationListener;
    LocationManager locationManager;
    private BroadcastReceiver broadcastReceiver;

    private String altcv = null;


    int k = 0;
    String live;
    String past;



    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("activitate: ", "Text has been updated");
                    live = live+"\n" +intent.getExtras().get("coordinates");

                }
            };
        }

        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }
















    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = new Intent(getApplicationContext() ,LocationService.class);
        Log.d("coo","saddadasd");
        startService(i);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        altcv = getSharedPreferences("pref", MODE_PRIVATE).getString("coord", altcv);
        k = getSharedPreferences("pref", MODE_PRIVATE).getInt("nr", k);
        Log.d("coordonate", " actuala: "+live+"\ntrecute: "+past);
        past = altcv;
        k++;

        String cv = live;
//        altcv = altcv.concat(cv);
        SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (k > 10) {
            altcv = cv;
        }
        editor.putString("coord", altcv);
        editor.commit();
        editor.putInt("nr", k);
        Log.d("coordonate", " actuala: "+cv+"\ntrecute: "+altcv);





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        layout = (RelativeLayout)findViewById(R.id.thumbnail);
        layout.setVisibility(View.INVISIBLE);
        text = (TextView)findViewById(R.id.Text);
        image = (ImageView)findViewById(R.id.imageView);
        invite = (Button) findViewById(R.id.button_invite);
        all_friends = (Button) findViewById(R.id.button_all);
        near_friends = (Button) findViewById(R.id.button_near);
        custom_friends = (Button) findViewById(R.id.button_custom);
        iMaps = new Bitmap[100];
        iStrings = new String[100];



    }
    public void getinvisible(View v){
        layout.setVisibility(View.INVISIBLE);
        mMap.getUiSettings().setAllGesturesEnabled(true);
    }
    public void call_friends(View v){
        invite.setVisibility(View.INVISIBLE);
        all_friends.setVisibility(View.VISIBLE);
        near_friends.setVisibility(View.VISIBLE);
        custom_friends.setVisibility(View.VISIBLE);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.json_map_style)));

        if (!success)
            Log.e(TAG, "Style parsing failed.");
        googleMap.setOnMarkerClickListener(this);
        //localurile
        strings = new String[100];
        //get localuri
        Thread thread2 = new Thread() {
            public void run() {
                double lat_o = 10; //get from Location
                double lng_o = 10; //get from Location
                int prio = 4; // get from zoom

                String gotString = "";
                try {
                    int k = 0;
                    URL url = new URL("http://192.168.2.136:8080/test/getPubs.jsp?lat=" + lat_o + "&lng=" + lng_o + "&prio=" + prio);
                    URLConnection urlConnection = url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    gotString = "ok";
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if(inputLine.contains("@"))
                            strings[k++] = inputLine;
                    }
                    in.close();
                } catch (Exception e) {
                    Log.d("debug3", "Error while getting pubs! " + e.getMessage());
                }
                //hardcode
                if (gotString.equals("")) {
                    strings[0] = "10 10@Oktoberfest@Un local misto din centrul vechi.@145@Zaganu 11";
                    strings[1] = "10.5 10.5@Fire_Pub@Un alt local misto din centrul vechi.@215@Vin_Rosu 8";
                }

                int i = 0;
                for (String s : strings) {
                    if (s != null) {
                        Log.d("debug4","String is: "+s);
                        final int j = i;
                        i++;
                        //declarations

                        //get data
                        String[] separated = s.split("@");
                        String[] coords = separated[0].split(" ");
                        final double lat = Double.parseDouble(coords[0]);
                        final double lng = Double.parseDouble(coords[1]);
                        final String name = separated[1];
                        final String description = separated[2];
                        final double dist = Double.parseDouble(separated[3]);
                        String[] bev = separated[4].split(" ");
                        final String beverage = bev[0];
                        final int price = Integer.parseInt(bev[1]);

                        //get icon and set data
                        Thread thread = new Thread() {
                            public void run() {
                                //Download image
                                try {

                                    //get the image
                                    URL url = new URL("http://192.168.2.136:8080/test/getPictures.jsp?name=" + name);
                                    URLConnection urlConnection = url.openConnection();
                                    String gotString = "";
                                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                    String inputLine;
                                    while ((inputLine = in.readLine()) != null)
                                        gotString += inputLine;
                                    in.close();
                                    String[] str = gotString.split("=");
                                    gotString = str[1].replace(">", "");
                                    URL url2 = new URL("http://192.168.2.136:8080" + gotString);
                                    URLConnection urlConnection2 = url2.openConnection();
                                    final InputStream inputStream = urlConnection2.getInputStream();
                                    final File image = File.createTempFile("tempPic", ".jpg");
                                    //final File image = new File("tempPic.jpg");
                                    OutputStream outputStream = new FileOutputStream(image);

                                    int read = 0;
                                    byte[] bytes = new byte[1024];

                                    while ((read = inputStream.read(bytes)) != -1) {
                                        outputStream.write(bytes, 0, read);
                                    }
                                    inputStream.close();
                                    outputStream.close();
                                    //output the image

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                                                iMaps[j] = bitmap;
                                                LatLng pub = new LatLng(lat, lng);
                                                Log.d("pub", String.valueOf(pub));
                                                iStrings[j] = name.replace('_', ' ') + "\n\n" + description + "\nDistance: " + dist + " m\n\nBest buy: " + beverage.replace('_', ' ') + " - " + price + " lei";
                                                Marker m = mMap.addMarker(new MarkerOptions().position(pub).title(name).snippet(description).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                                                m.setTag(j);
                                            } catch (Exception e) {
                                                Log.d("exception2", "Exception ocured while rendering the image! " + e.getMessage());
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.d("exception", "Exception ocured while getting the image! " + e.getMessage());
                                }

                            }

                        };
                        thread.start();
                    }
                }
            }
        };
        thread2.start();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(10, 10.25)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(9.0f));
        Log.d("debug2", "Map initialized!");
    }

    @Override
    public boolean onMarkerClick(final Marker arg0) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(10, 10)));
        mMap.getUiSettings().setAllGesturesEnabled(false);
        Log.d("debug","Marker clicked!");
        if(arg0.getSnippet()!=null && layout != null){
            int tag = (int)arg0.getTag();
            if(tag < 100) {
                layout.setVisibility(View.VISIBLE);
                text.setText(iStrings[tag]);
                image.setImageBitmap(iMaps[tag]);
            }
        }
        return true;
    }
}
