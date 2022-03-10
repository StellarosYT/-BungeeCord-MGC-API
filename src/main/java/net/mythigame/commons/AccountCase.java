package net.mythigame.commons;

import java.util.UUID;

public class AccountCase implements Cloneable{

    private String id;
    private UUID uuid;
    private String type;
    private String reason;
    private long end_time;
    private UUID uuid_moderator;
    private UUID cancel_moderator;
    private String cancel_reason;

    public AccountCase(){}

    public AccountCase(String id, UUID uuid, String type, String reason, long end_time, UUID uuid_moderator, UUID cancel_moderator, String cancel_reason) {
        this.id = id;
        this.uuid = uuid;
        this.type = type;
        this.reason = reason;
        this.end_time = end_time;
        this.uuid_moderator = uuid_moderator;
        this.cancel_moderator = cancel_moderator;
        this.cancel_reason = cancel_reason;
    }

    public AccountCase(String id, UUID uuid, String type, String reason, long end_time, UUID uuid_moderator) {
        this.id = id;
        this.uuid = uuid;
        this.type = type;
        this.reason = reason;
        this.end_time = end_time;
        this.uuid_moderator = uuid_moderator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public UUID getUuid_moderator() {
        return uuid_moderator;
    }

    public void setUuid_moderator(UUID uuid_moderator) {
        this.uuid_moderator = uuid_moderator;
    }

    public UUID getCancel_moderator() {
        return cancel_moderator;
    }

    public void setCancel_moderator(UUID cancel_moderator) {
        this.cancel_moderator = cancel_moderator;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public void setCancel_reason(String cancel_reason) {
        this.cancel_reason = cancel_reason;
    }

    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(!(o instanceof AccountCase)){
            return false;
        } else {
            return ((AccountCase)o).getId().equals(this.id);
        }
    }

    public AccountCase clone(){
        try {
            return (AccountCase) super.clone();
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }

        return null;
    }

}
