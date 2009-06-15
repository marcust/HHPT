package org.thiesen.hhpt.ui.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.thiesen.hhpt.geolookup.remote.LookupException;
import org.thiesen.hhpt.shared.io.StationReader;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;
import org.thiesen.hhpt.ui.map.StationMarkerLocationOverlay;
import org.thiesen.hhpt.ui.map.TapListenerOverlay;

import android.app.AlertDialog;
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
import com.pjaol.search.geo.utils.DistanceQuery;

public class MainActivity extends MapActivity {

    private class LocationListenerImpl implements LocationListener {
        public void onStatusChanged( final String provider, final int status, final Bundle extras ) {
            // do nothing;
        }

        public void onProviderEnabled( final String provider ) {
            // do nothing;
        }

        public void onProviderDisabled( final String provider ) {
            // do nothing;
        }

        public void onLocationChanged( final Location location ) {
            if ( !_paused && location != null ) {
                updateLocation( location.getLatitude(), location.getLongitude() );
            }
        }
    }


    public final static String TAG = "HHPT";

    private MapView _mapView;  
    private MapController _mc;  
    private final static int DEFAULT_ZOOM_VALUE = 17;

    public static final double DEFAULT_SEARCH_RADIUS_MILES = 1.0D;

    private static final int MENU_SHOW_POSITION = 1;
    private static final int MENU_CONFIG = 2;  

    private MyLocationOverlay _myLocationOverlay;

    private Bitmap _busBmp;

    private Bitmap _trainBmp;

    private Handler _uiThreadCallback;

    private boolean _paused;

    private IndexSearcher _searcher;


