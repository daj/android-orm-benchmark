/*
 * RoomUserDao.java
 *
 * Description:
 *
 * Author Deng Xinliang
 *
 * Ver 1.0, Mar 13, 2018, Deng Xinliang, Create file
 */
package com.littleinc.orm_benchmark.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

@Dao
public interface RoomUserDao {
    @Insert
    void insert(RoomUser[] users);
}
