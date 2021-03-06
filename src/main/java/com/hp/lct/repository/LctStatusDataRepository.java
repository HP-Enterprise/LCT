package com.hp.lct.repository;

import com.hp.lct.entity.LctStatusData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jackl on 2016/11/15.
 */
@Repository
public interface LctStatusDataRepository extends CrudRepository<LctStatusData,Long> {

    LctStatusData findTopByImeiOrderByReceiveTimeDesc(String imei);

}
