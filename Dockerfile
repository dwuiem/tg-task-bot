# Этап сборки
FROM ubuntu:20.04 AS builder
ARG PLUGIN_VERSION
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y curl
RUN mkdir -p /plugins && \
    curl -fsSL \
    -o "/plugins/rabbitmq_delayed_message_exchange-3.12.0.ez" \
    https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.12.0/rabbitmq_delayed_message_exchange-3.12.0.ez

# Финальный этап
FROM rabbitmq:3.12-management-alpine
ARG PLUGIN_VERSION
COPY --from=builder /plugins/rabbitmq_delayed_message_exchange-3.12.0.ez $RABBITMQ_HOME/plugins/
RUN rabbitmq-plugins enable --offline rabbitmq_delayed_message_exchange
