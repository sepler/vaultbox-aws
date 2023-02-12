import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as apigateway from 'aws-cdk-lib/aws-apigateway'
import { execSync } from 'child_process';
import * as fs from 'fs';

export class VaultBoxStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const bucket = new s3.Bucket(this, 'vault');
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
        VAULT_BUCKET: bucket.bucketName
      }
    });

    new apigateway.LambdaRestApi(this, 'vaultbox-api', {
      handler: handler
    });
  }
}
