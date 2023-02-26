package dev.sepler.vaultbox.model;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor
public class GetDownloadUrlResponse {

    String downloadUrl;

}
