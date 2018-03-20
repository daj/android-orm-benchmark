/*
 * RoomMessageDao.java
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
import android.arch.persistence.room.Query;

@Dao
public interface RoomMessageDao {
    @Insert
    void insert(RoomMessage[] messages);

    @Query("SELECT * FROM message")
    RoomMessage[] query();

    @Query("SELECT * FROM message WHERE command_id = 5000")
    RoomMessage queryByIndexed();

    @Query("SELECT * FROM message WHERE content LIKE '%a%' LIMIT 100")
    RoomMessage[] search();
}
