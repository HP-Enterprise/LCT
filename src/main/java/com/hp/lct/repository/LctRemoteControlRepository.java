package com.hp.lct.repository;

import com.hp.lct.entity.LctRemoteControl;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jackl on 2016/11/16.
 */
public interface LctRemoteControlRepository extends CrudRepository<LctRemoteControl,Long> {

    LctRemoteControl findBySequenceId(String sequenceId);
}
