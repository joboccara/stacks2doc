# stacks2doc
[`stacks2doc`](https://joboccara.github.io/stacks2doc/) transforms multiple call stacks into a diagram. It can be used to help understand, document or debug a codebase.

## How to use?
- Paste one or more call stacks into input fields (only Java call stacks are supported for now).
- Optionally, add a `>` in front of each stack frame that is interesting to you. This will mask all the other frames from the graph. That can be useful if you want to declutter the diagram.
- By default, the diagram will only show the _package graph_, i.e. all the packages that exist in the call stacks.
- `Display class diagram` will display each class in the call stack, grouped by package.
- `Show method calls` will add the method names to each edge. The method names are clickable and will link to the method definition in Github. For this to work, you have to provide a base URL in the "Base URL" field (e.g. `https://github.com/MyOrg/repo/blob/prod/service/src/main/java`) as well as a file extension.

## Example use
Paste each stack into a different stack input.

**Stack 1**
```parse:374, JSONParser (com.fasterxml.jackson.core)
readValue:96, ObjectMapper (com.fasterxml.jackson.databind)
> execute:54, HttpClient (org.apache.http.impl.client)
doExecute:123, HttpRequestExecutor (org.apache.http.protocol)
handle:75, RequestHandler (javax.servlet.http)
doGet:56, Servlet (javax.servlet)
> get:25, PipelineResource (javax.ws.rs.core)
```
**Stack 2**
```
> initialize:102, ApplicationContext (org.springframework.context)
createBean:84, BeanFactory (org.springframework.beans.factory)
> execute:45, RestTemplate (org.springframework.web.client)
performRequest:122, OkHttpClient (okhttp3)
call:67, Callable (java.util.concurrent)
> process:50, HttpRequestHandler (javax.ws.rs.core)
close:25, FileInputStream (java.io)
> handle:60, RestController (org.springframework.annotation)
```
**Stack 3**
```
execute:89, ExecutorService (java.util.concurrent)
run:112, FutureTask (java.util.concurrent)
> doPost:45, HttpServlet (javax.servlet.http)
service:90, Servlet (javax.servlet)
handleRequest:210, SpringDispatcher (org.springframework.web)
> handle:60, RestController (org.springframework.annotation)
read:52, InputStreamReader (java.io)
```

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
