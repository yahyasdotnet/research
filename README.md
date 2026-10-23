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

## System Architecture

```mermaid


flowchart TB
    U(["👤 User"])

    subgraph FRONTEND["PRESENTATION LAYER"]
        direction TB
        FE["🖥️ Web Interface<br/>Writing Editor · Research UI"]
    end

    subgraph BACKEND["APPLICATION LAYER"]
        direction TB
        BE["⚙️ Spring Boot<br/>REST API · WebSocket · JWT Authentication"]
        AI["✨ Writing Assistant<br/>Spring AI · Search · Fact-Checking · Grammar"]
        BE --> AI
    end

    subgraph SERVICES["DATA & AI LAYER"]
        direction LR
        DB[("🐘 PostgreSQL")]
        LLM["🤖 Ollama<br/>LLM or an API (Claude, Gemini etc)"]
        WEB["🔎 Firecrawl<br/>Web Search"]
    end

    U <--> FE
    FE <--> BE
    AI <--> DB
    AI <--> LLM
    AI <--> WEB

    classDef frontend fill:#172554,stroke:#60a5fa,color:#eff6ff,stroke-width:2px
    classDef backend fill:#14532d,stroke:#4ade80,color:#f0fdf4,stroke-width:2px
    classDef services fill:#422006,stroke:#fbbf24,color:#fffbeb,stroke-width:2px
    classDef user fill:#334155,stroke:#cbd5e1,color:#ffffff,stroke-width:2px

    class FE frontend
    class BE,AI backend
    class DB,LLM,WEB services
    class U user

    style FRONTEND fill:#0f172a,stroke:#3b82f6,stroke-width:1px,color:#93c5fd
    style BACKEND fill:#052e16,stroke:#22c55e,stroke-width:1px,color:#86efac
    style SERVICES fill:#1c1917,stroke:#f59e0b,stroke-width:1px,color:#fcd34d
