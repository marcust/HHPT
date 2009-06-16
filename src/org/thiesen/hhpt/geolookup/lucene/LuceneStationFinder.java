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
package org.thiesen.hhpt.geolookup.lucene;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Iterator;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.thiesen.hhpt.geolookup.StationFinder;
import org.thiesen.hhpt.geolookup.remote.LookupException;
import org.thiesen.hhpt.shared.io.StationReader;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;

import android.content.Context;

import com.pjaol.search.geo.utils.DistanceQuery;

public class LuceneStationFinder implements StationFinder {

    private static final String  INDEX_FILE_NAME = "index_1.ser";
    
    private Searcher _searcher;
    private final Context _context;
    
    public LuceneStationFinder( final Context context ) {
        _context = context;
    }
    
    public void createIndex( final InputStream stations ) throws IOException {
        final long startTime = System.currentTimeMillis();
        
        Directory d = tryLoadDirectory();

        if ( d == null ) {
            d = createDirectory( stations );
        
            storeDirectory( d );
            
           
        }
        _searcher = new IndexSearcher( d );

        System.out.println("Indexing took " + ( System.currentTimeMillis() - startTime ) );
    }                                                                                      

    private void storeDirectory( final Directory d ) {
            try {
                final FileOutputStream openFileOutput = _context.openFileOutput( INDEX_FILE_NAME, Context.MODE_PRIVATE );
                
                final ObjectOutputStream oout = new ObjectOutputStream( openFileOutput );
                
                oout.writeObject( d );
                
                oout.close();
                
            } catch ( final FileNotFoundException e ) {
                e.printStackTrace();
            } catch ( final IOException e ) {
                e.printStackTrace();
            }
            
            
    }

    private Directory tryLoadDirectory() {
        try {
            final FileInputStream openFileInput = _context.openFileInput( INDEX_FILE_NAME );
            
            final ObjectInputStream inputStream = new ObjectInputStream( openFileInput );
            
            return (Directory) inputStream.readObject();
            
            
        } catch ( final FileNotFoundException e ) {
            e.printStackTrace();
            return null;
        } catch ( final StreamCorruptedException e ) {
            e.printStackTrace();
            return null;
        } catch ( final IOException e ) {
            e.printStackTrace();
            return null;
        } catch ( final ClassNotFoundException e ) {
            e.printStackTrace();
            return null;
        }
        
    }

    private Directory createDirectory( final InputStream stations ) throws IOException {
        final StationReader reader = new StationReader( stations );
        final Directory d = new RAMDirectory();                    

        final IndexWriter writer = new IndexWriter( d, new StandardAnalyzer() );

        for ( final Station s : reader ) {                
            addPoint( writer, s );                        
        }                                                 

        writer.optimize();
        writer.close();   

        return d;
        
    }

    /* Cited from Solr NumberUtil, Apache License */
    public static int long2sortableStr(final long ival, final char[] out, final int ioffset) {
        final long val = ival + Long.MIN_VALUE;                                               
        int offset = ioffset;                                                                 
        out[offset++] = (char)(val >>>60);                                                    
        out[offset++] = (char)(val >>>45 & 0x7fff);                                           
        out[offset++] = (char)(val >>>30 & 0x7fff);                                           
        out[offset++] = (char)(val >>>15 & 0x7fff);                                           
        out[offset] = (char)(val & 0x7fff);                                                   
        return 5;                                                                             
    }                                                                                         


    public static String long2sortableStr(final long val) {
        final char[] arr = new char[5];                    
        long2sortableStr(val,arr,0);                       
        return new String(arr,0,5);                        
    }                                                      

    public static String double2sortableStr(final double val) {
        long f = Double.doubleToRawLongBits(val);              
        if (f<0) f ^= 0x7fffffffffffffffL;                     
        return long2sortableStr(f);                            
    }                                                          
    /* End Citation */                                         


    @SuppressWarnings("unchecked")
    public Stations makeGeoLookup( final double lat, final double lon, final double defaultSearchRadiusMiles ) throws LookupException  {
        try {                                                                                                                            

            final DistanceQuery dq = new DistanceQuery(lat, lon, defaultSearchRadiusMiles, "lat", "lng", true );

            //perform a reqular search
            final Hits hits = _searcher.search( new MatchAllDocsQuery(), dq.getFilter() );

            System.out.println( "Found " + hits.length() + " hits" );
            
            final Stations retval = new Stations();
            final Iterator<Hit> it = hits.iterator();
            while ( it.hasNext() ) {                 
                final Hit hit = it.next();           


                retval.add( convert( hit.getDocument() ) );
            }                                              

            return retval;

        } catch ( final IOException e ) {
            throw new LookupException( e );
        }                                  
    }                                      

    private Station convert( final Document doc ) {
        return Station.createStation(              
                doc.getField("id").stringValue(),  
                doc.getField("lat_plain").stringValue(), 
                doc.getField("lng_plain").stringValue(), 
                doc.getField("type").stringValue(),
                doc.getField("name").stringValue(),
                doc.getField("operator").stringValue());
    }                                                   


    private static void addPoint(final IndexWriter writer, final Station s ) throws IOException{

        final org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();

        doc.add(new Field("id", s.getId().stringValue(),Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("name", s.getName(),Field.Store.YES, Field.Index.UN_TOKENIZED));          
        doc.add(new Field("type", s.getType().toString(),Field.Store.YES, Field.Index.UN_TOKENIZED ));
        doc.add(new Field("operator", s.getOperator().stringValue(),Field.Store.YES, Field.Index.UN_TOKENIZED ));
        doc.add(new Field("lat_plain", s.getPosition().getLatitude().toString(),Field.Store.YES, Field.Index.UN_TOKENIZED ));
        doc.add(new Field("lng_plain", s.getPosition().getLongitude().toString(),Field.Store.YES, Field.Index.UN_TOKENIZED ));


        // convert the lat / long to lucene fields
        doc.add(new Field("lat", double2sortableStr(s.getPosition().getLatitude().doubleValue()),Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("lng", double2sortableStr(s.getPosition().getLongitude().doubleValue()),Field.Store.YES, Field.Index.UN_TOKENIZED));

        writer.addDocument(doc);
    }                           



}
