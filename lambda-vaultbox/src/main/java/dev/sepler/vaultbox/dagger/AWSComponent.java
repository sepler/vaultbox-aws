package dev.sepler.vaultbox.dagger;

import dagger.Component;
import dev.sepler.vaultbox.accessor.S3Accessor;
import dev.sepler.vaultbox.dao.VaultItemDao;

@Component(modules = AWSModule.class)
public interface AWSComponent {

    S3Accessor s3Accessor();

    VaultItemDao vaultItemDao();

}
