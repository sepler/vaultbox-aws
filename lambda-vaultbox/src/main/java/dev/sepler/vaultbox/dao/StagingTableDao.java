package dev.sepler.vaultbox.dao;

import dev.sepler.vaultbox.dao.model.StagingRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
public class StagingTableDao {

    private final DynamoDbTable<StagingRecord> stagingTable;

    public String createRecord() {
        StagingRecord stagingRecord = StagingRecord.builder()
                .id(UUID.randomUUID().toString())
                .build();
        stagingTable.putItem(stagingRecord);
        log.info("Created StagingRecord: {}", stagingRecord);
        return stagingRecord.getId();
    }
}
