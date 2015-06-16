/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static com.netsol.adagent.util.beans.BaseData.arrayIsEmpty;
import static com.netsol.adagent.util.beans.BaseData.collectionIsEmpty;
import static com.netsol.adagent.util.beans.BaseData.collectionIsNotEmpty;
import static com.netsol.adagent.util.beans.BaseData.filter;
import static com.netsol.adagent.util.beans.BaseData.stringIsBlank;
import static com.netsol.adagent.util.beans.BaseData.stringIsEmpty;
import static com.netsol.adagent.util.beans.BaseData.stringIsNotEmpty;
import static com.netsol.adagent.util.beans.BaseData.stringsEqual;
import static com.netsol.adagent.util.beans.BaseData.stringsEqualIgnoreCase;
import static com.netsol.adagent.util.beans.BaseData.toIterable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.CallTrackingFeatures;
import com.netsol.adagent.util.beans.InterceptorFeatures;
import com.netsol.adagent.util.beans.InterceptorMapping;
import com.netsol.adagent.util.beans.InterceptorReplacement;
import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.beans.Quadruple;
import com.netsol.adagent.util.beans.Triple;
import com.netsol.adagent.util.codes.LeadTrackingType;
import com.netsol.adagent.util.codes.LimitType;
import com.netsol.adagent.util.codes.ProdId;
import com.netsol.adagent.util.codes.ReplacementType;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.CallTrackingFeaturesHelper;
import com.netsol.adagent.util.dbhelpers.InterceptorFeaturesHelper;
import com.netsol.adagent.util.dbhelpers.InterceptorMappingHelper;
import com.netsol.adagent.util.dbhelpers.InterceptorReplacementHelper;
import com.netsol.adagent.util.dbhelpers.ProductSecondaryUrlHelper;
import com.netsol.adagent.util.dbhelpers.TopLevelDomainHelper;
import com.netsol.adagent.util.log.BaseLoggable;
import com.netsol.adagent.util.log.SimpleLoggable;
import com.netsol.adagent.util.tracking.ParamAdder;
import com.netsol.adagent.util.tracking.ParamStripper;
import com.netsol.adagent.util.tracking.ParamTools;

