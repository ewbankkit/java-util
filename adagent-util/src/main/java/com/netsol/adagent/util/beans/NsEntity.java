/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.netsol.adagent.util.Comparators;

/**
 * Represents an NS entity.
 */
public abstract class NsEntity extends BaseDataWithUpdateTracking {
    protected static final int TWO_DECIMAL_PLACES = 2;

    /**
     * Compare two NS entities based on the vendor entity ID.
     */
    private static final Comparator<NsEntity> VENDOR_ENTITY_ID_COMPARATOR = new Comparator<NsEntity>() {
        private final Comparator<Long> comparator = Comparators.newNullSmallestComparator();

        public int compare(NsEntity nsEntity1, NsEntity nsEntity2) {
            return comparator.compare(nsEntity1.getVendorEntityId(), nsEntity2.getVendorEntityId());
        }};

    @ColumnName("name")
    private String name = "";
    private long nsEntityId;
    @ColumnName("status")
    private String nsStatus;
    private String prodInstId;
    @ColumnName("updated_by_system")
    private String updatedBySystem;
    @ColumnName("updated_by_user")
    private String updatedByUser;
    @ColumnName("vendor_entity_id")
    private Long vendorEntityId;
    private int vendorId;
    private boolean vendorSync;

    public void setName(String name) {
        setTrackedField("name", name);
    }

    public String getName() {
        return name;
    }

    public void setNsStatus(String nsStatus) {
        setTrackedField("nsStatus", nsStatus);
    }

