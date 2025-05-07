#!/bin/bash
set -e

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "AWS CLI is not installed"
    exit 1
fi

# Path to the encrypted file
ENCRYPTED_FILE="/app/config/firebase-service-account.json.encrypted"

# Path to save the decrypted file
DECRYPTED_FILE="/app/config/firebase-service-account.json"

# KMS key ID - Replace with your actual KMS key ID
KMS_KEY_ID="alias/lovekeeper-kms-key"

# Check if environment variables exist
if [ -z "$AWS_ACCESS_KEY" ] || [ -z "$AWS_SECRET_KEY" ]; then
    echo "AWS credentials not found in environment variables. Trying instance role..."
else
    # Configure AWS CLI with provided credentials
    aws configure set aws_access_key_id "$AWS_ACCESS_KEY"
    aws configure set aws_secret_access_key "$AWS_SECRET_KEY"
    aws configure set region "ap-northeast-2"
    echo "AWS credentials configured from environment variables."
fi

# Check if encrypted file exists
if [ ! -f "$ENCRYPTED_FILE" ]; then
    echo "Encrypted Firebase service account file not found at $ENCRYPTED_FILE"
    echo "Checking if we can get it from AWS Secrets Manager..."
    
    # Try to get the file content from Secrets Manager
    if [ -n "$SPRING_PROFILES_ACTIVE" ]; then
        echo "Getting Firebase credentials from Secrets Manager for profile: $SPRING_PROFILES_ACTIVE"
        aws secretsmanager get-secret-value \
            --secret-id "${SPRING_PROFILES_ACTIVE}/lovekeeper/firebase-credentials" \
            --query SecretString \
            --output text > $DECRYPTED_FILE
        
        if [ $? -eq 0 ]; then
            echo "Successfully retrieved Firebase credentials from Secrets Manager"
            chmod 600 $DECRYPTED_FILE
            echo "Set proper permissions for Firebase service account file"
            
            # Create symbolic link if needed
            ln -sf $DECRYPTED_FILE /app/firebase-service-account.json
            echo "Firebase service account setup complete"
            exit 0
        else
            echo "Failed to retrieve Firebase credentials from Secrets Manager"
            exit 1
        fi
    else
        echo "No active profile set, cannot determine which Secret to fetch"
        exit 1
    fi
fi

# Decrypt the file using AWS KMS
echo "Decrypting Firebase service account file..."
aws kms decrypt \
    --key-id $KMS_KEY_ID \
    --ciphertext-blob fileb://$ENCRYPTED_FILE \
    --output text \
    --query Plaintext \
    --region ap-northeast-2 \
    | base64 --decode > $DECRYPTED_FILE

# Check if decryption was successful
if [ $? -eq 0 ]; then
    echo "Successfully decrypted Firebase service account file to $DECRYPTED_FILE"
    chmod 600 $DECRYPTED_FILE
    echo "Set proper permissions for Firebase service account file"
else
    echo "Failed to decrypt Firebase service account file"
    exit 1
fi

# Create a symbolic link to the decrypted file if needed
ln -sf $DECRYPTED_FILE /app/firebase-service-account.json

echo "Firebase service account setup complete"
exit 0
