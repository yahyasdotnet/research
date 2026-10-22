#!/usr/bin/env bash
set -euo pipefail

if [[ -e .env ]]; then
  printf 'Refusing to overwrite the existing .env file.\n' >&2
  exit 1
fi

if ! command -v openssl >/dev/null 2>&1; then
  printf 'OpenSSL is required to generate secure local secrets.\n' >&2
  exit 1
fi

umask 077
postgres_password=$(openssl rand -hex 32)
jwt_secret=$(openssl rand -base64 32)

cat > .env <<EOF
POSTGRES_USER=research
POSTGRES_PASSWORD=${postgres_password}
POSTGRES_DB=research_db
JWT_SECRET=${jwt_secret}
FIRECRAWL_API_KEY=
OLLAMA_BASE_URL=http://host.docker.internal:11434
OLLAMA_MODEL=llama3.2
EOF

printf 'Created a private .env file with generated database and JWT secrets.\n'
