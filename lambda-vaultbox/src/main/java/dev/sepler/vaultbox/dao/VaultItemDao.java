package dev.sepler.vaultbox.dao;

import dev.sepler.vaultbox.dao.model.VaultItem;
import dev.sepler.vaultbox.dao.model.VaultItemStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
public class VaultItemDao {

    private final DynamoDbTable<VaultItem> vaultItemTable;

    public VaultItem create() {
        VaultItem vaultItem = VaultItem.builder()
                .id(UUID.randomUUID().toString())
                .status(VaultItemStatus.STAGING)
                .build();
        vaultItemTable.putItem(vaultItem);
        log.info("Created VaultItem: {}", vaultItem);
        return vaultItem;
    }

    public Optional<VaultItem> get(final String id) {
        return Optional.ofNullable(vaultItemTable.getItem(Key.builder()
                .partitionValue(id)
                .build()));
    }

    public void makeInvalid(final String id, final String reason) {

    }

    public void save(final VaultItem vaultItem) {
        log.info("Saving VaultItem: {}", vaultItem);
        vaultItemTable.putItem(vaultItem);
    }

}