    public String getNsStatus() {
        return nsStatus;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setUpdatedBySystem(String updatedBySystem) {
        setTrackedField("updatedBySystem", updatedBySystem);
    }

    public String getUpdatedBySystem() {
        return updatedBySystem;
    }

    public void setUpdatedByUser(String updatedByUser) {
        setTrackedField("updatedByUser", updatedByUser);
    }

    public String getUpdatedByUser() {
        return updatedByUser;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getVendorId() {
        return vendorId;
    }

    protected void setNsEntityId(long nsEntityId) {
        this.nsEntityId = nsEntityId;
    }

    protected long getNsEntityId() {
        return nsEntityId;
    }

    protected void setVendorEntityId(Long vendorEntityId) {
        setTrackedField("vendorEntityId", vendorEntityId);
    }

    protected Long getVendorEntityId() {
        return vendorEntityId;
    }

    public void setVendorSync(boolean vendorSync) {
        this.vendorSync = vendorSync;
    }

    public boolean isVendorSync() {
        return vendorSync;
    }

    @Override
    public int hashCode() {
        int result = 17;
        int c = (prodInstId == null) ? 0 : prodInstId.hashCode();
        result = 31 * result + c;
        c = (int)(nsEntityId ^ (nsEntityId >>> 32));
        result = 31 * result + c;
        return result;
    }

    public static <T extends NsEntity> Map<Long, T> newNsEntityIdKeyedMap(Iterable<T> ts) {
        Map<Long, T> nsEntityIdKeyedMap = new HashMap<Long, T>();
        for (T t : ts) {
            T previousValue = nsEntityIdKeyedMap.put(Long.valueOf(t.nsEntityId), t);
            if (previousValue != null) {
                throw new IllegalArgumentException();
            }
        }
        return nsEntityIdKeyedMap;
    }

    public static <T extends NsEntity> Map<Pair<String, Long>, T> newProdInstIdAndNsEntityIdKeyedMap(Iterable<T> ts) {
        Map<Pair<String, Long>, T> prodInstIdAndNsEntityIdKeyedMap = new HashMap<Pair<String, Long>, T>();
        for (T t : ts) {
            T previousValue = prodInstIdAndNsEntityIdKeyedMap.put(Pair.from(t.prodInstId, Long.valueOf(t.nsEntityId)), t);
            if (previousValue != null) {
                throw new IllegalArgumentException();
            }
        }
        return prodInstIdAndNsEntityIdKeyedMap;
    }

    public static <T extends NsEntity> Map<Long, T> newVendorEntityIdKeyedMap(Iterable<T> ts) {
        Map<Long, T> vendorEntityIdKeyedMap = new HashMap<Long, T>();
        for (T t : ts) {
            T previousValue = vendorEntityIdKeyedMap.put(t.vendorEntityId, t);
            if (previousValue != null) {
                throw new IllegalArgumentException();
            }
        }
        return vendorEntityIdKeyedMap;
    }

    public static <T extends NsEntity> Map<Pair<Integer, Long>, T> newVendorIdAndVendorEntityIdKeyedMap(Iterable<T> ts) {
        Map<Pair<Integer, Long>, T> vendorIdAndVendorEntityIdKeyedMap = new HashMap<Pair<Integer, Long>, T>();
        for (T t : ts) {
            T previousValue = vendorIdAndVendorEntityIdKeyedMap.put(Pair.from(Integer.valueOf(t.vendorId), t.vendorEntityId), t);
            if (previousValue != null) {
                throw new IllegalArgumentException();
            }
        }
        return vendorIdAndVendorEntityIdKeyedMap;
    }

    public static <T extends NsEntity> Map<Triple<String, Integer, Long>, T> newProdInstIdVendorIdAndVendorEntityIdKeyedMap(Iterable<T> ts) {
        Map<Triple<String, Integer, Long>, T> prodInstIdVendorIdAndVendorEntityIdKeyedMap = new HashMap<Triple<String, Integer, Long>, T>();
        for (T t : ts) {
            T previousValue = prodInstIdVendorIdAndVendorEntityIdKeyedMap.put(Triple.from(t.prodInstId, Integer.valueOf(t.vendorId), t.vendorEntityId), t);
            if (previousValue != null) {
                throw new IllegalArgumentException();
            }
        }
        return prodInstIdVendorIdAndVendorEntityIdKeyedMap;
    }

    /**
     * Partition for vendor sync operations.
     * Return three lists:
     * 1) Entities in both lists updated from the vendor
     * 2) Entities only in the vendor list
     * 3) Entities only in the DB list
     */
    public static <T extends NsEntity> Triple<List<T>, List<T>, List<T>> partitionForVendorSync(List<T> nsEntitiesFromDb, List<T> nsEntitiesFromVendor, Copier<T> copier) {
        List<T> nsEntitiesInBoth = new ArrayList<T>();
        List<T> nsEntitiesOnlyInVendor = new ArrayList<T>();

        if (collectionIsEmpty(nsEntitiesFromVendor)) {
            nsEntitiesFromVendor = Collections.emptyList();
        }

        List<T> nsEntitiesFromDbSorted = null;
        if (collectionIsEmpty(nsEntitiesFromDb)) {
            nsEntitiesFromDbSorted = Collections.emptyList();
        }
        else {
            nsEntitiesFromDbSorted = new ArrayList<T>(nsEntitiesFromDb);
            Collections.sort(nsEntitiesFromDbSorted, VENDOR_ENTITY_ID_COMPARATOR);
        }
        for (T nsEntityFromVendor : nsEntitiesFromVendor) {
            int index = Collections.binarySearch(nsEntitiesFromDbSorted, nsEntityFromVendor, VENDOR_ENTITY_ID_COMPARATOR);
            if (index >= 0) {
                T nsEntityFromDb = nsEntitiesFromDbSorted.get(index);
                // Update from vendor.
                copier.copy(nsEntityFromVendor, nsEntityFromDb);
                nsEntityFromDb.setVendorSync(true);
                nsEntitiesInBoth.add(nsEntityFromDb);
                nsEntitiesFromDbSorted.remove(index);
            }
            else {
                nsEntityFromVendor.setVendorSync(true);
                nsEntityFromVendor.clearTrackedUpdates();
                nsEntitiesOnlyInVendor.add(nsEntityFromVendor);
            }
        }
        for (T nsEntityFromDbSorted : nsEntitiesFromDbSorted) {
            nsEntityFromDbSorted.setVendorSync(true);
        }

        return Triple.from(nsEntitiesInBoth, nsEntitiesOnlyInVendor, nsEntitiesFromDbSorted);
    }

    public static <T extends NsEntity> void setNsEntityIds(Iterable<T> ts, Iterable<Long> ids) {
        Iterator<T> tIterator = ts.iterator();
        Iterator<Long> idIterator = ids.iterator();

        while (tIterator.hasNext()) {
            T t = tIterator.next();
            Long id = idIterator.next();
            if (id != null) {
                t.setNsEntityId(id.longValue());
            }
        }
    }

    public static interface Copier<T extends NsEntity> {
        public abstract void copy(T src, T dest);
    }

    protected static void copy(NsEntity src, NsEntity dest) {
        if ((src != null) && (dest != null)) {
            // Don't copy fields which don't change after construction.
            dest.setName(src.getName());
            dest.setNsStatus(src.getNsStatus());
            dest.setUpdatedBySystem(src.getUpdatedBySystem());
            dest.setUpdatedByUser(src.getUpdatedByUser());
            dest.setVendorEntityId(src.getVendorEntityId());
        }
    }

    protected static <T extends NsEntity> boolean equals(T lhs, T rhs) {
        return stringsEqual(lhs.prodInstId, rhs.prodInstId) && (lhs.nsEntityId == rhs.nsEntityId);
    }
}
