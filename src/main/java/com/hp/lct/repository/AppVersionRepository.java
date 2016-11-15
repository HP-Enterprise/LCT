package com.hp.lct.repository;

import com.hp.lct.entity.AppVersion;
import com.hp.lct.entity.Device;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jackl on 2016/11/15.
 */
@Repository
public interface AppVersionRepository extends CrudRepository<AppVersion,Long> {


  /*  @Query("select av from AppVersion av where av.appId = ?1 and av.version > ?2 order by av.version")
  //  AppVersion findLatestApp(String appId,String version);
*/

    AppVersion findTopByAppIdAndVersionGreaterThanOrderByPublishTimeDesc(String appId,String version);



}
