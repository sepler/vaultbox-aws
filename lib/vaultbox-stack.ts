import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as dyanmodb from 'aws-cdk-lib/aws-dynamodb';
import { execSync } from 'child_process';
import * as fs from 'fs';
import { Duration } from 'aws-cdk-lib';

export class VaultBoxStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const bucketVault = new s3.Bucket(this, 'vault', {
      encryption: s3.BucketEncryption.S3_MANAGED
    });
    const bucketStaging = new s3.Bucket(this, 'staging', {
      encryption: s3.BucketEncryption.S3_MANAGED
    });
    
    const stagingDdbTable = new dyanmodb.Table(this, 'StagingTable', {
      partitionKey: {
        name: 'id',
        type: dyanmodb.AttributeType.STRING
      },
      billingMode: dyanmodb.BillingMode.PAY_PER_REQUEST
    });


    const handler = new lambda.Function(this, 'VaultBoxLambda', {
      runtime: lambda.Runtime.JAVA_11,
      code: lambda.Code.fromAsset('./lambda-vaultbox', {
        bundling: {
          image: lambda.Runtime.JAVA_11.bundlingImage,
          command: [],
          local: {
            tryBundle(outputDir: string) {
              try {
                execSync('mvn --version');
              } catch {
                return false;
              }
              fs.copyFileSync('./lambda-vaultbox/target/vaultbox.jar', outputDir + '/vaultbox.jar');
              return true;
            }
          }
        }
      }),
      handler: 'dev.sepler.vaultbox.lambda.MainHandler::handleRequest',
      environment: {
        VAULT_BUCKET: bucketVault.bucketName,
        STAGING_BUCKET: bucketStaging.bucketName,
        STAGING_TABLE: stagingDdbTable.tableName
      },
      memorySize: 512,
      timeout: Duration.seconds(10)
    });
    stagingDdbTable.grantFullAccess(handler);
    bucketStaging.grantReadWrite(handler);
    new apigateway.LambdaRestApi(this, 'vaultbox-api', {
      handler: handler,
    });

  }
}
