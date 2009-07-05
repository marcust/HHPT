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
package org.thiesen.hhpt.geolookup.mock;

import org.thiesen.hhpt.geolookup.LookupException;
import org.thiesen.hhpt.geolookup.StationFinder;
import org.thiesen.hhpt.geolookup.StationFinderBase;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;

public class MockStationFinder extends StationFinderBase implements StationFinder {

    @Override
    protected void indexOneStation( final Station s ) {
        // do nothiung
    }

    public Stations makeGeoLookup( final double lat, final double lon, final double defaultSearchRadiusMiles ) throws LookupException {
        return new Stations();
    }

}
