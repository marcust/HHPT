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
package org.thiesen.hhpt.geolookup;

import java.io.IOException;
import java.io.InputStream;

import org.thiesen.hhpt.geolookup.remote.LookupException;
import org.thiesen.hhpt.shared.model.station.Stations;

public interface StationFinder {

    void createIndex( InputStream openRawResource ) throws IOException;

    Stations makeGeoLookup( double lat, double lon, double defaultSearchRadiusMiles ) throws LookupException;

}
