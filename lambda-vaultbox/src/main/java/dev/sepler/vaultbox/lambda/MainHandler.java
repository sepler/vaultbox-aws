package dev.sepler.vaultbox.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import dev.sepler.vaultbox.accessor.S3Accessor;
import dev.sepler.vaultbox.dagger.DaggerAWSComponent;

import dev.sepler.vaultbox.dao.StagingTableDao;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MainHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final S3Accessor s3Accessor;

    private final StagingTableDao stagingTableDao;

    public MainHandler() {
        var component = DaggerAWSComponent.create();
        this.s3Accessor = component.s3Accessor();
        this.stagingTableDao = component.stagingTableDao();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
        log.info("Received request: {}", apiGatewayProxyRequestEvent);
        if ("/getUploadUrl".equals(apiGatewayProxyRequestEvent.getPath())) {
            String id = stagingTableDao.createRecord();
            return new APIGatewayProxyResponseEvent()
                    .withBody(s3Accessor.getStagingUploadUrl(id));
        }
        return new APIGatewayProxyResponseEvent()
                .withBody("pizza mmmmmmmmm");
    }
}
