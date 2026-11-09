#!/usr/bin/env bash
set -euo pipefail

# Executa o Park Adventure a partir de qualquer diretório.
cd "$(dirname "$(readlink -f "$0")")"

if ! command -v java >/dev/null 2>&1; then
  echo "Erro: Java não encontrado. Instale o JDK 21 ou superior." >&2
  exit 1
fi

JAVA_MAJOR="$(java -version 2>&1 | awk -F '[".]' '/version/ { print $2; exit }')"
if [[ -n "${JAVA_MAJOR:-}" && "$JAVA_MAJOR" -lt 21 ]]; then
  echo "Erro: este projeto requer JDK 21 ou superior (encontrado: $JAVA_MAJOR)." >&2
  exit 1
fi

if [[ ! -x ./mvnw ]]; then
  chmod +x ./mvnw
fi

exec ./mvnw -U clean javafx:run "$@"
