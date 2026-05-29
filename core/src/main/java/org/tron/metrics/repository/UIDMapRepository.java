package org.tron.metrics.repository;

import androidx.annotation.NonNull;

import org.tron.common.utils.LogUtils;
import org.tron.metrics.bean.UIdMappingEntity;
import org.tron.metrics.dao.MetricsDatabase;
import org.tron.metrics.dao.UIDMappingDao;

public class UIDMapRepository implements IUIDMapRepository {
    private static final String TAG = "UIdMappingController";
    private final UIDMappingDao uidMappingDao;

    public UIDMapRepository() {
        uidMappingDao = MetricsDatabase.getInstance().uidMappingDao();
    }

    @NonNull
    @Override
    public synchronized String queryUIDByAddress(String address) {
        UIdMappingEntity uIdMappingEntity = uidMappingDao.getByAddress(address);
        if (uIdMappingEntity == null) {
            String uuid = newUID();
            boolean b = insert(address, uuid);
            if (b) {
                return uuid;
            } else {
                // "Error" is a protocol-level sentinel agreed with the server:
                // when local uid persistence fails, upload uId="Error" so the
                // backend can identify and handle such records. Do not change
                // this literal without coordinating with the server side.
                return "Error";
            }
        }
        String uId = uIdMappingEntity.getUId();
        return uId;
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
