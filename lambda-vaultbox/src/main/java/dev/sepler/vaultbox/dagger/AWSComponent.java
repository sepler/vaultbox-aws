package dev.sepler.vaultbox.dagger;

import dagger.Component;
import dev.sepler.vaultbox.accessor.S3Accessor;
import dev.sepler.vaultbox.dao.StagingTableDao;

@Component(modules = AWSModule.class)
public interface AWSComponent {

    S3Accessor s3Accessor();

    StagingTableDao stagingTableDao();

}
