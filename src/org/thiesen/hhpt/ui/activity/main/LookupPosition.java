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


public class LookupPosition {
    
    private final double _latitude;
    private final double _longitude;
  
    public LookupPosition( final double latitude, final double longitude ) {
        super();
        _latitude = latitude;
        _longitude = longitude;
    }
    
    public double getLatitude() {
        return _latitude;
    }
    public double getLongitude() {
        return _longitude;
    }
    
    
    
}
