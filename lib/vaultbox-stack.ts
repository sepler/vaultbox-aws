import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as s3n from 'aws-cdk-lib/aws-s3-notifications';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as dyanmodb from 'aws-cdk-lib/aws-dynamodb';
import { Duration } from 'aws-cdk-lib';
import { execSync } from 'child_process';
import * as fs from 'fs';

export class VaultBoxStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const vaultBucket = new s3.Bucket(this, 'VaultBucket', {
      encryption: s3.BucketEncryption.S3_MANAGED
    });
    const stagingBucket = new s3.Bucket(this, 'StagingBucket', {
      encryption: s3.BucketEncryption.S3_MANAGED,
      lifecycleRules: [
        {
          expiration: Duration.days(1)
        }
      ]
    });
    
    const vaultItemDdbTable = new dyanmodb.Table(this, 'VaultItemTable', {
      partitionKey: {
        name: 'id',
        type: dyanmodb.AttributeType.STRING
      },
      billingMode: dyanmodb.BillingMode.PAY_PER_REQUEST
    });


    const apiHandler = new lambda.Function(this, 'VaultBoxLambda', {
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
      handler: 'dev.sepler.vaultbox.lambda.APIHandler::handleRequest',
      environment: {
        VAULT_BUCKET: vaultBucket.bucketName,
        STAGING_BUCKET: stagingBucket.bucketName,
        VAULT_ITEM_TABLE: vaultItemDdbTable.tableName
      },
      memorySize: 512,
      timeout: Duration.seconds(10)
    });
    new apigateway.LambdaRestApi(this, 'vaultbox-api', {
      handler: apiHandler,
    });

    const stagingEventHandler = new lambda.Function(this, 'StagingBucketEventHandlerLambda', {
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
      handler: 'dev.sepler.vaultbox.lambda.StagingBucketEventHandler::handleRequest',
      environment: {
        VAULT_BUCKET: vaultBucket.bucketName,
        STAGING_BUCKET: stagingBucket.bucketName,
        VAULT_ITEM_TABLE: vaultItemDdbTable.tableName
      },
      memorySize: 512,
      timeout: Duration.seconds(20)
    });
    stagingBucket.addEventNotification(s3.EventType.OBJECT_CREATED, new s3n.LambdaDestination(stagingEventHandler));

    vaultItemDdbTable.grantFullAccess(apiHandler);
    vaultItemDdbTable.grantFullAccess(stagingEventHandler);
    stagingBucket.grantReadWrite(apiHandler);
    stagingBucket.grantReadWrite(stagingEventHandler);
    vaultBucket.grantReadWrite(apiHandler);
    vaultBucket.grantReadWrite(stagingEventHandler);
  }
}
