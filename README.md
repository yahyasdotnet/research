# Writing Assistant

A modular tool for writers and researchers to organize ideas, find sources, and improve their writing.

## Demo

Screenshots of the writing, source-search, and full-assist workflows:

### Improve writing

![PenBuddy improve-writing screen](./media/improve-writing.png)

### Find sources

![PenBuddy find-sources screen](./media/find-sources.png)

### Full assist

![PenBuddy full-assist screen](./media/full-assist.png)

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

To run the app and PostgreSQL with Docker Compose, use the clickable tasks in [COMMANDS.md](./COMMANDS.md), or initialize a private local `.env` file and start the stack manually:

```bash
bash scripts/init-local-env.sh
bash ./mvnw -DskipTests package
docker compose up -d --build
```

The setup script generates database and JWT secrets and refuses to overwrite an existing `.env`. Add `FIRECRAWL_API_KEY` there to enable web search through Firecrawl. Compose connects the app to PostgreSQL and publishes it at `http://localhost:3000`. The `.env` file is ignored by Git; keep it private.

- `POST /api/auth/register` with `email` and `password` creates an account and returns a token.
- `POST /api/auth/login` with `email` and `password` returns a token.
- Send the token as `Authorization: Bearer <token>` when calling protected endpoints.

Registration requires a password of 12 to 64 characters and rejects values exceeding BCrypt's 72-byte input limit. Tokens expire after 15 minutes.

## Live writing assistance

The writing assistant searches the web through Firecrawl, checks claims against search sources, and suggests grammar edits using the configured Spring AI chat model. Set `FIRECRAWL_API_KEY` to enable web search; Firecrawl plan limits apply. Make an Ollama server available with the model named by `OLLAMA_MODEL` (default `llama3.2`). With Docker Compose, the app connects to Ollama on the host via `host.docker.internal`; set `OLLAMA_BASE_URL` if yours is elsewhere. The WebSocket endpoint is `/ws/writing-assistant`; connect, send `{"token":"<JWT>"}` as the first message, then send `{"text":"...","action":"ASSIST"}` as the user edits. Supported actions are `ASSIST`, `SEARCH`, `FACT_CHECK`, and `GRAMMAR`. Updates are debounced for one second and stale results are discarded.

REST endpoints require the normal bearer token:

- `POST /api/websearch` with `{"text":"...","action":"SEARCH"}` runs a web search.
- `POST /api/websearch/assist` with `{"text":"...","action":"ASSIST"}` searches and requests writing suggestions.
- `GET /api/websearch/history` returns the signed-in user's saved REST searches.

The live WebSocket does not save drafts or keystrokes. REST searches are stored per account. Text sent for search or AI analysis is sent to the configured third-party providers; do not submit sensitive material.

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
