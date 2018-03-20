/*
 * RoomMessage.java
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
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.littleinc.orm_benchmark.sqlite.User;
import com.littleinc.orm_benchmark.util.Util;

import java.util.List;

@Entity(tableName = "message", indices = {@Index("command_id")})
public class RoomMessage {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public long id;

    @ColumnInfo(name = "client_id")
    public long clientId;

    @ColumnInfo(name = "command_id")
    public long commandId;

    @ColumnInfo(name = "sorted_by")
    public double sortedBy;

    @ColumnInfo(name = "created_at")
    public int createdAt;

    public String content;

    @ColumnInfo(name = "sender_id")
    public long senderId;

    @ColumnInfo(name = "channel_id")
    public long channelId;

    @Ignore
    public List<User> readers;

    public void fillMessageWithRandomData(int messageNumber, int totalNumber) {
        commandId = messageNumber;
        sortedBy = System.nanoTime();
        content = Util.getRandomString(100);
        clientId = System.currentTimeMillis();
        senderId = Math.round(Math.random() * totalNumber);
        channelId = Math.round(Math.random() * totalNumber);
        createdAt = (int) (System.currentTimeMillis() / 1000L);
    }
}
