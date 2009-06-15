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
package org.thiesen.hhpt.beans;


public enum Carriers {

 
    VODAFONE( "Vodafone", "+491738829999" ),
    TMOBILE( "T-Mobile", "+491753609999" ),
    O2( "O2", "+491783609999" ),
    EPLUS( "E-Plus", "+1783609999" );
            
    private final String _number;
    private final String _name;
    
    private Carriers( final String name, final String number ) {
        _name = name;
        _number = number;
    }
    
    public String getNumber() {
        return _number;
    }

    public String getName() {
        return _name;
    }
    
}
