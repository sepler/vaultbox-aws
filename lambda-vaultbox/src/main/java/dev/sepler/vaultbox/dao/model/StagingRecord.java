package dev.sepler.vaultbox.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@ToString
@Builder
@AllArgsConstructor
public class StagingRecord {

    private String id;

    public StagingRecord() {
        id = null;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

}
