package dev.sepler.vaultbox.dagger;

import dagger.Module;
import dagger.Provides;
import dev.sepler.vaultbox.accessor.S3Accessor;
import dev.sepler.vaultbox.dao.VaultItemTableDao;
import dev.sepler.vaultbox.dao.model.VaultItem;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Module
public class AWSModule {

    @Provides
    public S3Accessor provideS3Accessor(final S3Client s3Client, final S3Presigner s3Presigner) {
        return new S3Accessor(s3Client, s3Presigner,
                System.getenv("STAGING_BUCKET"), System.getenv("VAULT_BUCKET"));
    }

    @Provides
    VaultItemTableDao provideVaultItemTableDao(final DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return new VaultItemTableDao(dynamoDbEnhancedClient.table(System.getenv("VAULT_ITEM_TABLE"),
                TableSchema.fromImmutableClass(VaultItem.class)));
    }

    @Provides
    public S3Client provideS3Client() {
        return S3Client.create();
    }

    @Provides
    public S3Presigner provideS3Presigner() {
        return S3Presigner.create();
    }

    @Provides
    public DynamoDbEnhancedClient provideDynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.create();
    }

}
