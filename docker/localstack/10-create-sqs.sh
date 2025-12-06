#!/usr/bin/env bash
set -euo pipefail

# Este script se ejecuta automáticamente por LocalStack al estar en /etc/localstack/init/ready.d
# Crea la cola SQS utilizada por la aplicación y muestra la URL resultante.

QUEUE_NAME=${SQS_QUEUE_NAME:-stock-updates}

echo "[init] Creando SQS queue: ${QUEUE_NAME}"
awslocal sqs create-queue --queue-name "${QUEUE_NAME}" >/dev/null
echo "[init] SQS queue creada: $(awslocal sqs get-queue-url --queue-name "${QUEUE_NAME}" --query QueueUrl --output text)"
