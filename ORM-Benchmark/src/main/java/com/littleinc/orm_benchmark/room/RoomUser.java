/*
 * RoomUser.java
 *
 * Description:
 *
 * Author Deng Xinliang
 *
 * Ver 1.0, Mar 01, 2018, Deng Xinliang, Create file
 */
package com.littleinc.orm_benchmark.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import static com.littleinc.orm_benchmark.util.Util.getRandomString;

@Entity(tableName = "user")
public class RoomUser {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public long id;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @ColumnInfo(name = "first_name")
    public String firstName;

    public void fillUserWithRandomData() {
        lastName = getRandomString(10);
        firstName = getRandomString(10);
    }
}