public class TrackingUtil extends BaseLoggable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:35 TrackingUtil.java NSI";

    private final static String HTTP_SCHEME = "http";
    private final static String HTTPS_SCHEME = "https";
    private final static String SCHEME_DELIMITER = "://";
    private final static int SCHEME_DELIMITER_LENGTH = SCHEME_DELIMITER.length();
    private final static String HTTP_SCHEME_PLUS_DELIMITER = HTTP_SCHEME + SCHEME_DELIMITER;
    private final static String INTERCEPTOR_DOMAIN = "netsolads.com";
    private static final String PHONE_NUMBER_SEPARATOR_REGEX = "[\\s\\./-]";

    private final CallTrackingFeaturesHelper callTrackingFeaturesHelper;
    private final InterceptorFeaturesHelper interceptorFeaturesHelper;
    private final InterceptorMappingHelper interceptorMappingHelper;
    private final InterceptorReplacementHelper interceptorReplacementHelper;
    private final ProductHelper productHelper;
    private final ProductSecondaryUrlHelper productSecondaryUrlHelper;
    private final TopLevelDomainHelper topLevelDomainHelper;

    /**
     * Constructor.
     */
    public TrackingUtil(String logComponent) {
        this(new SimpleLoggable(logComponent));
    }

    /**
     * Constructor.
     */
    public TrackingUtil(String logComponent, boolean logSqlStatements) {
        this(new SimpleLoggable(logComponent), Boolean.valueOf(logSqlStatements));
    }

    /**
     * Constructor.
     */
    public TrackingUtil(Log logger) {
        this(new SimpleLoggable(logger));
    }

    /**
     * Constructor.
     */
    public TrackingUtil(BaseLoggable baseLoggable) {
        this(baseLoggable, null);
    }

    /**
     * Constructor.
     */
    private TrackingUtil(BaseLoggable baseLoggable, Boolean logSqlStatements) {
        super(baseLoggable);

        callTrackingFeaturesHelper = new CallTrackingFeaturesHelper(baseLoggable);
        interceptorFeaturesHelper = new InterceptorFeaturesHelper(baseLoggable);
        interceptorMappingHelper = new InterceptorMappingHelper(baseLoggable);
        interceptorReplacementHelper = new InterceptorReplacementHelper(baseLoggable);
        productHelper =
            (logSqlStatements == null) ? new ProductHelper(baseLoggable) : new ProductHelper(baseLoggable, logSqlStatements.booleanValue());
        productSecondaryUrlHelper = new ProductSecondaryUrlHelper(baseLoggable);
        topLevelDomainHelper = new TopLevelDomainHelper(baseLoggable);
    }

    /**
     * Delete the Interceptor replacement for the specified Interceptor mapping.
     */
    public void deleteInterceptorReplacementForMapping(String logTag, Connection connection, InterceptorMapping interceptorMapping) throws SQLException {
        interceptorReplacementHelper.deleteReplacement(logTag, connection, getInterceptorReplacementForMapping(interceptorMapping));
    }

    /**
     * Delete the Interceptor mapping for the specified alias and product instance ID.
     */
    public void deleteInterceptorMappingByAliasAndProdInstId(String logTag, Connection connection, String alias, String prodInstId) throws SQLException {
        interceptorMappingHelper.deleteMappingByAliasAndProdInstId(logTag, connection, alias, prodInstId);
    }

    /**
     * Delete the Interceptor mappings for the specified alias.
     */
    public void deleteInterceptorMappingsByAlias(String logTag, Connection connection, String alias) throws SQLException {
        interceptorMappingHelper.deleteMappingByAlias(logTag, connection, alias);
    }

    /**
     * Delete the Interceptor settings for the specified product instance ID.
     */
    public void deleteInterceptorSettingsByProdInstId(String logTag, Connection connection, String prodInstId) throws SQLException {
        interceptorFeaturesHelper.deleteFeatures(logTag, connection, prodInstId);
        interceptorMappingHelper.deleteMappingsByProdInstId(logTag, connection, prodInstId);
        interceptorReplacementHelper.deleteReplacementsByProdInstIdAndReplacementType(logTag, connection, prodInstId, ReplacementType.URL);
    }

    /**
     * Return all of a product's Interceptor aliases.
     */
    public Collection<String> getAllAliases(String logTag, Connection connection, String prodInstId) throws SQLException {
        Collection<InterceptorMapping> interceptorMappings = getAllInterceptorMappings(logTag, connection, prodInstId);
        Collection<String> interceptorAliases = new ArrayList<String>(interceptorMappings.size());
        for (InterceptorMapping interceptorMapping : interceptorMappings) {
            interceptorAliases.add(interceptorMapping.getAlias());
        }
        return Collections.unmodifiableCollection(interceptorAliases);
    }

    /**
     * Return all of a product's Interceptor mappings.
     */
    public Collection<InterceptorMapping> getAllInterceptorMappings(String logTag, Connection connection, String prodInstId) throws SQLException {
        return interceptorMappingHelper.getMappingsByProdInstId(logTag, connection, prodInstId);
    }

    /**
     * Return a product's alternate Interceptor mapping.
     */
    public InterceptorMapping getAlternateInterceptorMapping(String logTag, Connection connection, String prodInstId, List<String> validTopLevelDomainNames) throws SQLException {
        String hostName = extractHostName(getProductUrl(logTag, connection, prodInstId));
        if (stringIsEmpty(hostName)) {
            return null;
        }
        String alternateHostName = getAlternateHostName(validTopLevelDomainNames, hostName);
        if (stringIsEmpty(alternateHostName)) {
            return null;
        }
        return getInterceptorMapping(prodInstId, alternateHostName);
    }

    /**
     * Return the NS destination URL and valid host flag for the specified vendor ad destination URL.
     */
    public Pair<String, Boolean> getNsDestinationUrlAndValidHostFlagFromVendor(String logTag, Connection pdbConnection, String prodInstId, String vendorDestinationUrl) throws SQLException {
        List<Triple<String, String, Boolean>> nsLandingPagesHostsAndValidHostFlags = getNsLandingPagesHostsAndValidHostFlagsFromVendor(logTag, pdbConnection, prodInstId, Collections.singleton(vendorDestinationUrl));
        if (collectionIsEmpty(nsLandingPagesHostsAndValidHostFlags)) {
            return null;
        }
        Triple<String, String, Boolean> nsLandingPageHostAndValidHostFlag = nsLandingPagesHostsAndValidHostFlags.get(0);
        String landingPage = nsLandingPageHostAndValidHostFlag.getFirst();
        String host = nsLandingPageHostAndValidHostFlag.getSecond();
        return Pair.from(stringIsEmpty(host) ? landingPage : host + landingPage, nsLandingPageHostAndValidHostFlag.getThird());
    }

    /**
     * Return the NS destination URL for the specified vendor ad destination URL.
     */
    public String getNsDestinationUrlFromVendor(String logTag, Connection pdbConnection, String prodInstId, String vendorDestinationUrl) throws SQLException {
        Pair<String, Boolean> nsDestinationUrlAndValidHostFlag = getNsDestinationUrlAndValidHostFlagFromVendor(logTag, pdbConnection, prodInstId, vendorDestinationUrl);
        return (nsDestinationUrlAndValidHostFlag == null) ? null : nsDestinationUrlAndValidHostFlag.getFirst();
    }

    /**
     * Return the NS host and landing page from the specified vendor destination URL.
     */
    public Pair<String, String> getNsHostAndLandingPageFromVendorDestinationUrl(String logTag, Connection pdbConnection, String prodInstId, String vendorDestinationUrl) throws SQLException {
        List<Pair<String, String>> nsHostsAndLandingPages = getNsHostsAndLandingPagesFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, new String[] {vendorDestinationUrl});
        if (collectionIsEmpty(nsHostsAndLandingPages)) {
            return null;
        }
        return nsHostsAndLandingPages.get(0);
    }

    /**
     * Return the NS host plus landing page from the specified vendor destination URL removing any valid host.
     */
    public String getNsHostPlusLandingPageFromVendorDestinationUrlRemovingValidHost(String logTag, Connection pdbConnection, String prodInstId, String vendorDestinationUrl) throws SQLException {
        String[] nsHostsPlusLandingPages = getNsHostsPlusLandingPagesFromVendorDestinationUrlsRemovingValidHosts(logTag, pdbConnection, prodInstId, new String[] {vendorDestinationUrl});
        if (arrayIsEmpty(nsHostsPlusLandingPages)) {
            return null;
        }
        return nsHostsPlusLandingPages[0];
    }

    /**
     * Return the NS hosts and landing pages from the specified vendor destination URLs.
     */
    public List<Pair<String, String>> getNsHostsAndLandingPagesFromVendorDestinationUrl(String logTag, Connection pdbConnection, String prodInstId, Iterable<String> vendorDestinationUrls) throws SQLException {
        List<Triple<String, String, Boolean>> nsLandingPagesHostsAndValidHostFlags = getNsLandingPagesHostsAndValidHostFlagsFromVendor(logTag, pdbConnection, prodInstId, vendorDestinationUrls);
        if (nsLandingPagesHostsAndValidHostFlags == null) {
            return null;
        }

        List<Pair<String, String>> nsHostsAndLandingPages = new ArrayList<Pair<String, String>>(nsLandingPagesHostsAndValidHostFlags.size());
        for (Triple<String, String, Boolean> nsLandingPageHostAndValidHostFlag : nsLandingPagesHostsAndValidHostFlags) {
            nsHostsAndLandingPages.add(Pair.from(nsLandingPageHostAndValidHostFlag.getSecond(), nsLandingPageHostAndValidHostFlag.getFirst()));
        }
        return nsHostsAndLandingPages;
    }

    /**
     * Return the NS hosts and landing pages from the specified vendor destination URLs.
     */
    public List<Pair<String, String>> getNsHostsAndLandingPagesFromVendorDestinationUrl(String logTag, Connection pdbConnection, String prodInstId, String[] vendorDestinationUrls) throws SQLException {
        return getNsHostsAndLandingPagesFromVendorDestinationUrl(logTag, pdbConnection, prodInstId, toIterable(vendorDestinationUrls));
    }

    /**
     * Return the NS hosts plus landing pages from the specified vendor destination URL removing any valid hosts.
     */
    public String[] getNsHostsPlusLandingPagesFromVendorDestinationUrlsRemovingValidHosts(String logTag, Connection pdbConnection, String prodInstId, String[] vendorDestinationUrls) throws SQLException {
        List<Triple<String, String, Boolean>> nsLandingPagesHostsAndValidHostFlags = getNsLandingPagesHostsAndValidHostFlagsFromVendor(logTag, pdbConnection, prodInstId, toIterable(vendorDestinationUrls));
        if (nsLandingPagesHostsAndValidHostFlags == null) {
            return null;
        }

        String[] nsHostsPlusLandingPages = new String[nsLandingPagesHostsAndValidHostFlags.size()];
        for (int i = 0; i < nsLandingPagesHostsAndValidHostFlags.size(); i++) {
            Triple<String, String, Boolean> nsLandingPageHostAndValidHostFlag = nsLandingPagesHostsAndValidHostFlags.get(i);
            String landingPage = nsLandingPageHostAndValidHostFlag.getFirst();
            String host = nsLandingPageHostAndValidHostFlag.getSecond();
            if (Boolean.TRUE.equals(nsLandingPageHostAndValidHostFlag.getThird())) {
                nsHostsPlusLandingPages[i] = landingPage;
            }
            else {
                nsHostsPlusLandingPages[i] = stringIsEmpty(host) ? landingPage : host + landingPage;
            }
        }

        return nsHostsPlusLandingPages;
    }

    /**
     * Return the NS landing pages, host names and valid host flags for the specified vendor ad destination URL.
     */
    public List<Triple<String, String, Boolean>> getNsLandingPagesHostsAndValidHostFlagsFromVendor(String logTag, Connection pdbConnection, String prodInstId, String[] vendorDestinationUrls) throws SQLException {
        return getNsLandingPagesHostsAndValidHostFlagsFromVendor(logTag, pdbConnection, prodInstId, toIterable(vendorDestinationUrls));
    }

    /**
     * Return the NS landing pages, host names and valid host flags for the specified vendor ad destination URL.
     */
    public List<Triple<String, String, Boolean>> getNsLandingPagesHostsAndValidHostFlagsFromVendor(String logTag, Connection pdbConnection, String prodInstId, Iterable<String> vendorDestinationUrls) throws SQLException {
        Pair<String, String> productUrlAndTrackingType = getProductUrlAndTrackingType(logTag, pdbConnection, prodInstId);
        if ((productUrlAndTrackingType == null) || (productUrlAndTrackingType.getFirst() == null)) {
            logError(logTag, "No product URL");
            return null;
        }
        String productUrl = productUrlAndTrackingType.getFirst();
        String trackingType = productUrlAndTrackingType.getSecond();

        Collection<InterceptorMapping> interceptorMappings = null;
        Collection<String> validHostNames = null;
        if (LeadTrackingType.INTERCEPTOR_TRACKING.equals(trackingType)) {
            interceptorMappings = interceptorMappingHelper.getMappingsByProdInstId(logTag, pdbConnection, prodInstId);
        }
        else {
            // Get all valid product host names.
            String primaryHostName = extractHostName(productUrl);
            Collection<String> secondaryUrls = productSecondaryUrlHelper.getSecondaryUrls(logTag, pdbConnection, prodInstId);
            validHostNames = new ArrayList<String>(1 + secondaryUrls.size());
            if (stringIsNotEmpty(primaryHostName)) {
                validHostNames.add(primaryHostName);
            }
            for (String secondaryUrl : secondaryUrls) {
                String secondaryHostName = extractHostName(secondaryUrl);
                if (stringIsNotEmpty(secondaryHostName)) {
                    validHostNames.add(secondaryHostName);
                }
            }
        }

        List<Triple<String, String, Boolean>> nsLandingPagesHostsAndValidHostFlags = new ArrayList<Triple<String, String, Boolean>>();
        for (String vendorDestinationUrl : vendorDestinationUrls) {
            Triple<String, String, Boolean> nsLandingPageHostAndValidHostFlag = null;
            if (stringIsEmpty(vendorDestinationUrl)) {
                nsLandingPageHostAndValidHostFlag = Triple.from(null, null, Boolean.FALSE);
                nsLandingPagesHostsAndValidHostFlags.add(nsLandingPageHostAndValidHostFlag);
                continue;
            }

            // Strip all ad parameters.
            for (ParamStripper paramStripper : ParamTools.getParamStrippers()) {
                vendorDestinationUrl = paramStripper.stripParams(vendorDestinationUrl);
            }
            String landingPage = extractPathPlusParameters(vendorDestinationUrl);

            Pair<String, Integer> hostNameAndPort = extractHostNameAndPort(vendorDestinationUrl);
            String hostName = hostNameAndPort.getFirst();
            if (stringIsNotEmpty(hostName)) {
                Boolean validHost = Boolean.FALSE;
                Integer port = hostNameAndPort.getSecond();
                StringBuilder sb = new StringBuilder(extractProtocolPlusDelimiter(vendorDestinationUrl));
                if (LeadTrackingType.INTERCEPTOR_TRACKING.equals(trackingType)) {
                    if (collectionIsNotEmpty(interceptorMappings)) {
                        for (InterceptorMapping interceptorMapping : interceptorMappings) {
                            String alias = interceptorMapping.getAlias();
                            if (alias != null) {
                                alias = alias.toLowerCase();
                            }
                            if (hostName.equals(alias)) {
                                sb.append(interceptorMapping.getRealHost());
                                int realPort = interceptorMapping.getRealPort();
                                if (realPort > 0) {
                                    sb.append(':').append(realPort);
                                }
                                validHost = Boolean.TRUE;

                                break;
                            }
                        }
                    }

                    if (Boolean.FALSE.equals(validHost)) {
                        sb.append(hostName);
                        if (port != null) {
                            sb.append(':').append(port);
                        }
                    }
                }
                else {
                    sb.append(hostName);
                    if (port != null) {
                        sb.append(':').append(port);
                    }
                    validHost = Boolean.valueOf(validHostNames.contains(hostName));
                }

                nsLandingPageHostAndValidHostFlag = Triple.from(landingPage, sb.toString(), validHost);
            }
            else {
                nsLandingPageHostAndValidHostFlag = Triple.from(landingPage, null, Boolean.FALSE);
            }

            nsLandingPagesHostsAndValidHostFlags.add(nsLandingPageHostAndValidHostFlag);
        }

        return nsLandingPagesHostsAndValidHostFlags;
    }

    /**
     * Return a product's primary Interceptor alias.
     */
    public String getPrimaryAlias(String logTag, Connection connection, String prodInstId) throws SQLException {
        return interceptorMappingHelper.getPrimaryAlias(logTag, connection, prodInstId);
    }

    /**
     * Return a product's primary Interceptor mapping.
     */
    public InterceptorMapping getPrimaryInterceptorMapping(String logTag, Connection connection, String prodInstId) throws SQLException {
        return interceptorMappingHelper.getPrimaryInterceptorMapping(logTag, connection, prodInstId);
    }

    /**
     * Return a product's product ID, URL, status and tracking type.
     */
    public Quadruple<Long, String, String, String> getProductProdIdUrlStatusAndTrackingType(String logTag, Connection connection, String prodInstId) throws SQLException {
        return productHelper.getProdIdUrlStatusAndTrackingType(logTag, connection, prodInstId);
    }

    /**
     * Return a product's secondary URLs.
     */
    public List<String> getProductSecondaryUrls(String logTag, Connection connection, String prodInstId) throws SQLException {
        return productSecondaryUrlHelper.getSecondaryUrls(logTag, connection, prodInstId);
    }

    /**
     * Return a product's status.
     */
    public String getProductStatus(String logTag, Connection connection, String prodInstId) throws SQLException {
        Quadruple<Long, String, String, String> prodIdUrlStatusAndTrackingType =
            productHelper.getProdIdUrlStatusAndTrackingType(logTag, connection, prodInstId);
        return (prodIdUrlStatusAndTrackingType == null) ? null :
            prodIdUrlStatusAndTrackingType.getThird();
    }

    /**
     * Return a product's tracking type.
     */
    public String getProductTrackingType(String logTag, Connection connection, String prodInstId) throws SQLException {
        Quadruple<Long, String, String, String> prodIdUrlStatusAndTrackingType =
            productHelper.getProdIdUrlStatusAndTrackingType(logTag, connection, prodInstId);
        return (prodIdUrlStatusAndTrackingType == null) ? null :
            prodIdUrlStatusAndTrackingType.getFourth();
    }

    /**
     * Return a product's URL.
     */
    public String getProductUrl(String logTag, Connection connection, String prodInstId) throws SQLException {
        Quadruple<Long, String, String, String> prodIdUrlStatusAndTrackingType =
            productHelper.getProdIdUrlStatusAndTrackingType(logTag, connection, prodInstId);
        return (prodIdUrlStatusAndTrackingType == null) ? null :
            prodIdUrlStatusAndTrackingType.getSecond();
    }

    /**
     * Return a product's URL and tracking type.
     */
    public Pair<String, String> getProductUrlAndTrackingType(String logTag, Connection connection, String prodInstId) throws SQLException {
        Quadruple<Long, String, String, String> prodIdUrlStatusAndTrackingType =
            productHelper.getProdIdUrlStatusAndTrackingType(logTag, connection, prodInstId);
        return (prodIdUrlStatusAndTrackingType == null) ? null :
            Pair.from(prodIdUrlStatusAndTrackingType.getSecond(), prodIdUrlStatusAndTrackingType.getFourth());
    }

    /**
     * Return a product's URL, status and tracking type.
     */
    public Triple<String, String, String> getProductUrlStatusAndTrackingType(String logTag, Connection connection, String prodInstId) throws SQLException {
        Quadruple<Long, String, String, String> prodIdUrlStatusAndTrackingType =
            productHelper.getProdIdUrlStatusAndTrackingType(logTag, connection, prodInstId);
        return (prodIdUrlStatusAndTrackingType == null) ? null :
            Triple.from(prodIdUrlStatusAndTrackingType.getSecond(), prodIdUrlStatusAndTrackingType.getThird(), prodIdUrlStatusAndTrackingType.getFourth());
    }

    /**
     * Return all valid top-level domain names.
     */
    public List<String> getValidTopLevelDomainNames(String logTag, Connection connection) throws SQLException {
        List<String> topLevelDomainNames = new ArrayList<String>(topLevelDomainHelper.getTopLevelDomainNames(logTag, connection));
        Collections.sort(topLevelDomainNames);
        return Collections.unmodifiableList(topLevelDomainNames);
    }

    /**
     * Return the vendor destination URL for the specified NS destination URL.
     */
    public String getVendorDestinationUrlFromNs(String logTag, Connection pdbConnection, String prodInstId, String nsDestinationUrl, int vendorId, long nsAdGroupId) throws SQLException {
        List<String> vendorDestinationUrls = getVendorDestinationUrlsFromNs(logTag, pdbConnection, prodInstId, Collections.singleton(nsDestinationUrl), vendorId, nsAdGroupId);
        return collectionIsEmpty(vendorDestinationUrls) ? null : vendorDestinationUrls.get(0);
    }

    /**
     * Return the vendor destination URLs for the specified NS destination URLs.
     */
    public List<String> getVendorDestinationUrlsFromNs(String logTag, Connection pdbConnection, String prodInstId, Iterable<String> nsDestinationUrls, int vendorId, long nsAdGroupId) throws SQLException {
        return getVendorDestinationUrlsFromNs(logTag, pdbConnection, prodInstId, nsDestinationUrls, vendorId, ParamTools.getParamStripper(vendorId), ParamTools.getParamAdder(vendorId, nsAdGroupId));
    }

    /**
     * Return the vendor destination URLs for the specified NS destination URLs.
     */
    public String[] getVendorDestinationUrlsFromNs(String logTag, Connection pdbConnection, String prodInstId, String[] nsDestinationUrls, int vendorId, long nsAdGroupId) throws SQLException {
        List<String> vendorDestinationUrls = getVendorDestinationUrlsFromNs(logTag, pdbConnection, prodInstId, toIterable(nsDestinationUrls), vendorId, nsAdGroupId);
        if (vendorDestinationUrls == null) {
            return null;
        }
        return vendorDestinationUrls.toArray(new String[vendorDestinationUrls.size()]);
    }

    /**
     * Return the vendor sitelink destination URL for the specified NS destination URL.
     */
    public String getVendorSitelinkDestinationUrlFromNs(String logTag, Connection pdbConnection, String prodInstId, String nsDestinationUrl, int vendorId) throws SQLException {
        List<String> vendorDestinationUrls = getVendorSitelinkDestinationUrlsFromNs(logTag, pdbConnection, prodInstId, Collections.singleton(nsDestinationUrl), vendorId);
        return collectionIsEmpty(vendorDestinationUrls) ? null : vendorDestinationUrls.get(0);
    }

    /**
     * Return the vendor sitelink destination URLs for the specified NS destination URLs.
     */
    public List<String> getVendorSitelinkDestinationUrlsFromNs(String logTag, Connection pdbConnection, String prodInstId, Iterable<String> nsDestinationUrls, int vendorId) throws SQLException {
        return getVendorDestinationUrlsFromNs(logTag, pdbConnection, prodInstId, nsDestinationUrls, vendorId, ParamTools.getParamStripper(vendorId), ParamTools.getSitelinksParamAdder(vendorId));
    }

    /**
     * Return the vendor sitelink destination URLs for the specified NS destination URLs.
     */
    public String[] getVendorSitelinkDestinationUrlsFromNs(String logTag, Connection pdbConnection, String prodInstId, String[] nsDestinationUrls, int vendorId) throws SQLException {
        List<String> vendorDestinationUrls = getVendorSitelinkDestinationUrlsFromNs(logTag, pdbConnection, prodInstId, toIterable(nsDestinationUrls), vendorId);
        if (vendorDestinationUrls == null) {
            return null;
        }
        return vendorDestinationUrls.toArray(new String[vendorDestinationUrls.size()]);
    }

    /**
     * Insert (and return) an Interceptor mapping for the specified URL.
     */
    public InterceptorMapping insertInterceptorMapping(String logTag, Connection connection, String prodInstId, String url) throws SQLException {
        InterceptorMapping interceptorMapping = getInterceptorMapping(prodInstId, url);
        if ((interceptorMapping != null) && (interceptorMapping.getAlias() != null)) {
            interceptorMappingHelper.insertMapping(logTag, connection, interceptorMapping);
        }
        return interceptorMapping;
    }

    /**
     * Insert an Interceptor replacement for the specified Interceptor mapping.
     * The replacement will replace occurrences of the real host (and port) with the alias.
     */
    public void insertInterceptorReplacementForMapping(String logTag, Connection connection, InterceptorMapping interceptorMapping) throws SQLException {
        interceptorReplacementHelper.insertReplacement(logTag, connection, getInterceptorReplacementForMapping(interceptorMapping));
    }

    /**
     * Creates, persists and stores the Interceptor settings for a given product instance ID, using the product's URL.
     * Returns the new Interceptor mapping's alias.
     */
    public String insertInterceptorSettings(String logTag, Connection connection, String prodInstId) throws SQLException {
        Quadruple<Long, String, String, String> prodIdUrlStatusAndTrackingType =
            productHelper.getProdIdUrlStatusAndTrackingType(logTag, connection, prodInstId);
        if (prodIdUrlStatusAndTrackingType == null) {
            return null;
        }

        String alias = null;
        Long prodId = prodIdUrlStatusAndTrackingType.getFirst();
        // Products that use Interceptor.
        if (ProdId.isPpcProduct(prodId) || ProdId.isDniProduct(prodId) || ProdId.isPCTProduct(prodId)) {
            insertInterceptorMapping(logTag, connection, prodInstId, prodIdUrlStatusAndTrackingType.getSecond());
        }

        // Products that use Interceptor or JS Collector.
        if (ProdId.isPpcProduct(prodId) ||
        	ProdId.isPCTProduct(prodId) ||
            ProdId.isDniProduct(prodId) ||
            ProdId.isS4EProduct(prodId) ||
            ProdId.isWebstatsProduct(prodId)) {

            InterceptorFeatures interceptorFeatures = new InterceptorFeatures();
            interceptorFeatures.setProdInstId(prodInstId);
            if (ProdId.isPCTProduct(prodId)) {
                interceptorFeatures.setAllowEmptyReferrer(true);
            }
            else {
                interceptorFeatures.setAllowEmptyReferrer(false);
            }
            if (ProdId.isPpcProduct(prodId) || ProdId.isPCTProduct(prodId) || ProdId.isDniProduct(prodId)) {
                interceptorFeatures.setPerformReplacements(true);
            }
            // TR103895 Don't propagate ad params for DIY PPC products.
            if (ProdId.isDiyPpcProduct(prodId)) {
                interceptorFeatures.setPropagateAdParams(false);
            }
            else {
                interceptorFeatures.setPropagateAdParams(true);
            }
            interceptorFeatures.setRewriteReferrer(false);
            if (ProdId.isDifmPpcProduct(prodId) || ProdId.isPCTProduct(prodId)) {
                interceptorFeatures.setTrackEmail(true);
                interceptorFeatures.setTrackForm(true);
                interceptorFeatures.setTrackHighValuePage(true);
                interceptorFeatures.setTrackShoppingCart(true);
            }
            // Collect parameters only for serviced products.
            if (ProdId.isDifmPpcProduct(prodId) || ProdId.isPCTProduct(prodId) || ProdId.isS4EProduct(prodId)) {
                interceptorFeatures.setCollectEmailParams(true);
                interceptorFeatures.setCollectFormParams(true);
                interceptorFeatures.setCollectShoppingCartParams(true);
            }
            else {
                interceptorFeatures.setCollectEmailParams(false);
                interceptorFeatures.setCollectFormParams(false);
                interceptorFeatures.setCollectShoppingCartParams(false);
            }
            interceptorFeaturesHelper.insertFeatures(logTag, connection, interceptorFeatures);
        }

        return alias;
    }

    /**
     * Is call lead recording enabled for the specified product.
     */
    public boolean isCallLeadRecordingEnabled(String logTag, Connection connection, String prodInstId) throws SQLException {
        CallTrackingFeatures callTrackingFeatures = callTrackingFeaturesHelper.getCallTrackingFeatures(logTag, connection, prodInstId);
        if (callTrackingFeatures == null) {
            return false;
        }
        return callTrackingFeatures.isLeadRecordingEnabled();
    }

    /**
     * Set a product's call lead recording enabled flag.
     */
    public void setCallLeadRecordingEnabled(String logTag, Connection connection, String prodInstId, boolean callLeadRecordingEnabled, String updatedByUser, String updatedBySystem) throws SQLException {
        CallTrackingFeatures callTrackingFeatures = callTrackingFeaturesHelper.getCallTrackingFeatures(logTag, connection, prodInstId);
        if (callTrackingFeatures != null) {
            callTrackingFeatures.setLeadRecordingEnabled(callLeadRecordingEnabled);
            callTrackingFeatures.setUpdatedBySystem(updatedBySystem);
            callTrackingFeatures.setUpdatedByUser(updatedByUser);
            callTrackingFeaturesHelper.insertOrUpdateCallTrackingFeatures(logTag, connection, callTrackingFeatures);
        }
    }

    /**
     * Is call tracking enabled for the specified product.
     */
    public boolean isCallTrackingEnabled(String logTag, Connection connection, String prodInstId) throws SQLException {
        CallTrackingFeatures callTrackingFeatures = callTrackingFeaturesHelper.getCallTrackingFeatures(logTag, connection, prodInstId);
        if (callTrackingFeatures == null) {
            return false;
        }
        return callTrackingFeatures.isCallTrackingEnabled();
    }

    /**
     * Set a product's call tracking enabled flag.
     */
    public void setCallTrackingEnabled(String logTag, Connection connection, String prodInstId, boolean callTrackingEnabled, String updatedByUser, String updatedBySystem) throws SQLException {
        CallTrackingFeatures callTrackingFeatures = callTrackingFeaturesHelper.getCallTrackingFeatures(logTag, connection, prodInstId);
        if (callTrackingFeatures != null) {
            callTrackingFeatures.setCallTrackingEnabled(callTrackingEnabled);
            callTrackingFeatures.setUpdatedBySystem(updatedBySystem);
            callTrackingFeatures.setUpdatedByUser(updatedByUser);
            callTrackingFeaturesHelper.insertOrUpdateCallTrackingFeatures(logTag, connection, callTrackingFeatures);
        }
    }

    /**
     * Set a product's secondary URLs.
     */
    public void setProductSecondaryUrls(String logTag, Connection gdbConnection, Connection pdbConnection, String prodInstId, Iterable<String> urls) throws SQLException {
        // Remove any secondary URLs that are equivalent to the product primary URL.
        String productUrl = getProductUrl(logTag, pdbConnection, prodInstId);
        final String productHostName = extractHostName(productUrl);
        urls = filter(urls, new Predicate<String>() {
            @Override
            public boolean apply(String url) {
                return !stringsEqualIgnoreCase(extractHostName(url), productHostName);
            }});

        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, pdbConnection, prodInstId);
        productSecondaryUrlHelper.insertSecondaryUrls(logTag, pdbConnection, prodInstId, urls);
        // Secondary URLs must have unique aliases, so append the product instance ID (lowercase).
        String hostNameSuffix = prodInstId.toLowerCase();
        // Keep any existing Interceptor mappings and associated replacements.
        for (String url : urls) {
            InterceptorMapping interceptorMapping = getInterceptorMapping(prodInstId, url, hostNameSuffix, INTERCEPTOR_DOMAIN);
            if (interceptorMapping == null) {
                logError(logTag, "Unable to create Interceptor mapping: " + url);
                continue;
            }
            interceptorMappingHelper.insertMapping(logTag, gdbConnection, interceptorMapping);
            insertInterceptorReplacementForMapping(logTag, gdbConnection, interceptorMapping);
        }
    }

    /**
     * Set a product's secondary URLs.
     */
    public void setProductSecondaryUrls(String logTag, Connection gdbConnection, Connection pdbConnection, String prodInstId, String[] urls) throws SQLException {
        setProductSecondaryUrls(logTag, gdbConnection, pdbConnection, prodInstId, toIterable(urls));
    }

    /**
     * Update a product's status.
     */
    public void updateStatus(String logTag, Connection connection, String prodInstId, String status, String updatedByUser, String updatedBySystem) throws SQLException {
        productHelper.updateStatus(logTag, connection, prodInstId, status, updatedByUser, updatedBySystem);
    }

    /**
     * Update a product's tracking type.
     */
    public void updateTrackingType(String logTag, Connection connection, String prodInstId, String trackingType, String updatedByUser, String updatedBySystem) throws SQLException {
        productHelper.updateTrackingType(logTag, connection, prodInstId, trackingType, updatedByUser, updatedBySystem);
    }

    /**
     * Update a product's URL.
     */
    public void updateUrl(String logTag, Connection connection, String prodInstId, String url, String updatedByUser, String updatedBySystem) throws SQLException {
        productHelper.updateUrl(logTag, connection, prodInstId, url, updatedByUser, updatedBySystem);
    }

    /**
     * Append the specified port if it's not a default port.
     */
    public static void appendPort(Appendable appendable, int port) {
        if (!isDefaultPort(port)) {
            try {
                appendable.append(':');
                appendable.append(Integer.toString(port));
            }
            catch (IOException ex) {}
        }
    }

    /**
     * Extract the host name from a site URL.
     */
    public static String extractHostName(String url) {
        return extractHostNameAndPort(url).getFirst();
    }

    /**
     * Extract the host name and port number from a site URL.
     */
    public static Pair<String, Integer> extractHostNameAndPort(String url) {
        if (url == null) {
            return Pair.from(null, null);
        }

        URL u = null;
        try {
            u = new URL(url);
        }
        catch (MalformedURLException ex1) {
            try {
                u = new URL(HTTP_SCHEME_PLUS_DELIMITER + url);
            }
            catch (MalformedURLException ex2) {
                return Pair.from(null, null);
            }
        }

        int p = u.getPort();

        return Pair.from(u.getHost().toLowerCase(), (p > 0) ? Integer.valueOf(p) : null);
    }

    /**
     * Extract the host name plus port number from a site URL.
     */
    public static String extractHostNamePlusPort(String url) {
        Pair<String, Integer> hostNameAndPort = extractHostNameAndPort(url);
        String hostName = hostNameAndPort.getFirst();
        if (hostName == null) {
            return null;
        }
        Integer port = hostNameAndPort.getSecond();
        if (port == null) {
            return hostName;
        }
        return new StringBuilder(hostName).append(':').append(port.intValue()).toString();
    }

    /**
     * Extract the path and parameters from a URL.
     */
    public static Pair<String, String> extractPathAndParameters(String url) {
        String path = null;
        String parameters = null;

        if (url != null) {
            int fromIndex = url.indexOf(SCHEME_DELIMITER);
            if (fromIndex < 0) {
                fromIndex = 0;
            }
            else {
                fromIndex += SCHEME_DELIMITER_LENGTH;
            }
            int pathStart = url.indexOf('/', fromIndex);
            if (pathStart >= 0) {
                int pathEnd = url.indexOf('?', pathStart);
                if (pathEnd < 0) {
                    path = url.substring(pathStart);
                }
                else {
                    path = url.substring(pathStart, pathEnd);
                    parameters = url.substring(pathEnd + 1, url.length());
                }
            }
            else {
                path = "/";
                int pathEnd = url.indexOf('?', fromIndex);
                if (pathEnd >= 0) {
                    parameters = url.substring(pathEnd + 1, url.length());
                }
            }
        }
        if (stringIsEmpty(parameters)) {
            parameters = null;
        }

        return Pair.from(path, parameters);
    }

    /**
     * Extract the path and parameters from a URL.
     */
    public static String extractPathPlusParameters(String url) {
        Pair<String, String> pathAndParameters = extractPathAndParameters(url);
        String path = pathAndParameters.getFirst();
        if (path == null) {
            return null;
        }
        String parameters = pathAndParameters.getSecond();
        if (parameters == null) {
            return path;
        }
        return new StringBuilder(path).append('?').append(parameters).toString();
    }

    /**
     * Extract the primary and top-level domain names from a URL.
     */
    public static Pair<String, String> extractPrimaryAndTopLevelDomainNames(List<String> validTopLevelDomainNames, String url) {
        Triple<String, String, Integer> primaryAndTopLevelDomainNamesAndDomainLevel =
                extractPrimaryAndTopLevelDomainNamesAndDomainLevel(validTopLevelDomainNames, url);
        return Pair.from(primaryAndTopLevelDomainNamesAndDomainLevel.getFirst(), primaryAndTopLevelDomainNamesAndDomainLevel.getSecond());
    }

    /**
     * Returns the protocol of the specified URL.
     */
    public static String extractProtocol(String url) {
        String protocolPlusDelimiter = extractProtocolPlusDelimiter(url);
        return protocolPlusDelimiter.substring(0, protocolPlusDelimiter.length() - SCHEME_DELIMITER_LENGTH);
    }

    /**
     * Returns the protocol plus delimiter of the specified URL.
     */
    public static String extractProtocolPlusDelimiter(String url) {
        if (url != null) {
            int index = url.indexOf(SCHEME_DELIMITER);
            if (index >= 0) {
                return url.substring(0, index + SCHEME_DELIMITER_LENGTH);
            }
        }
        return HTTP_SCHEME_PLUS_DELIMITER;
    }

    /**
     * Prefixes the given URL with the HTTP protocol if it does not already have a protocol of HTTP or HTTPS.
     */
    public static String fixUrl(String url) {
        if (url == null) {
            return null;
        }

        int index = url.indexOf(SCHEME_DELIMITER);
        if (index < 0) {
            return HTTP_SCHEME_PLUS_DELIMITER + url;
        }

        String scheme = url.substring(0, index).toLowerCase();
        if (stringsEqual(scheme, HTTP_SCHEME) || stringsEqual(scheme, HTTPS_SCHEME)) {
            return url;
        }

        return HTTP_SCHEME_PLUS_DELIMITER + url.substring(index + SCHEME_DELIMITER_LENGTH);
    }

    /**
     * Returns the alternate host name for the specified URL.
     * The alternate host name is the specified host name either prefixed with 'www.' or with 'www.' removed,
     * but only if the domain name is at the correct level.
     */
    public static String getAlternateHostName(List<String> validTopLevelDomainNames, String hostName) {
        Triple<String, String, Integer> primaryAndTopLevelDomainNamesAndDomainLevel =
                extractPrimaryAndTopLevelDomainNamesAndDomainLevel(validTopLevelDomainNames, hostName);
        Integer domainLevel = primaryAndTopLevelDomainNamesAndDomainLevel.getThird();
        if (domainLevel == null) {
            return null;
        }
        switch (domainLevel.intValue()) {
        case 2:
            return "www." + hostName;
        case 3:
            if (hostName.startsWith("www.")) {
                return hostName.substring(4);
            }
            break;
        default:
            break;
        }

        return null;
    }

    /**
     * Return the replacement for the specified host name and port.
     */
    public static String getHostNamePlusPortReplacement(CharSequence hostName, int port) {
        StringBuilder sb = new StringBuilder("$1").append(hostName);
        appendPort(sb, port);
        return sb.toString();
    }

    /**
     * Return the replacement regular expression for the specified host name and port.
     */
    public static String getHostNamePlusPortReplacementRegex(CharSequence hostName, int port) {
        StringBuilder sb = new StringBuilder("(?i)(http[s]?://)");
        for (int i = 0; i < hostName.length(); i++) {
            char ch = hostName.charAt(i);
            // Escape '.'s so that they are matched exactly.
            if (ch == '.') {
                sb.append('\\');
            }
            sb.append(ch);
        }
        appendPort(sb, port);
        return sb.toString();
    }

    /**
     * Returns the Interceptor alias for the specified host name.
     */
    public static String getInterceptorAlias(String hostName) {
        return getInterceptorAlias(hostName, null, INTERCEPTOR_DOMAIN);
    }

    /**
     * Returns the Interceptor alias for the specified host name and Interceptor domain.
     */
    public static String getInterceptorAlias(String hostName, String hostNameSuffix, String interceptorDomain) {
        if (hostName == null) {
            return null;
        }
        if (stringIsNotEmpty(hostNameSuffix)) {
            hostName = hostName + "." + hostNameSuffix;
        }
        return new StringBuilder(hostName.replace('.', '-')).append('.').append(interceptorDomain).toString();
    }

    /**
     * Returns a new Interceptor mapping with the default Interceptor domain.
     */
    public static InterceptorMapping getInterceptorMapping(String prodInstId, String url) {
        return getInterceptorMapping(prodInstId, url, null, INTERCEPTOR_DOMAIN);
    }

    /**
     * Returns a new Interceptor mapping with the specified Interceptor domain.
     */
    public static InterceptorMapping getInterceptorMapping(String prodInstId, String url, String hostNameSuffix, String interceptorDomain) {
        if (stringIsBlank(url)) {
            return null;
        }

        Pair<String, Integer> hostNameAndPort = extractHostNameAndPort(url);
        String hostName = hostNameAndPort.getFirst();
        if (hostName == null) {
            return null;
        }
        Integer port = hostNameAndPort.getSecond();
        InterceptorMapping mapping = new InterceptorMapping();
        mapping.setAlias(getInterceptorAlias(hostName, hostNameSuffix, interceptorDomain));
        mapping.setProdInstId(prodInstId);
        mapping.setRealHost(hostName);
        if (port != null) {
            mapping.setRealPort(port.intValue());
        }

        return mapping;
    }

    /**
     * Return Interceptor replacement for the specified Interceptor mapping.
     */
    public static InterceptorReplacement getInterceptorReplacementForMapping(InterceptorMapping interceptorMapping) {
        InterceptorReplacement interceptorReplacement = new InterceptorReplacement();
        interceptorReplacement.setLimitType(LimitType.NONE);
        interceptorReplacement.setOriginalRegex(getHostNamePlusPortReplacementRegex(interceptorMapping.getRealHost(), interceptorMapping.getRealPort()));
        interceptorReplacement.setOriginalText("n/a");
        interceptorReplacement.setProdInstId(interceptorMapping.getProdInstId());
        interceptorReplacement.setReplacementText(getHostNamePlusPortReplacement(interceptorMapping.getAlias(), 0));
        interceptorReplacement.setReplacementType(ReplacementType.URL);
        interceptorReplacement.setVendorEntityId(Long.valueOf(0L));
        return interceptorReplacement;
    }

    /**
     * Return the original number regular expression:
     * 7036684751 -> 1?[\s*-\.]?\(?[703]{0,3}\)?[\s\./-]*668[\s\./-]*4751
     */
    public static String getOriginalNumberRegex(String originalNumber) {
        if ((originalNumber == null) || (originalNumber.length() < 10)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("1?[\\s*-\\.]?\\(?[");
        sb.append(originalNumber.substring(0, 3));
        sb.append("]{0,3}\\)?").append(PHONE_NUMBER_SEPARATOR_REGEX).append('*'); // Zero or more times.
        sb.append(originalNumber.substring(3, 6));
        sb.append(PHONE_NUMBER_SEPARATOR_REGEX).append('*'); // Zero or more times.
        sb.append(originalNumber.substring(6, 10));
        return sb.toString();
    }

    /**
     * Return the original number regular expression:
     * 7036684751 -> (1[\s*-\.])?(\()?703(\))?([\s\./-]*)668([\s\./-]*)4751
     */
    public static String getOriginalNumberRegexNew(String originalNumber) {
        if ((originalNumber == null) || (originalNumber.length() < 10)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("(1[\\s*-\\.])?(\\()?");
        sb.append(originalNumber.substring(0, 3));
        sb.append("(\\))?");
        sb.append('(').append(PHONE_NUMBER_SEPARATOR_REGEX).append('*').append(')'); // Zero or more times.
        sb.append(originalNumber.substring(3, 6));
        sb.append('(').append(PHONE_NUMBER_SEPARATOR_REGEX).append('*').append(')'); // Zero or more times.
        sb.append(originalNumber.substring(6, 10));
        return sb.toString();
    }

    /**
     * Return the standard phone number replacement:
     * 8005551234 -> (800) 555-1234
     */
    public static String getStandardPhoneNumberReplacement(String string) {
        if (string == null) {
            return null;
        }
        return string.replaceAll("^(\\d\\d\\d)(\\d\\d\\d)(\\d\\d\\d\\d)$","($1) $2-$3");
    }

    /**
     * Return the standard phone number replacement:
     * 8005551234 -> $1$2800$3$4555$51234
     */
    public static String getStandardPhoneNumberReplacementNew(String string) {
        if (string == null) {
            return null;
        }
        return string.replaceAll("^(\\d\\d\\d)(\\d\\d\\d)(\\d\\d\\d\\d)$","\\$1\\$2$1\\$3\\$4$2\\$5$3");
    }

    /**
     * Return the vanity number regular expression:
     * 1-800-EAT-SHIT -> 1[\s\./-]?800[\s\./-]?EAT[\s\./-]?SHIT
     */
    public static String getVanityNumberRegex(String vanityNumber) {
        if ((vanityNumber == null) || (vanityNumber.length() < 10)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vanityNumber.length(); i++) {
            char c = vanityNumber.charAt(i);
            switch (c) {
            case '(':
            case ')':
            case '*':
            case '+':
            case '?':
                sb.append('\\').append(c);
                break;
            case '.':
            case ' ':
            case '-':
                sb.append(PHONE_NUMBER_SEPARATOR_REGEX).append('?'); // Once or not at all.
                break;
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Is the specified port number a default port number?
     */
    public static boolean isDefaultPort(int port) {
        return (port == 0) || (port == 80) || (port == 443);
    }

    /**
     * Is the specified port number a default port number?
     */
    public static boolean isDefaultPort(Integer port) {
        return (port == null) || isDefaultPort(port.intValue());
    }

    /**
     * Returns whether or not the protocol of the specified URL is HTTP or HTTPS.
     */
    public static boolean isHyperTextProtocol(String url) {
        if (url == null) {
            return false;
        }

        int index = url.indexOf(SCHEME_DELIMITER);
        if (index <= 0) {
            return false;
        }

        String scheme = url.substring(0, index).toLowerCase();
        return stringsEqual(scheme, HTTP_SCHEME) || stringsEqual(scheme, HTTPS_SCHEME);
    }

    public static String lowercaseHostAndProtocol(String url) {
        URL u = null;
        String result = null;

        try {
            u = new URL(url);
        }
        catch (MalformedURLException ex1) {
            try {
                u = new URL(HTTP_SCHEME_PLUS_DELIMITER + url);
            }
            catch (MalformedURLException ex2) {}
        }

        if (u != null) {
            int pathIndex = url.indexOf(u.getHost()) + u.getHost().length();

            String protocol = u.getProtocol().toLowerCase() + SCHEME_DELIMITER;
            String host = u.getHost().toLowerCase();

            String portPathAndQuery = url.substring(pathIndex);
            result = protocol + host + portPathAndQuery;
        }

        return result;
    }

    /**
     * Modify the host name and port of the specified URL.
     */
    public static String modifyHostNameAndPort(String url, String newHostName, int newPort) {
        if (url == null) {
            return null;
        }
        if (newHostName == null) {
            return url;
        }

        Integer port = null;
        Pair<String, String> pathAndParameters = extractPathAndParameters(url);
        String path = pathAndParameters.getFirst();
        String parameters = pathAndParameters.getSecond();

        StringBuilder sb = new StringBuilder(extractProtocolPlusDelimiter(url));
        sb.append(newHostName);
        if (newPort > 0) {
            sb.append(':');
            sb.append(newPort);
        }
        else if ((port = extractHostNameAndPort(url).getSecond()) != null) {
            sb.append(':');
            sb.append(port);
        }
        if (path != null) {
            sb.append(path);
        }
        if (parameters != null) {
            sb.append('?');
            sb.append(parameters);
        }
        return sb.toString();
    }

    public static String updateDestinationUrlHostNameAndPort(String destinationUrl, int vendorId, long nsAdGroupId, String newHostName, int newPort) {
        ParamStripper paramStripper = ParamTools.getParamStripper(vendorId);
        ParamAdder paramAdder = ParamTools.getParamAdder(vendorId, nsAdGroupId);
        return updateDestinationUrlHostNameAndPort(destinationUrl, newHostName, newPort, paramStripper, paramAdder);
    }

    public static String updateSitelinkDestinationUrlHostNameAndPort(String destinationUrl, int vendorId, String newHostName, int newPort) {
        ParamStripper paramStripper = ParamTools.getParamStripper(vendorId);
        ParamAdder paramAdder = ParamTools.getSitelinksParamAdder(vendorId);
        return updateDestinationUrlHostNameAndPort(destinationUrl, newHostName, newPort, paramStripper, paramAdder);
    }

    private static Triple<String, String, Integer> extractPrimaryAndTopLevelDomainNamesAndDomainLevel(List<String> validTopLevelDomainNames, String url) {
        String hostName = extractHostName(url);
        if (stringIsNotEmpty(hostName)) {
            int level = 1;
            String value = hostName;
            String previousValue = null;
            while (true) {
                if (Collections.binarySearch(validTopLevelDomainNames, value) >= 0) {
                    return Triple.from(previousValue, value, Integer.valueOf(level));
                }
                int index = value.indexOf('.');
                if (index == -1) {
                    break;
                }
                previousValue = value;
                value = previousValue.substring(index + 1);
                level++;
            }
        }

        return Triple.from(null, null, null);
    }

    /**
     * Return the vendor destination URLs for the specified NS ad destination URLs using the specified parameter stripper and adder.
     */
    private List<String> getVendorDestinationUrlsFromNs(String logTag, Connection pdbConnection, String prodInstId, Iterable<String> nsDestinationUrls, int vendorId, ParamStripper paramStripper, ParamAdder paramAdder) throws SQLException {
        if ((paramStripper == null) || (paramAdder == null)) {
            logError(logTag, "Invalid vendor ID: " + Integer.toString(vendorId));
            return null;
        }

        Pair<String, String> productUrlAndTrackingType = getProductUrlAndTrackingType(logTag, pdbConnection, prodInstId);
        if ((productUrlAndTrackingType == null) || (productUrlAndTrackingType.getFirst() == null)) {
            logError(logTag, "No product URL");
            return null;
        }
        String productUrl = productUrlAndTrackingType.getFirst();
        String trackingType = productUrlAndTrackingType.getSecond();
        String defaultVendorDestinationUrlHostNamePlusPort =
            LeadTrackingType.INTERCEPTOR_TRACKING.equals(trackingType) ? getPrimaryAlias(logTag, pdbConnection, prodInstId) : extractHostNamePlusPort(productUrl);
        if (stringIsEmpty(defaultVendorDestinationUrlHostNamePlusPort)) {
            logError(logTag, "Unable to determine default vendor destination URL host name");
            return null;
        }

        List<String> vendorDestinationUrls = new ArrayList<String>();
        for (String nsDestinationUrl : nsDestinationUrls) {
            if (stringIsEmpty(nsDestinationUrl)) {
                vendorDestinationUrls.add(null);
                continue;
            }
            // Remove any existing ad parameters and add new ones.
            String landingPage = extractPathPlusParameters(paramAdder.addParams(paramStripper.stripParams(nsDestinationUrl)));
            Pair<String, Integer> hostNameAndPort = extractHostNameAndPort(nsDestinationUrl);
            String hostName = hostNameAndPort.getFirst();

            StringBuilder sb = new StringBuilder(extractProtocolPlusDelimiter(nsDestinationUrl));
            if (stringIsEmpty(hostName)) {
                sb.append(defaultVendorDestinationUrlHostNamePlusPort);
            }
            else {
                Integer port = hostNameAndPort.getSecond();
                if (LeadTrackingType.INTERCEPTOR_TRACKING.equals(trackingType)) {
                    InterceptorMapping interceptorMapping =
                        interceptorMappingHelper.getMappingByProdInstIdAndRealHost(logTag, pdbConnection, prodInstId, hostName);
                    if (interceptorMapping == null) {
                        sb.append(hostName);
                        if (port != null) {
                            sb.append(':').append(port);
                        }
                    }
                    else {
                        String alias = interceptorMapping.getAlias();
                        if (alias != null) {
                            alias = alias.toLowerCase();
                        }
                        sb.append(alias);
                    }
                }
                else {
                    sb.append(hostName);
                    if (port != null) {
                        sb.append(':').append(port);
                    }
                }
            }
            sb.append(landingPage);

            vendorDestinationUrls.add(sb.toString());
        }

        return vendorDestinationUrls;
    }

    private static String updateDestinationUrlHostNameAndPort(String destinationUrl, String newHostName, int newPort, ParamStripper paramStripper, ParamAdder paramAdder) {
        if ((paramStripper == null) || (paramAdder == null)) {
            return null;
        }

        destinationUrl = paramAdder.addParams(paramStripper.stripParams(destinationUrl));

        return modifyHostNameAndPort(destinationUrl, newHostName, newPort);
    }

    /**
     * DB helper.
     */
    private static class ProductHelper extends BaseHelper {
        /**
         * Constructor.
         */
        public ProductHelper(BaseLoggable baseLoggable) {
            super(baseLoggable);
        }

        /**
         * Constructor.
         */
        public ProductHelper(BaseLoggable baseLoggable, boolean logSqlStatements) {
            super(baseLoggable, logSqlStatements);
        }

        /**
         * Return the product's product ID, URL, status and tracking type.
         */
        public Quadruple<Long, String, String, String> getProdIdUrlStatusAndTrackingType(String logTag, Connection connection, String prodInstId) throws SQLException {
            final String SQL =
                "SELECT" +
                "  prod_id," +
                "  url," +
                "  status," +
                "  tracking_type " +
                "FROM" +
                "  product " +
                "WHERE" +
                "  prod_inst_id = ?;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setString(1, prodInstId);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, ProdIdUrlStatusAndTrackingTypeFactory.INSTANCE);
            }
            finally {
                close(statement, resultSet);
            }
        }

        /**
         * Update the product's status.
         */
        public void updateStatus(String logTag, Connection connection, String prodInstId, String status, String updatedByUser, String updatedBySystem) throws SQLException {
            updateOneValue(logTag, connection, "status", prodInstId, status, updatedByUser, updatedBySystem);
        }

        /**
         * Update the product's tracking type.
         */
        public void updateTrackingType(String logTag, Connection connection, String prodInstId, String trackingType, String updatedByUser, String updatedBySystem) throws SQLException {
            updateOneValue(logTag, connection, "tracking_type", prodInstId, trackingType, updatedByUser, updatedBySystem);
        }

        /**
         * Update the product's URL.
         */
        public void updateUrl(String logTag, Connection connection, String prodInstId, String url, String updatedByUser, String updatedBySystem) throws SQLException {
            updateOneValue(logTag, connection, "url", prodInstId, url, updatedByUser, updatedBySystem);
        }

        /**
         * Update the value of one column.
         */
        private void updateOneValue(String logTag, Connection connection, String columnName, String prodInstId, Object value, String updatedByUser, String updatedBySystem) throws SQLException {
            final String SQL =
                "UPDATE" +
                "  product " +
                "SET" +
                "  %1$s = ?," +
                "  updated_date = NOW()," +
                "  updated_by_user = ?," +
                "  updated_by_system = ? " +
                "WHERE" +
                "  prod_inst_id = ?;";

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(String.format(SQL, columnName));
                statement.setObject(1, value);
                statement.setString(2, updatedByUser);
                statement.setString(3, updatedBySystem);
                statement.setString(4, prodInstId);
                logSqlStatement(logTag, statement);
                statement.executeUpdate();
            }
            finally {
                close(statement);
            }
        }

        /**
         * Factory class used to create product ID, URL, status and tracking
         * type quadruples from a result set.
         */
        private static class ProdIdUrlStatusAndTrackingTypeFactory implements Factory<Quadruple<Long, String, String, String>> {
            public static final ProdIdUrlStatusAndTrackingTypeFactory INSTANCE = new ProdIdUrlStatusAndTrackingTypeFactory();

            /**
             * Constructor.
             */
            private ProdIdUrlStatusAndTrackingTypeFactory() {}

            /**
             * Return a new instance with values from the result set.
             */
            public Quadruple<Long, String, String, String> newInstance(ResultSet resultSet) throws SQLException {
                return Quadruple.from(
                        getLongValue(resultSet, "prod_id"),
                        resultSet.getString("url"),
                        resultSet.getString("status"),
                        resultSet.getString("tracking_type"));
            }
        }
    }
}