    @Override  
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            prepareIndex();
        } catch ( final IOException e ) {
            showException( e ); 
        }

        
        _uiThreadCallback = new Handler();

        setContentView(R.layout.main);  
        _mapView = (MapView) findViewById(R.id.map);
        _mapView.setBuiltInZoomControls( true );
        _mc = _mapView.getController();  

        _busBmp = BitmapFactory.decodeResource(getResources(),R.drawable.bus);
        _trainBmp = BitmapFactory.decodeResource(getResources(),R.drawable.train);

        _paused = false;

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        initMyLocationOverview();

        manager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 20, new LocationListenerImpl() );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }         

        updateLocation( 53.549758, 9.999323 );
        useLastKnownLocation( manager );
        Log.v( TAG, "On create finished");
    }


    private void prepareIndex() throws IOException {
        final InputStream stations = getResources().openRawResource( R.raw.stations );

        final StationReader reader = new StationReader( stations );
        final Directory d = new RAMDirectory();

        final IndexWriter writer = new IndexWriter( d, new StandardAnalyzer() );

        final long startTime = System.currentTimeMillis();
        for ( final Station s : reader ) {
            addPoint( writer, s );
        }

        writer.optimize();
        writer.close();

        _searcher = new IndexSearcher( d );

        System.out.println("Indexing took " + ( System.currentTimeMillis() - startTime ) );
    }

    /* Cited from Solr NumberUtil, Apache License */
    public static int long2sortableStr(final long ival, final char[] out, final int ioffset) {
        final long val = ival + Long.MIN_VALUE;
        int offset = ioffset;
        out[offset++] = (char)(val >>>60);
        out[offset++] = (char)(val >>>45 & 0x7fff);
        out[offset++] = (char)(val >>>30 & 0x7fff);
        out[offset++] = (char)(val >>>15 & 0x7fff);
        out[offset] = (char)(val & 0x7fff);
        return 5;
    }


    public static String long2sortableStr(final long val) {
        final char[] arr = new char[5];
        long2sortableStr(val,arr,0);
        return new String(arr,0,5);
    }

    public static String double2sortableStr(final double val) {
        long f = Double.doubleToRawLongBits(val);
        if (f<0) f ^= 0x7fffffffffffffffL;
        return long2sortableStr(f);
    }
    /* End Citation */




    @Override
    protected void onPause() {
        super.onPause();
        _paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        _paused = false;
    }

    @Override
    public boolean onCreateOptionsMenu( final Menu menu ) {
        menu.add(0, MENU_SHOW_POSITION, 0, "Show Position" );
        menu.add(0, MENU_CONFIG, 0, "Configuration" );

        return true;
    }

    /* Handles item selections */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SHOW_POSITION:
                final GeoPoint myLocation = _myLocationOverlay.getMyLocation();

                if ( myLocation != null ) {
                    _mc.animateTo( myLocation );
                    updateLocation( myLocation );
                } else {
                    showNoLocationDialog();
                }
                return true;
            case MENU_CONFIG: 
                final Intent intend = new Intent( getApplicationContext(), ConfigActivity.class );
                startActivity( intend );


                return true;
        }
        return false;
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

            public void onClick( final DialogInterface dialog, final int which ) {
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


    private void updateLocation( final double lat, final double lon ) {
        Log.d( TAG, "Starting updateLocation " + lat + ", " + lon );

        Log.d( TAG, "My Location: " + _myLocationOverlay.getMyLocation() );
        _mc.animateTo( new GeoPoint( (int) Math.round(lat * 1E6) , (int) Math.round(lon * 1E6) ));
        _mc.setZoom(DEFAULT_ZOOM_VALUE);  


        getAndDisplayResultsFor( lat, lon );


    }



    private void getAndDisplayResultsFor( final double lat, final double lon ) {
        // allows non-"edt" thread to be re-inserted into the "edt" queue

        if ( _paused ) return;


        final MainActivity that = this;


        new Thread() {
            @Override public void run() {

                try {
                    //final Stations results = GeoClient.makeGeoLookup( lat, lon , DEFAULT_SEARCH_RADIUS_MILES );

                    final Stations results = makeGeoLookup( lat, lon , DEFAULT_SEARCH_RADIUS_MILES );

                    Log.d( TAG, "Found " + results.size() + " points" );


                    final StationMarkerLocationOverlay overlay = new StationMarkerLocationOverlay( that, results, _busBmp, _trainBmp );


                    _uiThreadCallback.post( new Runnable() {

                        public void run() {
                            _mapView.getOverlays().clear();
                            _mapView.getOverlays().add( overlay );
                            _mapView.getOverlays().add(_myLocationOverlay);
                            _mapView.getOverlays().add( new TapListenerOverlay(that) );
                        }
                    });


                } catch ( final LookupException e ) {
                    _uiThreadCallback.post( new Runnable() {

                        public void run() {
                            showException( e );

                        }


                    });
                } 
            }


        }.start();


    }

    private Stations makeGeoLookup( final double lat, final double lon, final double defaultSearchRadiusMiles ) throws LookupException  {
        try {

            final DistanceQuery dq = new DistanceQuery(lat, lon, defaultSearchRadiusMiles, "lat", "lng", true );

            //perform a reqular search
            final Hits hits = _searcher.search( new FilteredQuery( new MatchAllDocsQuery(), dq.getFilter() ) );

            final Stations retval = new Stations();
            final Iterator<Hit> it = hits.iterator();
            while ( it.hasNext() ) {
                final Hit hit = it.next();


                retval.add( convert( hit.getDocument() ) );
            }

            return retval;

        } catch ( final IOException e ) {
            throw new LookupException( e );
        }
    }

    private Station convert( final Document doc ) {
        return Station.createStation( 
                doc.getField("id").stringValue(),
                doc.getField("lat").stringValue(),
                doc.getField("lng").stringValue(),
                doc.getField("type").stringValue(),
                doc.getField("name").stringValue(),
                doc.getField("operator").stringValue());
    }


    private static void addPoint(final IndexWriter writer, final Station s ) throws IOException{

        final org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();

        doc.add(new Field("id", s.getId().stringValue(),Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("name", s.getName(),Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("type", s.getType().toString(),Field.Store.YES, Field.Index.UN_TOKENIZED ));
        doc.add(new Field("operator", s.getOperator().stringValue(),Field.Store.YES, Field.Index.UN_TOKENIZED ));


        // convert the lat / long to lucene fields
        doc.add(new Field("lat", double2sortableStr(s.getPosition().getLatitude().doubleValue()),Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("lng", double2sortableStr(s.getPosition().getLongitude().doubleValue()),Field.Store.YES, Field.Index.UN_TOKENIZED));

        writer.addDocument(doc);
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

    private void showException( final Exception e ) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage( "Fatal Error: " + e.getMessage() ).setPositiveButton( "OK", new DialogInterface.OnClickListener() {

            public void onClick( final DialogInterface dialog, final int which ) {
                dialog.cancel();
            }
        } );

        builder.show();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }



}