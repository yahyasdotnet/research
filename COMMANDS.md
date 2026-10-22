# Project Commands

Open this file in VS Code's Markdown Preview (`Ctrl+Shift+V` / `Cmd+Shift+V`) and click a task link to run it. VS Code may ask you to trust the workspace or approve a task the first time. You can also run any task from **Terminal → Run Task**.

## First-time setup

1. Create a private `.env` file and generate fresh local database/JWT secrets:

   [Initialize Local Environment](command:workbench.action.tasks.runTask?%5B%22Initialize%20Local%20Environment%22%5D)

   This task will not overwrite an existing `.env`. Add a Firecrawl API key as `FIRECRAWL_API_KEY` in `.env` to enable web search.

2. Install or start [Ollama](https://ollama.com/) on the host, then download the default model:

   [Prepare Ollama Model (`llama3.2`)](command:workbench.action.tasks.runTask?%5B%22Prepare%20Ollama%20Model%22%5D)

   To choose another model or Ollama server, update `OLLAMA_MODEL` and `OLLAMA_BASE_URL` in `.env`.

3. Validate the Docker Compose configuration:

   [Validate Compose Configuration](command:workbench.action.tasks.runTask?%5B%22Validate%20Compose%20Configuration%22%5D)

## Build and test

- [Compile without running tests](command:workbench.action.tasks.runTask?%5B%22Maven%20Compile%22%5D)
- [Run Maven tests](command:workbench.action.tasks.runTask?%5B%22Maven%20Tests%22%5D)
- [Package the application](command:workbench.action.tasks.runTask?%5B%22Maven%20Package%22%5D)
- [Build the Docker image JAR without tests](command:workbench.action.tasks.runTask?%5B%22Build%20Container%20Jar%22%5D)

The test task uses the repository's test suite. The Spring context test needs a working datasource; run the Docker stack first if your local test configuration does not provide one.

## Run with Docker Compose

- [Build and start the app and PostgreSQL](command:workbench.action.tasks.runTask?%5B%22Start%20Project%22%5D) (builds the app JAR first)
- [Follow application logs](command:workbench.action.tasks.runTask?%5B%22View%20Project%20Logs%22%5D)
- [Stop the app and database](command:workbench.action.tasks.runTask?%5B%22Stop%20Project%22%5D)

The app is published at <http://localhost:3000>. Stopping the stack preserves the database container; do not use `docker compose down` unless you intend to remove the containers.
