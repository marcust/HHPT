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
package org.thiesen.hhpt.common;

import org.thiesen.hhpt.shared.model.station.Station;

import com.google.android.maps.GeoPoint;

public class GeoLocationUtils {

    public static GeoPoint pointFromStation( final Station testLocation ) {
        return new GeoPoint( testLocation.getPosition().getLatitudeE6(), testLocation.getPosition().getLongitudeE6() );
        
    }

}
