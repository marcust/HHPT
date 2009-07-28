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

package org.thiesen.hhpt.ui.map;

import org.thiesen.hhpt.ui.activity.main.MainActivity;

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
    public boolean onTouchEvent( @SuppressWarnings("unused") final MotionEvent e, final MapView mapView ) {
        if ( _oldCenter == null ) {
            _oldCenter = mapView.getMapCenter();
        }
        
        
        final GeoPoint currentCenter = mapView.getMapCenter();

        //Log.d( MainActivity.TAG, "Got a on Touch event " + currentCenter );

        
        Location.distanceBetween( _oldCenter.getLatitudeE6() / 1E6D, 
                                   _oldCenter.getLongitudeE6() / 1E6D, 
                                   currentCenter.getLatitudeE6() / 1E6D,
                                   currentCenter.getLongitudeE6() / 1E6D,
                                   _results ); 
        
        
        //Log.d( MainActivity.TAG, "Distance is " + _results[0] + " m" );
        
        if ( ( _results[0] / 1000 ) * 0.621371192 > MainActivity.DEFAULT_SEARCH_RADIUS_MILES / 2 ) {
            _oldCenter = currentCenter;
            _shower.getAndDisplayResultsFor( currentCenter );
        }
        
        return false;
        
    }
    
    

    
}
