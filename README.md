## Suggested dev setup

Install VS Code

Install the Calva plugin (for REPL, formatting, linting, doc, evaluating Clojure expressions directly in the IDE...)

## Install

Install [leiningen](https://leiningen.org/):

```
brew install leiningen
```

Install dependencies:

```
npm install
```

### To run the tests

```
lein test
```

### To run the webpage

Start the local server

```
npx shadow-cljs watch app
```
Then open http://localhost:3000/ in the browser
### To update Tailwind CSS
```
npx tailwindcss -i ./src/css/tailwind.css -o ./public/css/output.css --watch
```
