package dev.sepler.vaultbox.dao.model;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor
public class GetUploadUrlResponse {

    private final String id;

    private final String uploadUrl;

}
