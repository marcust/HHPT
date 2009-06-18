/*
 * $ Id $
 * (c) Copyright 2009 freiheit.com technologies gmbh
 *
 * This file contains unpublished, proprietary trade secret information of
 * freiheit.com technologies gmbh. Use, transcription, duplication and
 * modification are strictly prohibited without prior written consent of
 * freiheit.com technologies gmbh.
 *
 * Initial version by Marcus Thiesen (marcus.thiesen@freiheit.com)
 */
package org.thiesen.hhpt.ui.activity.main;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.thiesen.hhpt.geolookup.LookupException;
import org.thiesen.hhpt.shared.model.position.Position;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;
import org.thiesen.hhpt.ui.map.overlay.BaseStationMarkerOverlay;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.util.Log;

final class SearcherThread extends Thread {
    private final MainActivity _mainActivity;
    private final BlockingQueue<Position> _mySearchRequestQueue;
    private final Set<Station> _displayedStations;

    SearcherThread( final MainActivity mainActivity, final BlockingQueue<Position> requestQueue ) {
        _displayedStations = new HashSet<Station>();
        
        _mainActivity = mainActivity;
        _mySearchRequestQueue = requestQueue;
    }

    @Override public void run() {
        
        while ( true  ) {
        try {
            
            final Position position = _mySearchRequestQueue.take();
            
            final long startTime = System.currentTimeMillis();
   
            final Stations results = _mainActivity._finder.makeGeoLookup( position.getLatitude().doubleValue(), 
                                                            position.getLongitude().doubleValue(),
                                                            MainActivity.DEFAULT_SEARCH_RADIUS_MILES );

            System.out.println("Searching took " + ( System.currentTimeMillis() - startTime ) + " ms, found " + results.size() + " points" );

            for ( final Station s : results ) {
                addOverlayToMapView( s );
            }


          
          
        } catch ( final LookupException e ) {
            _mainActivity._uiThreadCallback.post( new Runnable() {

                public void run() {
                    SearcherThread.this._mainActivity.showException( e );

                }


            });
        } catch ( final InterruptedException e ) {
           return;
        } 
    }
    }

    private void addOverlayToMapView( final Station s ) {
        if ( _displayedStations.contains( s ) ) {
            return;
        }
        _displayedStations.add( s );
        
        final Bitmap marker = s.isBus() ? _mainActivity._busBmp : _mainActivity._trainBmp;
        
        final BaseStationMarkerOverlay overlay = new BaseStationMarkerOverlay( _mainActivity, marker, s );

        _mainActivity._uiThreadCallback.post( new Runnable() {

            public void run() {
                SearcherThread.this._mainActivity._mapView.getOverlays().add( overlay );
            }
        });

        
    }

}