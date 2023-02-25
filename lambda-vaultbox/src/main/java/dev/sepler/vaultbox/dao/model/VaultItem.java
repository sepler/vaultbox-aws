package dev.sepler.vaultbox.dao.model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Value
@Builder
@DynamoDbImmutable(builder = VaultItem.VaultItemBuilder.class)
public class VaultItem {

    @Getter(onMethod = @__({@DynamoDbPartitionKey}))
    private final String id;

    private final VaultItemStatus status;

}
