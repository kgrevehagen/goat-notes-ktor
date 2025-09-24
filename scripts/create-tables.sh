#!/bin/bash
TABLE_NAME="Notes"
ENDPOINT="http://localhost:8000"

echo "🔍 Checking if table '$TABLE_NAME' exists..."

export AWS_ACCESS_KEY_ID=dummy
export AWS_SECRET_ACCESS_KEY=dummy

aws dynamodb describe-table \
  --table-name "$TABLE_NAME" \
  --endpoint-url "$ENDPOINT" \
  > /dev/null 2>&1
if [ $? -eq 0 ]; then
  echo "✅ Table '$TABLE_NAME' already exists. Skipping creation."
else
  echo "🚀 Creating table '$TABLE_NAME'..."
  aws dynamodb create-table \
    --table-name "$TABLE_NAME" \
    --attribute-definitions \
        AttributeName=userId,AttributeType=S \
        AttributeName=noteId,AttributeType=S \
        AttributeName=createdAt,AttributeType=N \
    --key-schema \
        AttributeName=userId,KeyType=HASH \
        AttributeName=noteId,KeyType=RANGE \
    --global-secondary-indexes \
        '[
          {
            "IndexName": "userId-createdAt-index",
            "KeySchema": [
              { "AttributeName": "userId", "KeyType": "HASH" },
              { "AttributeName": "createdAt", "KeyType": "RANGE" }
            ],
            "Projection": {
              "ProjectionType": "ALL"
            }
          }
        ]' \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url "$ENDPOINT" \
    --no-cli-pager
  echo "✅ Table created."
fi
