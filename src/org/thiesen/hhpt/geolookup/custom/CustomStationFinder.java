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
package org.thiesen.hhpt.geolookup.custom;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.thiesen.hhpt.geolookup.StationFinder;
import org.thiesen.hhpt.geolookup.remote.LookupException;
import org.thiesen.hhpt.shared.io.StationReader;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;

import android.location.Location;

public class CustomStationFinder implements StationFinder {

    public void createIndex( final InputStream openRawResource ) throws IOException {
        final StationReader reader = new StationReader( openRawResource );
        
        for ( final Station station : reader ) {
            
            
        }
        
    }

    public Stations makeGeoLookup( final double lat, final double lon, final double defaultSearchRadiusMiles ) throws LookupException {
        throw new UnsupportedOperationException("Auto generated method stub");
        
    }
  
    
    
}
