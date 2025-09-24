FROM amazon/dynamodb-local

USER root
RUN mkdir -p /home/dynamodblocal/data && chown -R dynamodblocal:root /home/dynamodblocal/data
USER dynamodblocal
CMD ["-jar", "DynamoDBLocal.jar", "-sharedDb", "-dbPath", "/home/dynamodblocal/data"]
