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


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

class LocationListenerImpl implements LocationListener {
    private final MainActivity _mainActivity;

    LocationListenerImpl( final MainActivity mainActivity ) {
        _mainActivity = mainActivity;
    }

    @SuppressWarnings("unused") 
    public void onStatusChanged( final String provider, final int status, final Bundle extras ) {
        // do nothing;
    }

    public void onProviderEnabled( @SuppressWarnings("unused")  final String provider ) {
        // do nothing;
    }

    public void onProviderDisabled( @SuppressWarnings("unused")  final String provider ) {
        // do nothing;
    }

    public void onLocationChanged( final Location location ) {
        if ( !_mainActivity.isPaused() && location != null ) {
            _mainActivity.updateLocation( location.getLatitude(), location.getLongitude() );
        }
    }
}