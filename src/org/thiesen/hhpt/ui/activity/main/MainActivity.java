/*
 * $ Id $
 * (c) Copyright 2009 Marcus Thiesen (marcus@thiesen.org)
 *
 *  This file is part of HHPT.
 *
 *  HHPT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  HHPT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with HHPT.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.thiesen.hhpt.ui.activity.main;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.thiesen.hhpt.geolookup.StationFinder;
import org.thiesen.hhpt.geolookup.lucene.LuceneStationFinder;
import org.thiesen.hhpt.osm.OSMUpdater;
import org.thiesen.hhpt.shared.model.position.Position;
import org.thiesen.hhpt.shared.model.station.Stations;
import org.thiesen.hhpt.ui.activity.ConfigActivity;
import org.thiesen.hhpt.ui.activity.R;
import org.thiesen.hhpt.ui.map.TapListenerOverlay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MainActivity extends MapActivity {

    public final static String TAG = "HHPT";

    MapView _mapView;  
    private MapController _mc;  
    private final static int DEFAULT_ZOOM_VALUE = 17;

    public static final double DEFAULT_SEARCH_RADIUS_MILES = 1.0D;

    private static final int MENU_SHOW_POSITION = 1;
    private static final int MENU_CONFIG = 2;  
    private static final int MENU_UPDATE = 3;

    MyLocationOverlay _myLocationOverlay;

    Bitmap _busBmp;

    Bitmap _trainBmp;

    Handler _uiThreadCallback;

    boolean _paused;

    private final LocationListener _listener =  new LocationListenerImpl(this);

    private LocationManager _locationManager; 

    StationFinder _finder;

    private final BlockingQueue<Position> _searchRequestQueue = new LinkedBlockingQueue<Position>();

    @Override  
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        _finder = new LuceneStationFinder( getApplicationContext() );
        _uiThreadCallback = new Handler();
        
        createOrLoadIndex();

        initViewMembers();  
        preloadIconBitmaps();

        _mapView.getOverlays().add( new TapListenerOverlay( this ) );
        
        _locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        initMyLocationOverview();

        registerLocationListener();

        if ( !_locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }         

        useLastKnownLocation( _locationManager );
        Log.v( TAG, "On create finished");
    }

    private void createOrLoadIndex() {

        final ProgressDialog pd = ProgressDialog.show( this, "Loading Station Data", "Loading station data, this might take some time on first run" );   

        pd.show();

        new Thread() {

            @Override
            public void run() {
                   try {
                        _finder.createIndex( getResources().openRawResource( R.raw.stations ) );
                    } catch ( final IOException e ) {
                        showException( e );
                    }

                    new SearcherThread( MainActivity.this, _searchRequestQueue ).start();

                    _uiThreadCallback.post( new Runnable() {
                        
                        public void run() {
                            pd.dismiss();
                        }
                    } );
                    
                }
            }.start();
        
    }

    private void preloadIconBitmaps() {
        _busBmp = BitmapFactory.decodeResource(getResources(),R.drawable.bus);
        _trainBmp = BitmapFactory.decodeResource(getResources(),R.drawable.train);
    }

    private void initViewMembers() {
        setContentView(R.layout.main);  
        _mapView = (MapView) findViewById(R.id.map);
        _mapView.setBuiltInZoomControls( true );
        _mc = _mapView.getController();
    }

    private void unregisterLocationListener() {
        _locationManager.removeUpdates( _listener );
    }

    private void registerLocationListener() {
        _locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 20, _listener );
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterLocationListener();
        _myLocationOverlay.disableMyLocation();
        _paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        _paused = false;
        registerLocationListener();
        _myLocationOverlay.enableMyLocation();
        updateLocation( _mapView.getMapCenter() );
    }

    @Override
    public boolean onCreateOptionsMenu( final Menu menu ) {
        menu.add(0, MENU_SHOW_POSITION, 0, "Show Position" ).setIcon( android.R.drawable.ic_menu_mylocation );
        menu.add(0, MENU_CONFIG, 0, "Configuration" ).setIcon( android.R.drawable.ic_menu_preferences );
        //menu.add(0, MENU_UPDATE, 0, "Update" ).setIcon( android.R.drawable.ic_menu_upload );

        return true;
    }

    /* Handles item selections */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SHOW_POSITION:
                showPosition();
                return true;
            case MENU_CONFIG: 
                showConfig();
                return true;
            case MENU_UPDATE:
                updateData();
                return true;

        }
        return false;
    }

    private void updateData() {

        final ProgressDialog pd = ProgressDialog.show( this, "Update in Progress", "Download and parsing file from OpenStreetMap (ca. 6 MB)..." );   

        pd.show();

        new Thread() {

            @Override
            public void run() {
                try {
                    final Stations newOSMStations = OSMUpdater.getNewOSMStations();

                    _uiThreadCallback.post( new Runnable() {
                        
                        public void run() {
                            pd.setMessage( "Updating Search Index" );
                        }
                    } );
                    
                    _finder.updateIndex( newOSMStations );

                    _uiThreadCallback.post( new Runnable() {
                        
                        public void run() {
                            pd.dismiss();
                        }
                    } );


                } catch ( final IOException e ) {
                    _uiThreadCallback.post( new Runnable() {
                        
                        public void run() {
                            pd.dismiss();
                            showException( e );
                        }
                    } );
                }

            } 

        }.start();
        


    }

    private void showConfig() {
        final Intent intend = new Intent( getApplicationContext(), ConfigActivity.class );
        startActivity( intend );
    }

    private void showPosition() {
        final GeoPoint myLocation = _myLocationOverlay.getMyLocation();

        if ( myLocation != null ) {
            updateLocation( myLocation );
            _mc.animateTo( myLocation );
        } else {
            showNoLocationDialog();
        }
    }

    private void useLastKnownLocation( final LocationManager manager ) {
        final Location lastKnownLocation = manager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
        if ( lastKnownLocation != null ) {
            updateLocation( lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude() );
        } else {
            //showNoLocationDialog();
        }
    }

    private void showNoLocationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage( "No location" ).setPositiveButton( "OK", new DialogInterface.OnClickListener() {

            public void onClick( final DialogInterface dialog, @SuppressWarnings("unused") final int which ) {
                dialog.cancel();
            }
        } );

        builder.show();
    }

    private void updateLocation( final GeoPoint myLocation ) {
        updateLocation( myLocation.getLatitudeE6() / 1E6D, myLocation.getLongitudeE6()  / 1E6D );   
    }

    public void getAndDisplayResultsFor( final GeoPoint currentCenter ) {
        getAndDisplayResultsFor(  currentCenter.getLatitudeE6() / 1E6D, currentCenter.getLongitudeE6()  / 1E6D );
    }

    void updateLocation( final double lat, final double lon ) {
        Log.d( TAG, "Starting updateLocation " + lat + ", " + lon );

        Log.d( TAG, "My Location: " + _myLocationOverlay.getMyLocation() );
        _mc.animateTo( new GeoPoint( (int) Math.round(lat * 1E6) , (int) Math.round(lon * 1E6) ));
        _mc.setZoom(DEFAULT_ZOOM_VALUE);  


        getAndDisplayResultsFor( lat, lon );
    }



    private void getAndDisplayResultsFor( final double lat, final double lon ) {
        if ( _paused ) return;

        _searchRequestQueue.add(  Position.valueOf( lat, lon ) );
        

    }

    private void launchGPSOptions() {
        final ComponentName toLaunch = new ComponentName("com.android.settings","com.android.settings.SecuritySettings");
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(toLaunch);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 0);
    }  


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Yout GPS seems to be disabled, do you want to enable it?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                launchGPSOptions(); 
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void initMyLocationOverview() {
        _myLocationOverlay = new MyLocationOverlay(this, _mapView );
        _myLocationOverlay.enableMyLocation();
        _myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                updateLocation( _myLocationOverlay.getMyLocation() );
            }


        });

    } 

    void showException( final Exception e ) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage( "Fatal Error: " + e.getMessage() ).setPositiveButton( "OK", new DialogInterface.OnClickListener() {

            public void onClick( final DialogInterface dialog, @SuppressWarnings("unused")  final int which ) {
                dialog.cancel();
            }
        } );

        builder.show();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    boolean isPaused() {
        return _paused;
        
    }



}