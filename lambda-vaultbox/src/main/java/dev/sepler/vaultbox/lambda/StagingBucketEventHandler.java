package dev.sepler.vaultbox.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class StagingBucketEventHandler implements RequestHandler<S3EventNotification, Void> {
    @Override
    public Void handleRequest(final S3EventNotification input, final Context context) {
        log.info("Received event: {}", input);
        return null;
    }
}
