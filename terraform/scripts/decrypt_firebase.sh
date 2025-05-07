#!/bin/bash

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

# Check if encrypted file exists
if [ ! -f "$ENCRYPTED_FILE" ]; then
    echo "Encrypted Firebase service account file not found at $ENCRYPTED_FILE"
    exit 1
fi

# Decrypt the file using AWS KMS
echo "Decrypting Firebase service account file..."
aws kms decrypt \
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
