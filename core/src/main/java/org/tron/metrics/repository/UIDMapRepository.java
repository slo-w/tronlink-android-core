package org.tron.metrics.repository;

import androidx.annotation.NonNull;

import org.tron.common.utils.LogUtils;
import org.tron.metrics.bean.UIdMappingEntity;
import org.tron.metrics.dao.MetricsDatabase;
import org.tron.metrics.dao.UIDMappingDao;

public class UIDMapRepository implements IUIDMapRepository {
    private static final String TAG = "UIdMappingController";

    /**
     * Protocol-level sentinel agreed with the server: when local uid persistence
     * fails, upload uId="Error" so the backend can identify and handle such
     * records. Do not change this literal without coordinating with the server side.
     */
    public static final String UID_PERSIST_ERROR = "Error";

    /**
     * Class-level lock: repository instances share one underlying table, so the
     * check-then-insert below must be serialized across instances. An instance
     * synchronized method cannot stop two instances from generating two different
     * UIDs for the same address.
     */
    private static final Object UID_LOCK = new Object();

    private final UIDMappingDao uidMappingDao;

    public UIDMapRepository() {
        uidMappingDao = MetricsDatabase.getInstance().uidMappingDao();
    }

    @NonNull
    @Override
    public String queryUIDByAddress(String address) {
        synchronized (UID_LOCK) {
            // Re-check inside the lock: an address that already has a mapping must
            // never get a second UID generated for it.
            UIdMappingEntity uIdMappingEntity = uidMappingDao.getByAddress(address);
            if (uIdMappingEntity != null) {
                return uIdMappingEntity.getUId();
            }
            String uuid = newUID();
            return insert(address, uuid) ? uuid : UID_PERSIST_ERROR;
        }
    }

    @Override
    public UIdMappingEntity query(String address) {
        if (address == null) {
            return null;
        }
        return uidMappingDao.getByAddress(address);
    }

    @Override
    public boolean insert(String address, String uId) {
        if (address == null || uId == null) {
            return false;
        }
        synchronized (UID_LOCK) {
            // Update the existing row in place so a duplicate row for the same
            // address is never created.
            UIdMappingEntity uIdMappingEntity = uidMappingDao.getByAddress(address);
            if (uIdMappingEntity != null) {
                uIdMappingEntity.setUId(uId);
                return uidMappingDao.insert(uIdMappingEntity) != -1;
            } else {
                uIdMappingEntity = new UIdMappingEntity();
                uIdMappingEntity.setAddress(address);
                uIdMappingEntity.setUId(uId);
                return uidMappingDao.insert(uIdMappingEntity) != -1;
            }
        }
    }
}
