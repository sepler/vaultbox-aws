package dev.sepler.vaultbox.dao;

import dev.sepler.vaultbox.dao.model.VaultItem;
import dev.sepler.vaultbox.dao.model.VaultItemStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
public class VaultItemTableDao {

    private final DynamoDbTable<VaultItem> vaultItemTable;

    public VaultItem createVaultItem() {
        VaultItem vaultItem = VaultItem.builder()
                .id(UUID.randomUUID().toString())
                .status(VaultItemStatus.STAGING)
                .build();
        vaultItemTable.putItem(vaultItem);
        log.info("Created VaultItem: {}", vaultItem);
        return vaultItem;
    }
}
