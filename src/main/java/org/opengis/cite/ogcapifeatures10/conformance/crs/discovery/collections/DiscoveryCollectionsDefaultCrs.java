package org.opengis.cite.ogcapifeatures10.conformance.crs.discovery.collections;

import static org.opengis.cite.ogcapifeatures10.EtsAssert.assertDefaultCrs;
import static org.opengis.cite.ogcapifeatures10.OgcApiFeatures10.DEFAULT_CRS_CODE;
import static org.opengis.cite.ogcapifeatures10.OgcApiFeatures10.DEFAULT_CRS_WITH_HEIGHT_CODE;
import static org.opengis.cite.ogcapifeatures10.util.JsonUtils.hasAtLeastOneSpatialFeatureCollection;

import java.util.List;
import java.util.Map;

import org.opengis.cite.ogcapifeatures10.openapi3.TestPoint;
import org.opengis.cite.ogcapifeatures10.util.JsonUtils;
import org.testng.SkipException;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;

/**
 * Verifies objects in the paths /collections
 *
 * <pre>
 * Abstract Test 2: /conf/crs/default-crs
 * Test Purpose: Verify that the list of supported CRSs includes the default CRS.
 * Requirement: /req/crs/fc-md-crs-list B
 *
 * Test Method
 * For each string value in a crs property in a collection object (for each path /collections and /collections/{collectionId})
 * validate that either
 * http://www.opengis.net/def/crs/OGC/1.3/CRS84 or
 * http://www.opengis.net/def/crs/OGC/1.3/CRS84h
 * is included in the array, if the collection has a spatial extent, i.e., is a spatial feature collection.
 * </pre>
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class DiscoveryCollectionsDefaultCrs extends AbstractDiscoveryCollections {

    /**
     * Test: crs property in the collections object in the path /collections
     *
     * @param testPoint
     *            test point to test, never <code>null</code>
     * @param jsonPath
     *            the /collections JSON, never <code>null</code>
     */
    @Test(description = "Implements A.1 Discovery, Abstract Test 2 (Requirement /req/crs/fc-md-crs-list B), "
                        + "crs property contains default crs in the collections object in the path /collections", dataProvider = "collectionsResponses", dependsOnGroups = "crs-conformance")
    public void verifyCollectionsPathCrsPropertyContainsDefaultCrs( TestPoint testPoint, JsonPath jsonPath ) {
        if ( hasAtLeastOneSpatialFeatureCollection( jsonPath ) ) {
            List<String> crs = JsonUtils.parseAsList( "crs", jsonPath );
            assertDefaultCrs( crs,
                              String.format( "Collections path %s does not specify one of the default CRS '%s' or '%s' but provides at least one spatial feature collections",
                                             testPoint.getPath(), DEFAULT_CRS_CODE, DEFAULT_CRS_WITH_HEIGHT_CODE ) );
        }
    }

    /**
     * Test: crs property in the collection objects in the path /collections
     *
     * @param testPoint
     *            test point to test, never <code>null</code>
     * @param jsonPath
     *            the /collections JSON, never <code>null</code>
     * @param collection
     *            the /collection object, never <code>null</code>
     */
    @Test(description = "Implements A.1 Discovery, Abstract Test 2 (Requirement /req/crs/fc-md-crs-list B), "
                        + "crs property contains default crs in the collection objects in the path /collections", dataProvider = "collectionItemUris", dependsOnGroups = "crs-conformance")
    public void verifyCollectionsPathCollectionCrsPropertyContainsDefaultCrs( TestPoint testPoint, JsonPath jsonPath,
                                                                              Map<String, Object> collection ) {
        if ( hasAtLeastOneSpatialFeatureCollection( collection ) ) {
            String collectionId = (String) collection.get( "id" );
            List<String> crs = JsonUtils.parseAsList( "crs", collection );
            if ( crs.size() == 1 && "#/crs".equals( crs.get( 0 ) ) ) {
                throw new SkipException( String.format( "Collection with id '%s' at collections path %s references to global crs section.",
                                                        collectionId, testPoint.getPath() ) );
            } else {
                assertDefaultCrs( crs,
                                  String.format( "Collection with id '%s' at collections path %s does not specify one of the default CRS '%s' or '%s' but provides at least one spatial feature collections",
                                                 collectionId, testPoint.getPath(), DEFAULT_CRS_CODE,
                                                 DEFAULT_CRS_WITH_HEIGHT_CODE ) );
            }
        }
    }

}