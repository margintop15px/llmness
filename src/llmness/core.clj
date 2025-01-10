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