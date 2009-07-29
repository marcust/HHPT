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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.thiesen.hhpt.geolookup.LookupException;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;
import org.thiesen.hhpt.ui.map.overlay.BaseStationMarkerOverlay;

import android.graphics.Bitmap;

final class SearcherThread extends Thread {
    private final MainActivity _mainActivity;
    private final BlockingQueue<LookupPosition> _mySearchRequestQueue;
    private final Set<Station> _displayedStations;

    SearcherThread( final MainActivity mainActivity, final BlockingQueue<LookupPosition> requestQueue ) {
        _displayedStations = new HashSet<Station>();
        
        _mainActivity = mainActivity;
        _mySearchRequestQueue = requestQueue;
    }

    @Override public void run() {
        
        while ( true  ) {
        try {
            
            final LookupPosition position = _mySearchRequestQueue.take();
            
            final long startTime = System.currentTimeMillis();
   
            final Stations results = _mainActivity._finder.makeGeoLookup( position.getLatitude(), 
                                                            position.getLongitude(),
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