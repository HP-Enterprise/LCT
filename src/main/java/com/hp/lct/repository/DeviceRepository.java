package com.hp.lct.repository;

import com.hp.lct.entity.Device;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jackl on 2016/11/15.
 */
@Repository
public interface DeviceRepository extends CrudRepository<Device,Long> {
    Device findByImei(String imei);
}
