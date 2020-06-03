# Searchy
A simple search engine.


## Instructions to run the engine
### 1. Filling the database.
First of all you need to fill the database with the tables created by our crawler, indexer, and ranker classes.
Connect to your SQL database and run the scripts in the /Database directory in the following order:
1.  script.sql (creates the tables)
2.  pages.sql
3.  ranks.sql, terms.sql, and suggestions.sql (in whatever order)
---
### 2. Running the server.
The server code is in the /src/connection/Launcher class. Run it to start the server locally on port 4000.

---
### 3. Running the interface.
To run the interface you have to have nodeJS installed on your machine.
Navigate to the /WebInterface directory, open a terminal and run the following commands: 
Run this once at the beginning.
```bash
$ npm install
```
Run this whenever you want to start the interface.
```bash
$ npm start
```

## The software design
[![](https://mermaid.ink/img/eyJjb2RlIjoiZ3JhcGggTFJcblxuXHRBW0NyYXdsZXJdIC0tPiBFe0RhdGFiYXNlfVxuXHRCW0ludmVydGVkSW5kZXhdIC0tPiBFXG4gIENbUGFnZVJhbmtdICAtLT4gRSAtLT4gRFxuICBEW1F1ZXJ5IEVuZ2luZV1cbiAgRCAtLT4gTGF1bmNoZXIgLS0-fFJFU1QgQVBJfEYoKEludGVyZmFjZSkpXG5cdFx0XHRcdFx0IiwibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQifSwidXBkYXRlRWRpdG9yIjpmYWxzZX0)](https://mermaid-js.github.io/mermaid-live-editor/#/edit/eyJjb2RlIjoiZ3JhcGggTFJcblxuXHRBW0NyYXdsZXJdIC0tPiBFe0RhdGFiYXNlfVxuXHRCW0ludmVydGVkSW5kZXhdIC0tPiBFXG4gIENbUGFnZVJhbmtdICAtLT4gRSAtLT4gRFxuICBEW1F1ZXJ5IEVuZ2luZV1cbiAgRCAtLT4gTGF1bmNoZXIgLS0-fFJFU1QgQVBJfEYoKEludGVyZmFjZSkpXG5cdFx0XHRcdFx0IiwibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQifSwidXBkYXRlRWRpdG9yIjpmYWxzZX0)