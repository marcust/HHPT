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
package org.thiesen.hhpt.ui.map;

import org.thiesen.hhpt.ui.activity.MainActivity;

import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class TapListenerOverlay extends Overlay {

    
    private GeoPoint _oldCenter;
    private final float[] _results = new float[1];
    
    private final MainActivity _shower;
    

    public TapListenerOverlay( final MainActivity shower ) {
        _shower = shower;
    }
    
    @Override
    public boolean onTouchEvent( final MotionEvent e, final MapView mapView ) {
        if ( _oldCenter == null ) {
            _oldCenter = mapView.getMapCenter();
        }
        
        
        final GeoPoint currentCenter = mapView.getMapCenter();

        Log.d( MainActivity.TAG, "Got a on Touch event " + currentCenter );

        
        Location.distanceBetween( _oldCenter.getLatitudeE6() / 1E6D, 
                                   _oldCenter.getLongitudeE6() / 1E6D, 
                                   currentCenter.getLatitudeE6() / 1E6D,
                                   currentCenter.getLongitudeE6() / 1E6D,
                                   _results ); 
        
        
        Log.d( MainActivity.TAG, "Distance is " + _results[0] + " m" );
        
        if ( ( _results[0] / 1000 ) * 0.621371192 > MainActivity.DEFAULT_SEARCH_RADIUS_MILES ) {
            _oldCenter = currentCenter;
            _shower.getAndDisplayResultsFor( currentCenter );
        }
        
        return false;
        
    }
    
    

    
}
