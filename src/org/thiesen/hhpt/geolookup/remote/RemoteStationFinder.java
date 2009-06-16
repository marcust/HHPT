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
package org.thiesen.hhpt.geolookup.remote;

import java.io.IOException;
import java.io.InputStream;

import org.thiesen.hhpt.geolookup.StationFinder;
import org.thiesen.hhpt.shared.model.station.Stations;

public class RemoteStationFinder implements StationFinder {

    @SuppressWarnings("unused")
    public void createIndex( final InputStream openRawResource ) throws IOException {
        // do nothing here;
    }

    public Stations makeGeoLookup( final double lat, final double lon, final double defaultSearchRadiusMiles ) throws LookupException {
        return GeoClient.makeGeoLookup( lat, lon, defaultSearchRadiusMiles );
    }
    

}
