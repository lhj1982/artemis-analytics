package com.nike.artemis.model.block;


import org.apache.flink.api.common.serialization.SerializationSchema;

import java.util.Objects;

public class Block {
    private String blockProducer;
    private String userType;
    private String user;
    private String disposalDecision;
    private String duration;
    private String destination;
    private String nameSpace;
    private String ttl;
    private Long blockTime;

    public Block() {
    }

    static class BlockSerializationSchema implements SerializationSchema<Block> {
        private static final long serialVersionUID = 1L;

        @Override
        public byte[] serialize(Block block) {
            return block.toString().getBytes();
        }
    }

    public static BlockSerializationSchema sinkSerializer() {
        return new BlockSerializationSchema();
    }

    public Block(String blockProducer, String userType, String user, String disposalDecision, String duration,
                 String destination, String nameSpace, String ttl, Long blockTime) {
        this.blockProducer = blockProducer;
        this.userType = userType;
        this.user = user;
        this.disposalDecision = disposalDecision;
        this.duration = duration;
        this.destination = destination;
        this.nameSpace = nameSpace;
        this.ttl = ttl;
        this.blockTime = blockTime;
    }

    public String getBlockProducer() {
        return blockProducer;
    }

    public void setBlockProducer(String blockProducer) {
        this.blockProducer = blockProducer;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDisposalDecision() {
        return disposalDecision;
    }

    public void setDisposalDecision(String disposalDecision) {
        this.disposalDecision = disposalDecision;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public Long getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(Long blockTime) {
        this.blockTime = blockTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Block block = (Block) o;

        if (blockProducer != null ? !blockProducer.equals(block.blockProducer) : block.blockProducer != null)
            return false;
        if (userType != null ? !userType.equals(block.userType) : block.userType != null) return false;
        if (user != null ? !user.equals(block.user) : block.user != null) return false;
        if (disposalDecision != null ? !disposalDecision.equals(block.disposalDecision) : block.disposalDecision != null)
            return false;
        if (duration != null ? !duration.equals(block.duration) : block.duration != null) return false;
        if (destination != null ? !destination.equals(block.destination) : block.destination != null) return false;
        if (ttl != null ? !ttl.equals(block.ttl) : block.ttl != null) return false;
        if (!Objects.equals(blockTime, block.blockTime)) return false;
        return nameSpace != null ? nameSpace.equals(block.nameSpace) : block.nameSpace == null;
    }

    @Override
    public int hashCode() {
        int result = blockProducer != null ? blockProducer.hashCode() : 0;
        result = 31 * result + (userType != null ? userType.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (disposalDecision != null ? disposalDecision.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (nameSpace != null ? nameSpace.hashCode() : 0);
        result = 31 * result + (ttl != null ? ttl.hashCode() : 0);
        result = 31 * result + (blockTime != null ? blockTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "  \"ruleId\":\"" + blockProducer + '\"' +
                ", \"userType\":\"" + userType + '\"' +
                ", \"user\":\"" + user + '\"' +
                ", \"disposalDecision\":\"" + disposalDecision + '\"' +
                ", \"duration\":\"" + duration + '\"' +
                ", \"destination\":\"" + destination + '\"' +
                ", \"nameSpace\":\"" + nameSpace + '\"' +
                ", \"expirationTtl\":\"" + ttl + '\"' +
                ", \"blockTime\":\"" + blockTime + '\"' +
                '}';
    }
}
