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
package org.thiesen.hhpt.geolookup.geohash;

import java.io.Serializable;

import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;

public class TreeNode implements Serializable {
    
    private static final long serialVersionUID = -5504770098867210464L;

    
    private static final int PRECISION = 3;
    
    private final TreeNode[] _parts = new TreeNode[32]; 
    
    private Stations _values;
    
    public void addNode( final int[] poshash, final Station s ) {
        addNode( poshash, s, 0 );
    }
    
    private void addNode( final int[] poshash, final Station s, final int i ) {
        if ( i < PRECISION ) {
            final int pos = poshash[i];
            
            if ( _parts[pos] == null ) {
                _parts[pos] = new TreeNode();
            }
            
            _parts[pos].addNode( poshash, s, i + 1 );
            
            
        } else {
            if ( _values == null ) {
                _values = new Stations();
            }
            _values.add( s );
        }
        
    }

    public Stations lookup( final int[] poshash ) {
        return lookup( poshash, 0 );
    }

    private Stations lookup( final int[] poshash, final int i ) {
        if ( i < PRECISION ) {
            final TreeNode childNode =  _parts[ poshash[i] ];
            return childNode != null ? childNode.lookup( poshash , i + 1 ) : null;
        } 
        return _values;
        
        
    }

}
