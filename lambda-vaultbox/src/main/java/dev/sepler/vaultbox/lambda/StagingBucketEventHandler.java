package dev.sepler.vaultbox.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.google.gson.Gson;
import dev.sepler.vaultbox.accessor.S3Accessor;
import dev.sepler.vaultbox.dagger.DaggerAWSComponent;
import dev.sepler.vaultbox.dao.VaultItemDao;
import dev.sepler.vaultbox.dao.model.VaultItem;
import dev.sepler.vaultbox.dao.model.VaultItemStatus;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
public class StagingBucketEventHandler implements RequestHandler<S3EventNotification, Void> {

    // 100 MB
    private static final long MAX_SIZE_IN_BYTES = 100 * 1024 * 1024;

    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("attachment; filename=\"(.*\\..*)\"");

    private static final Gson GSON = new Gson();

    private final S3Accessor s3Accessor;

    private final VaultItemDao vaultItemDao;

    public StagingBucketEventHandler() {
        var component = DaggerAWSComponent.create();
        this.s3Accessor = component.s3Accessor();
        this.vaultItemDao = component.vaultItemDao();
    }

    @Override
    public Void handleRequest(final S3EventNotification input, final Context context) {
        log.info("Received event: {}", GSON.toJson(input));
        for (String key : toObjectKeys(input)) {
            Optional<VaultItem> vaultItemOptional = vaultItemDao.get(key);
            if (vaultItemOptional.isEmpty()) {
                log.debug("Could not find VaultItem with ID: {}", key);
                s3Accessor.deletingFromStaging(key);
                continue;
            }
            VaultItem vaultItem = vaultItemOptional.get();

            HeadObjectResponse headObjectResponse = s3Accessor.headStagingObject(key);
            Optional<String> fileNameOptional = getFileName(headObjectResponse.contentDisposition());
            if (fileNameOptional.isEmpty()) {
                log.info("No filename provided for ID: {}", key);
                vaultItemDao.save(vaultItem.withStatus(VaultItemStatus.INVALID)
                        .withStatusReason("No filename provided"));
                s3Accessor.deletingFromStaging(key);
                continue;
            }
            if (headObjectResponse.contentLength() > MAX_SIZE_IN_BYTES) {
                log.info("Exceeded max file size of {} bytes for ID: {}", MAX_SIZE_IN_BYTES, key);
                vaultItemDao.save(vaultItem.withStatus(VaultItemStatus.INVALID)
                        .withStatusReason("Exceeded max file size"));
                s3Accessor.deletingFromStaging(key);
                continue;
            }
            s3Accessor.moveToVault(key);
            vaultItemDao.save(vaultItem.withStatus(VaultItemStatus.IN_VAULT)
                    .withSizeInBytes(headObjectResponse.contentLength())
                    .withName(fileNameOptional.get()));
        }
        return null;
    }

    private List<String> toObjectKeys(final S3EventNotification s3EventNotification) {
        return s3EventNotification.getRecords().stream()
                .map(S3EventNotification.S3EventNotificationRecord::getS3)
                .map(S3EventNotification.S3Entity::getObject)
                .map(S3EventNotification.S3ObjectEntity::getKey)
                .collect(Collectors.toList());
    }

    private Optional<String> getFileName(final String contentDisposition) {
        Matcher matcher = FILE_NAME_PATTERN.matcher(contentDisposition);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }

}
