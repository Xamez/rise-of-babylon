#!/usr/bin/env bash

SERVICES=("notification" "user")
TEMP_PRIV=$(mktemp -u)
TEMP_PUB="${TEMP_PRIV}_pub"

if ! command -v ssh-keygen &> /dev/null; then
    echo "ssh-keygen not found."
    exit 1
fi

ssh-keygen -q -t rsa -b 2048 -m PKCS8 -N "" -f "$TEMP_PRIV"

if [ ! -f "$TEMP_PRIV" ]; then
    echo "Key generation failed."
    exit 1
fi

ssh-keygen -e -m PKCS8 -f "$TEMP_PRIV" > "$TEMP_PUB"

for SERVICE in "${SERVICES[@]}"; do
    TARGET_DIR="$SERVICE/src/main/resources/keys"

    mkdir -p "$TARGET_DIR"
    cp "$TEMP_PRIV" "$TARGET_DIR/private.pem"
    cp "$TEMP_PUB" "$TARGET_DIR/public.pem"

    echo "Keys updated for: $SERVICE"
done

rm -f "$TEMP_PRIV" "$TEMP_PUB" "${TEMP_PRIV}.pub"