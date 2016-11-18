package com.hp.lct.repository;

import com.hp.lct.entity.Device;
import com.hp.lct.entity.DeviceStatusData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jackl on 2016/11/15.
 */
@Repository
public interface DeviceStatusDataRepository extends CrudRepository<DeviceStatusData,Long> {

    DeviceStatusData findTopByImeiOrderByReceiveTimeDesc(String imei);

}
