/*
 * Message.java
 *
 * Description:
 *
 * Author Deng Xinliang
 *
 * Ver 1.0, Mar 14, 2018, Deng Xinliang, Create file
 */
package com.littleinc.orm_benchmark.greendao3;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Message {
    @Id(autoincrement = true)
    @Property(nameInDb = "_id")
    public long id;

    @Property(nameInDb = "client_id")
    public long clientId;

    @Index
    @Property(nameInDb = "command_id")
    public long commandId;

    @Property(nameInDb = "sorted_by")
    public double sortedBy;

    @Property(nameInDb = "created_at")
    public int createdAt;

    public String content;

    @Property(nameInDb = "sender_id")
    public long senderId;

    @Property(nameInDb = "channel_id")
    public long channelId;

    @Transient
    public List<User> readers;

    @Generated(hash = 2069970248)
    public Message(long id, long clientId, long commandId, double sortedBy,
            int createdAt, String content, long senderId, long channelId) {
        this.id = id;
        this.clientId = clientId;
        this.commandId = commandId;
        this.sortedBy = sortedBy;
        this.createdAt = createdAt;
        this.content = content;
        this.senderId = senderId;
        this.channelId = channelId;
    }

    @Generated(hash = 637306882)
    public Message() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClientId() {
        return this.clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getCommandId() {
        return this.commandId;
    }

    public void setCommandId(long commandId) {
        this.commandId = commandId;
    }

    public double getSortedBy() {
        return this.sortedBy;
    }

    public void setSortedBy(double sortedBy) {
        this.sortedBy = sortedBy;
    }

    public int getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSenderId() {
        return this.senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getChannelId() {
        return this.channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

}
