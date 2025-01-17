(ns llmness.core
  (:require
   [bosquet.env :as env]
   [bosquet.llm.generator :refer [generate llm fun]]
   [bosquet.llm.wkk :as wkk])
  (:import
   [dev.langchain4j.model.ollama OllamaChatModel]))


(:ollama env/config)

(llm :ollama)

(comment
 (-> (generate
      {:question-answer "Question: {{question}}  Answer: {{answer}}"
       :answer          (llm :ollama wkk/model-params {:model "llama3.2:3b"})
       :self-eval       ["{{question-answer}}"
                         ""
                         "Is this a correct answer? {{test}}"]
       :test            (llm :ollama wkk/model-params {:model "zephyr"})}
      {:question "What is the distance from Moon to Io?"})
     :bosquet/completions)



 (generate
  {:repeat   "{{repeat-x}}: {{repeater}}"
   :number   (fun (fn [n] (rand-int n)) ['n])
   :repeat-x "Repeat 'X' {{n}} times:"
   :repeater (llm :ollama wkk/model-params {:model "llama3.2:3b"})}
  {:n 5}))



(def model
  (-> (OllamaChatModel/builder)
      (.baseUrl "http://localhost:11434")
      (.modelName "llama3.2:3b")
      (.build)))

(comment
 (.generate model "Как погода?"))

(def auth
  "Authorization
   How to build the authorization header when sending HTTP calls to Bob.
   When you send the HTTP requests to call Bob’s API, you need to include an authorization header which includes the following:
   Service user credentials: The API service user is how you can access Bob via the API. You must include in the header the service user’s ID and Token.
   The authentication method must be basic access authentication.
   How to build the authorization header
   First, follow the instructions in API Service Users to create a service user and assign the required permissions. Once you have the service user's credentials, you can build the authorization header:
   Form the credentials: Combine your service user ID and token into a single string, separated by a colon (:).
   SERVICE-USER-ID:TOKEN
   Encode: Encode this combined string using Base64 encoding. The resulting string is your credentials in Base64 format.
   Base64.encode(SERVICE-USER-ID:Qe8q89RwbzeS7mmhMcAsN1crM73m6MdbjewGCCUY)
   Note: This sample is not specific to a single programming language.
   It represents a conceptual example of encoding a string using Base64 encoding.
   Set the Authorization Header: Include an HTTP header field in your request in the form of:
   authorization: Basic <Base64-encoded credentials>
   Note: The method to perform Base64 encoding varies between programming languages. Consult the documentation for your specific language or ask your development team for details on how to encode the credentials.")


(def jumper-example
  "{:continue?
   [{:entry-point? true
     :fn           (fn [_ _ flow]
                     (let [{:keys [people-ids next-ids]} flow]
                       (if next-ids
                         (< 1 (count next-ids))
                         (some? (not-empty people-ids)))))
     :id           :condition
     :results?     true
     :type         :custom}]
   :steps
   [{:entry-point? true
     :id           :main-switch
     :next         (fn [_ _ flow]
                     (if (:people-ids flow) :next-ids :get-people))
     :type         :switch}
    {:fn   (fn [_ _ flow]
             (let [ids (or (:next-ids flow) (:people-ids flow))] (rest ids)))
     :id   :next-ids
     :next :get-lifecycles
     :type :custom}
    {:id     :get-people
     :next   :people-ids
     :params (fn parameters-people [job-config _ _]
               {:url          \"https://api.hibob.com/v1/people\"
                :method       :get
                :content-type :json
                :query-params {:showInactive true}
                :headers      {\"Authorization\" (get-in job-config
                                                       [:source :api-key])}})
     :type   :http-request}
    {:fn   (fn [_ _ flow-data]
             (map :id (get-in flow-data [:get-people :employees])))
     :id   :people-ids
     :next :get-lifecycles
     :type :custom}
    {:id     :get-lifecycles
     :next   :results
     :params (fn [job-config _ flow]
               (let [{:keys [next-ids people-ids]} flow
                     next-id (or (first next-ids) (first people-ids))
                     api-key (get-in job-config [:source :api-key])]
                 {:url          (format \"https://api.hibob.com/v1/people/%s/lifecycle\"
                                        next-id)
                  :method       :get
                  :content-type :json
                  :headers      {\"Authorization\" api-key}}))
     :type   :http-request}
    {:fn       (fn [_ _ flow]
                 (let [current-id (first (or (:next-ids flow) (:people-ids flow)))]
                   (map (fn [data] (assoc data :employeeId current-id))
                        (get-in flow [:get-lifecycles :values]))))
     :id       :results
     :results? true
     :type     :custom}]}")


(generate
 {:auth-docs      auth
  :jumper-example jumper-example
  :question       "Given the documentation about HTTP API authorization: {{auth-docs}}.
              Generate a Python HTTP request with API token being: abc.
              Return only a snippet of the code that sets the Authorization header and invokes the API request.
              Answer: {{answer}}"
  :answer         (llm :ollama wkk/model-params {:model "llama3.2:3b"})})