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

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.NumberUtils;
import org.apache.lucene.spatial.tier.LatLongDistanceFilter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.thiesen.hhpt.geolookup.LookupException;
import org.thiesen.hhpt.geolookup.StationFinder;
import org.thiesen.hhpt.shared.io.StationReader;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;

import android.content.Context;

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
        _searcher = new IndexSearcher( d, true );

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
        return createNewDirectory( reader );

    }

    private Directory createNewDirectory( final Iterable<Station> stations ) throws CorruptIndexException, LockObtainFailedException, IOException {
        final Directory d = new RAMDirectory();                    

        final IndexWriter writer = new IndexWriter( d, new SimpleAnalyzer(), MaxFieldLength.LIMITED );

        for ( final Station s : stations ) {                
            addPoint( writer, s );                        
        }                                                 

        writer.optimize();
        writer.close();   

        return d;
    }


    public Stations makeGeoLookup( final double lat, final double lon, final double defaultSearchRadiusMiles ) throws LookupException  {
        try {                                                                                                                            

            final LatLongDistanceFilter filter = new LatLongDistanceFilter( lat, lon, defaultSearchRadiusMiles, "lat", "lng" );

            final TopDocs topDocs = _searcher.search( new MatchAllDocsQuery(), filter, 1000 );

            final ScoreDoc[] hits = topDocs.scoreDocs;
            
            System.out.println( "Found " + hits.length + " hits" );

            final Stations retval = new Stations();

            for ( int i = 0; i < hits.length; i++ ) {
                retval.add( convert( _searcher.doc( hits[i].doc ) ) );
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


    @SuppressWarnings( "deprecation" ) // there doesnt seem to be an alternative right now
    private static void addPoint(final IndexWriter writer, final Station s ) throws IOException{

        final org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();

        doc.add(new Field("id", s.getId().stringValue(),Field.Store.YES, Field.Index.NOT_ANALYZED  ));
        doc.add(new Field("name", s.getName(),Field.Store.YES, Field.Index.NOT_ANALYZED ));          
        doc.add(new Field("type", s.getType().toString(),Field.Store.YES, Field.Index.NOT_ANALYZED  ));
        doc.add(new Field("operator", s.getOperator().stringValue(),Field.Store.YES, Field.Index.NOT_ANALYZED  ));
        doc.add(new Field("lat_plain", s.getPosition().getLatitude().toString(),Field.Store.YES, Field.Index.NOT_ANALYZED  ));
        doc.add(new Field("lng_plain", s.getPosition().getLongitude().toString(),Field.Store.YES, Field.Index.NOT_ANALYZED  ));


        // convert the lat / long to lucene fields
        doc.add(new Field("lat", NumberUtils.double2sortableStr(s.getPosition().getLatitude().doubleValue()),Field.Store.YES, Field.Index.NOT_ANALYZED ));
        doc.add(new Field("lng", NumberUtils.double2sortableStr(s.getPosition().getLongitude().doubleValue()),Field.Store.YES, Field.Index.NOT_ANALYZED ));

        writer.addDocument(doc);
    }

    public void updateIndex( final Stations newOSMStations ) throws IOException {
        if ( newOSMStations.isNotEmpty() ) {

            final Directory d = createNewDirectory( newOSMStations );
            storeDirectory( d );

            _searcher = new IndexSearcher( d, true );
        }

    }                           



}
