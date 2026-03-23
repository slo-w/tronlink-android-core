package org.tron.metrics.repository;

import org.tron.metrics.bean.UIdMappingEntity;

import java.util.UUID;

public interface IUIDMapRepository {
    String queryUIDByAddress(String address);

    UIdMappingEntity query(String address);

    boolean insert(String address, String uId);

    default String newUID() {
        return UUID.randomUUID().toString();
    }
}
