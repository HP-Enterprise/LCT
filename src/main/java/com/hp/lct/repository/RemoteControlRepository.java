package com.hp.lct.repository;

import com.hp.lct.entity.RemoteControl;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jackl on 2016/11/16.
 */
public interface RemoteControlRepository extends CrudRepository<RemoteControl,Long> {

    RemoteControl findBySequenceId(String sequenceId);
}
