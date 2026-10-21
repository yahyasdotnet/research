# Writing Assistant

A modular tool for writers and researchers to organize ideas, find sources, and improve their writing.

## Features

| Feature | Details | Status |
|---|---|---|
| Source search | Search engines, preferred sites, reference generation | In progress |
| Fact-checking | Verify claims and compare sources | Planned |
| Writing support | Suggestions for overcoming writer's block | Planned |
| Grammar & editing | Improve clarity and language quality | Planned |
| Web interface | Simple interface for research and writing workflows | Planned |

## User authentication

User accounts are stored in the configured JPA database. Passwords are hashed with BCrypt, and successful registration or login returns a short-lived JWT bearer token.

Set `JWT_SECRET` to a Base64-encoded secret containing at least 32 random bytes before starting the application. For example, generate one with `openssl rand -base64 32`. Keep the value private and do not commit it.

To run the app and PostgreSQL with Docker Compose, build the application and create a private local `.env` file for the signing secret:

```bash
bash ./mvnw package
umask 077
printf 'JWT_SECRET=%s\n' "$(openssl rand -base64 32)" > .env
sudo docker compose up -d --build
```

Compose connects the app to PostgreSQL and publishes it at `http://localhost:3000`. The `.env` file is ignored by Git; keep it private.

- `POST /api/auth/register` with `email` and `password` creates an account and returns a token.
- `POST /api/auth/login` with `email` and `password` returns a token.
- Send the token as `Authorization: Bearer <token>` when calling protected endpoints.

Registration requires a password of 12 to 64 characters and rejects values exceeding BCrypt's 72-byte input limit. Tokens expire after 15 minutes.

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 350 110" width="100%" height="100%">
  <defs>
    <linearGradient id="cyberGreen" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#10b981" />
      <stop offset="50%" stop-color="#34d399" />
      <stop offset="100%" stop-color="#06b6d4" />
    </linearGradient>
  </defs>

  <text x="15" y="72" font-family="system-ui, -apple-system, sans-serif" font-size="52" font-weight="800" fill="#ffffff" letter-spacing="-1">
    Pen<tspan fill="url(#cyberGreen)">Buddy</tspan>
  </text>
</svg>
