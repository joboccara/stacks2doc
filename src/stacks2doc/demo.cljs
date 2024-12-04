(ns stacks2doc.demo
  (:require
   [reagent.core :as r]
   [stacks2doc.app :refer [app]]))

(def demo-stack-1 "parse:374, JSONParser (com.fasterxml.jackson.core)
readValue:96, ObjectMapper (com.fasterxml.jackson.databind)
> execute:54, HttpClient (org.apache.http.impl.client)
doExecute:123, HttpRequestExecutor (org.apache.http.protocol)
handle:75, RequestHandler (javax.servlet.http)
doGet:56, Servlet (javax.servlet)
> get:25, PipelineResource (javax.ws.rs.core)")

(def demo-stack-2 "execute:89, ExecutorService (java.util.concurrent)
run:112, FutureTask (java.util.concurrent)
> doPost:45, HttpServlet (javax.servlet.http)
service:90, Servlet (javax.servlet)
handleRequest:210, SpringDispatcher (org.springframework.web)
> handle:60, RestController (org.springframework.web.bind.annotation)
read:52, InputStreamReader (java.io)")

(def demo-stack-3 ">initialize:102, ApplicationContext (org.springframework.context)
createBean:84, BeanFactory (org.springframework.beans.factory)
> execute:45, RestTemplate (org.springframework.web.client)
performRequest:122, OkHttpClient (okhttp3)
call:67, Callable (java.util.concurrent)
> process:50, HttpRequestHandler (javax.ws.rs.core)
close:25, FileInputStream (java.io)")

(defn demo-app []
  ;; Initialize the stack-sources with predefined values
  (let [predefined-stack-sources (r/atom [demo-stack-1 demo-stack-2 demo-stack-3])]
    (fn []
      (app predefined-stack-sources)))) ; Pass pre-defined sources to the app function
