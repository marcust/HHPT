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


public class Miles {

    private final Double _valueInMiles;
    
    private Miles( final Double valueInMiles ) {
        _valueInMiles = valueInMiles;
    }
    
    public static Miles valueOf( final Double valueInMiles ) {
        return new Miles( valueInMiles );
    }
    
    public String getValue() {
        return _valueInMiles.toString();
    }

}
