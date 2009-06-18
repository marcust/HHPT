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