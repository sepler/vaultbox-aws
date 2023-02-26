package dev.sepler.vaultbox.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;
import dev.sepler.vaultbox.accessor.S3Accessor;
import dev.sepler.vaultbox.dagger.DaggerAWSComponent;
import dev.sepler.vaultbox.dao.VaultItemDao;
import dev.sepler.vaultbox.model.GetUploadUrlResponse;
import dev.sepler.vaultbox.dao.model.VaultItem;
import dev.sepler.vaultbox.model.GetVaultItemRequest;
import dev.sepler.vaultbox.model.GetVaultItemResponse;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
public class APIHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson GSON = new Gson();

    private final S3Accessor s3Accessor;

    private final VaultItemDao vaultItemDao;

    public APIHandler() {
        var component = DaggerAWSComponent.create();
        this.s3Accessor = component.s3Accessor();
        this.vaultItemDao = component.vaultItemDao();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
        log.info("Received request: {}", apiGatewayProxyRequestEvent);
        if ("/getUploadUrl".equals(apiGatewayProxyRequestEvent.getPath())) {
            VaultItem vaultItem = vaultItemDao.create();
            String uploadUrl = s3Accessor.getStagingUploadUrl(vaultItem.getId());
            GetUploadUrlResponse response = GetUploadUrlResponse.builder()
                    .id(vaultItem.getId())
                    .uploadUrl(uploadUrl)
                    .build();
            return new APIGatewayProxyResponseEvent()
                    .withBody(GSON.toJson(response));
        }
        if ("/getVaultItem".equals(apiGatewayProxyRequestEvent.getPath())) {
            GetVaultItemRequest request = GSON.fromJson(apiGatewayProxyRequestEvent.getBody(), GetVaultItemRequest.class);
            Optional<VaultItem> vaultItemOptional = vaultItemDao.get(request.getId());
            if (vaultItemOptional.isEmpty()) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(404);
            }
            VaultItem vaultItem = vaultItemOptional.get();
            GetVaultItemResponse response = GetVaultItemResponse.builder()
                    .id(vaultItem.getId())
                    .name(vaultItem.getName())
                    .sizeInBytes(vaultItem.getSizeInBytes())
                    .status(vaultItem.getStatus().name())
                    .statusReason(vaultItem.getStatusReason())
                    .build();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(GSON.toJson(response));
        }
        return new APIGatewayProxyResponseEvent()
                .withBody("pizza mmmmmmmmm");
    }
}
