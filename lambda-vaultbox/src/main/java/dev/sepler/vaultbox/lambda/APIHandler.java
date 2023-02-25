package dev.sepler.vaultbox.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;
import dev.sepler.vaultbox.accessor.S3Accessor;
import dev.sepler.vaultbox.dagger.DaggerAWSComponent;
import dev.sepler.vaultbox.dao.VaultItemTableDao;
import dev.sepler.vaultbox.dao.model.GetUploadUrlResponse;
import dev.sepler.vaultbox.dao.model.VaultItem;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class APIHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson GSON = new Gson();

    private final S3Accessor s3Accessor;

    private final VaultItemTableDao vaultItemTableDao;

    public APIHandler() {
        var component = DaggerAWSComponent.create();
        this.s3Accessor = component.s3Accessor();
        this.vaultItemTableDao = component.vaultItemTableDao();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
        log.info("Received request: {}", apiGatewayProxyRequestEvent);
        if ("/getUploadUrl".equals(apiGatewayProxyRequestEvent.getPath())) {
            VaultItem vaultItem = vaultItemTableDao.createVaultItem();
            String uploadUrl = s3Accessor.getStagingUploadUrl(vaultItem.getId());
            GetUploadUrlResponse response = GetUploadUrlResponse.builder()
                    .id(vaultItem.getId())
                    .uploadUrl(uploadUrl)
                    .build();
            return new APIGatewayProxyResponseEvent()
                    .withBody(GSON.toJson(response));
        }
        return new APIGatewayProxyResponseEvent()
                .withBody("pizza mmmmmmmmm");
    }
}
