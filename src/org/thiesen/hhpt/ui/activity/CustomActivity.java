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

package org.thiesen.hhpt.ui.activity;

import java.util.List;

import org.thiesen.hhpt.common.GlobalConstants;
import org.thiesen.hhpt.config.CustomPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;

public class CustomActivity extends Activity {

    protected CustomPreferences preferences() {
        return new CustomPreferences(  getSharedPreferences( GlobalConstants.MY_PREFS,  0 ) );
    }
    
    protected boolean isIntentAvailable(final Context context, final String action ) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        final List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    protected Location getBestLastKnownLocation() {
        final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        
        final Location lastKnownGpsLocation = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
        
        if ( lastKnownGpsLocation != null ) {
            return lastKnownGpsLocation;
        }
        
        final Location lastKnownNetworkLocation = locationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
        
        if ( lastKnownNetworkLocation != null ) {
            return lastKnownNetworkLocation;
        }
        
        return null;

    
    }
    
    
    
}
