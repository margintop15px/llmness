(defproject llmness "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies
  [[org.clojure/clojure "1.12.0"]
   [io.github.zmedelis/bosquet "2024.08.08"]
   [org.clojars.kapil/instructor-clj "0.0.1-alpha.3"]
   [dev.langchain4j/langchain4j-ollama "1.0.0-alpha1"]
   [dev.langchain4j/langchain4j "1.0.0-alpha1"]]

  :repl-options
  {:init-ns llmness.core})
