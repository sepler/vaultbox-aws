package dev.sepler.vaultbox.model;

import dev.sepler.vaultbox.dao.model.VaultItemStatus;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor
public class GetVaultItemResponse {

    String id;

    String name;

    String status;

    String statusReason;

    long sizeInBytes;

}
