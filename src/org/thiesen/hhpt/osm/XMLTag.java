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
package org.thiesen.hhpt.osm;


public class XMLTag {
    
    private final String _key;
    private final String _value;
    
    private XMLTag( final String key, final String value ) {
        _key = key;
        _value = value;
    }

    public static XMLTag valueOf( final String key, final String value ) {
        return new XMLTag( key, value );
        
    }

    public String getValue() {
        return _value;
        
    }

    public String getKey() {
        return _key;
        
    }

    public boolean keyIs( final String key ) {
        return _key.equalsIgnoreCase( key );
        
    }

    public boolean keyAnyOf( final Iterable<String> keys ) {
        for ( final String key : keys ) {
            if ( _key.equalsIgnoreCase( key ) ) {
                return true;
            }
        }
        return false;
    }

}
