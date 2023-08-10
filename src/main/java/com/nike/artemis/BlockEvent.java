package com.nike.artemis;

import org.apache.flink.api.common.serialization.SerializationSchema;

public class BlockEvent {
    /**
     * Blockkind:
     * county
     * upmid
     * deviceIp / trueClient IP
     * block time: when does block start
     * block ends: when does block end
     *
     */

    private String blockKind;
    private String entity;
    private Long startTime;
    private Long endTime;
    private String ruleName;

    static class BlockEventSerializationSchema implements SerializationSchema<BlockEvent>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public byte[] serialize(BlockEvent blockEvent)
        {
            return blockEvent.toString().getBytes();
        }
    }

//    public static BlockEventSerializationSchema sinkSerializer()
//    {
//        return new BlockEventSerializationSchema();
//    }

    public BlockEvent(String blockKind, String entity, Long startTime, Long endTime, String ruleName) {
        this.blockKind = blockKind;
        this.entity = entity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.ruleName = ruleName;
    }

    public String getString() {
        return blockKind;
    }

    public void setString(String blockKind) {
        this.blockKind = blockKind;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockEvent that = (BlockEvent) o;

        if (blockKind != null ? !blockKind.equals(that.blockKind) : that.blockKind != null) return false;
        if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        return ruleName != null ? ruleName.equals(that.ruleName) : that.ruleName == null;
    }

    @Override
    public int hashCode() {
        int result = blockKind != null ? blockKind.hashCode() : 0;
        result = 31 * result + (entity != null ? entity.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (ruleName != null ? ruleName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.join(",",blockKind,entity,String.valueOf(startTime),String.valueOf(endTime),ruleName);
    }
}
